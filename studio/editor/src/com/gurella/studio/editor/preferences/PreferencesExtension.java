package com.gurella.studio.editor.preferences;

import com.gurella.engine.plugin.Plugin;

public interface PreferencesExtension extends Plugin {
	void setPreferencesStore(PreferencesStore preferencesStore);
}
