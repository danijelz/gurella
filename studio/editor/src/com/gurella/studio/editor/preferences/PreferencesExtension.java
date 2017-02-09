package com.gurella.studio.editor.preferences;

import com.gurella.engine.utils.plugin.Plugin;

public interface PreferencesExtension extends Plugin {
	void setPreferencesStore(PreferencesStore preferencesStore);
}
