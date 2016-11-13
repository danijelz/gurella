package com.gurella.studio.editor.preferences;

public interface PreferencesStore {
	PreferencesNode projectNode();

	PreferencesNode resourceNode();

	PreferencesNode sceneNode();
}
