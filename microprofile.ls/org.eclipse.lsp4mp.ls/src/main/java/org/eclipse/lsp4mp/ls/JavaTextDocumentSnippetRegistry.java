/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4mp.ls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4mp.ls.JavaTextDocuments.JavaTextDocument;
import org.eclipse.lsp4mp.ls.commons.BadLocationException;
import org.eclipse.lsp4mp.ls.commons.snippets.ISnippetContext;
import org.eclipse.lsp4mp.ls.commons.snippets.Snippet;
import org.eclipse.lsp4mp.ls.commons.snippets.TextDocumentSnippetRegistry;
import org.eclipse.lsp4mp.snippets.LanguageId;
import org.eclipse.lsp4mp.snippets.SnippetContextForJava;
import org.eclipse.lsp4mp.utils.StringUtils;

/**
 * Java snippet registry.
 *
 * @author Angelo ZERR
 *
 */
public class JavaTextDocumentSnippetRegistry extends TextDocumentSnippetRegistry {

	private static final String PACKAGENAME_KEY = "packagename";
	private List<String> types;

	public JavaTextDocumentSnippetRegistry() {
		this(true);
	}

	public JavaTextDocumentSnippetRegistry(boolean loadDefault) {
		super(LanguageId.java.name(), loadDefault);
	}

	/**
	 * Returns the all distinct types declared in context/type of each snippet.
	 *
	 * @return the all distinct types declared in context/type of each snippet.
	 */
	public List<String> getTypes() {
		if (types != null) {
			return types;
		}
		types = collectTypes();
		return types;
	}

	private synchronized List<String> collectTypes() {
		if (types != null) {
			return types;
		}
		List<String> types = new ArrayList<>();
		for (Snippet snippet : getSnippets()) {
			if (snippet.getContext() != null && snippet.getContext() instanceof SnippetContextForJava) {
				List<String> snippetTypes = ((SnippetContextForJava) snippet.getContext()).getTypes();
				if (snippetTypes != null) {
					for (String snippetType : snippetTypes) {
						if (!types.contains(snippetType)) {
							types.add(snippetType);
						}
					}
				}
			}
		}
		return types;
	}

	public List<CompletionItem> getCompletionItems(JavaTextDocument document, int completionOffset,
			boolean canSupportMarkdown, boolean snippetsSupported,
			BiPredicate<ISnippetContext<?>, Map<String, String>> contextFilter) {
		Map<String, String> model = new HashMap<>();
		// fill package name to replace in the snippets
		String packageStatement = "";
		if (!StringUtils.isEmpty(document.getPackageName())) {
			String lineDelimiter = System.lineSeparator();
			try {
				lineDelimiter = document.lineDelimiter(0);
			} catch (BadLocationException e) {
			}
			packageStatement = new StringBuilder("package ")//
					.append(document.getPackageName()) //
					.append(";") //
					.append(lineDelimiter) //
					.append(lineDelimiter) //
					.toString();
		}
		model.put(PACKAGENAME_KEY, packageStatement);
		return super.getCompletionItems(document, completionOffset, canSupportMarkdown, snippetsSupported,
				contextFilter, model);
	}
}
