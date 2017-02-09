package com.gurella.studio.editor.preferences;

import java.util.HashSet;
import java.util.Set;

import com.gurella.engine.utils.plugin.Plugin;
import com.gurella.engine.utils.plugin.PluginListener;

public class PreferencesExtensionRegistry implements PluginListener {
	private final PreferencesStore preferencesStore;
	private final Set<PreferencesExtension> extensions = new HashSet<>();

	PreferencesExtensionRegistry(PreferencesStore preferencesStore) {
		this.preferencesStore = preferencesStore;
	}

	@Override
	public void activated(Plugin plugin) {
		if (plugin instanceof PreferencesExtension) {
			PreferencesExtension exstension = (PreferencesExtension) plugin;
			if (extensions.add(exstension)) {
				exstension.setPreferencesStore(preferencesStore);
			}
		}
	}

	@Override
	public void deactivated(Plugin plugin) {
		if (plugin instanceof PreferencesExtension) {
			PreferencesExtension exstension = (PreferencesExtension) plugin;
			if (extensions.remove(exstension)) {
				exstension.setPreferencesStore(null);
			}
		}
	}
}
