package com.gurella.studio.editor.preferences;

import java.util.HashSet;
import java.util.Set;

import com.gurella.engine.plugin.Plugin;
import com.gurella.engine.plugin.PluginListener;

public class PreferencesProviderExtensionRegistry implements PluginListener {
	private final PreferencesProvider preferencesProvider;
	private final Set<PreferencesProviderExtension> extensions = new HashSet<>();

	PreferencesProviderExtensionRegistry(PreferencesProvider preferencesProvider) {
		this.preferencesProvider = preferencesProvider;
	}

	@Override
	public void activated(Plugin plugin) {
		if (plugin instanceof PreferencesProviderExtension) {
			PreferencesProviderExtension exstension = (PreferencesProviderExtension) plugin;
			if (extensions.add(exstension)) {
				exstension.setPreferencesProvider(preferencesProvider);
			}
		}
	}

	@Override
	public void deactivated(Plugin plugin) {
		if (plugin instanceof PreferencesProviderExtension) {
			PreferencesProviderExtension exstension = (PreferencesProviderExtension) plugin;
			if (extensions.remove(exstension)) {
				exstension.setPreferencesProvider(null);
			}
		}
	}
}
