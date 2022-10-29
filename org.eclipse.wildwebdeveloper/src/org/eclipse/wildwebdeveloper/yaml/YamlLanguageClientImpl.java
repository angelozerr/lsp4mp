/*******************************************************************************
 * Copyright (c) 2019 Red Hat Inc. and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.wildwebdeveloper.yaml;

import static org.eclipse.wildwebdeveloper.yaml.ui.preferences.YAMLPreferenceServerConstants.isMatchYamlSection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4e.LanguageClientImpl;
import org.eclipse.lsp4j.ConfigurationItem;
import org.eclipse.lsp4j.ConfigurationParams;
import org.eclipse.wildwebdeveloper.ui.preferences.Settings;
import org.eclipse.wildwebdeveloper.yaml.ui.preferences.YAMLPreferenceServerConstants;

public class YamlLanguageClientImpl extends LanguageClientImpl {

	@Override
	public CompletableFuture<List<Object>> configuration(ConfigurationParams params) {
		return CompletableFuture.supplyAsync(() -> {
			List<Object> settings = new ArrayList<>();
			for (ConfigurationItem item : params.getItems()) {
				String section = item.getSection();
				if (isMatchYamlSection(section)) {
					// 'yaml' section, returns the yaml settings
					Settings yamlSettings = YAMLPreferenceServerConstants.getGlobalSettings();
					settings.add(yamlSettings.findSettings(section.split("[.]")));
				} else {
					// Unkwown section
					settings.add(null);
				}
			}
			return settings;
		});
	}

}
