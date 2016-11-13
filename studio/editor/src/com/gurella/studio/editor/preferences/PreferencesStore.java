package com.gurella.studio.editor.preferences;

import java.util.Optional;

public interface PreferencesStore {
	PreferencesNode projectNode();

	PreferencesNode resourceNode();

	Optional<PreferencesNode> sceneNode();
}
