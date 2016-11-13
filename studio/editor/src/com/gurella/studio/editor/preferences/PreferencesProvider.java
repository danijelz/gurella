package com.gurella.studio.editor.preferences;

import java.util.Optional;

public interface PreferencesProvider {
	PreferencesNode projectNode();

	PreferencesNode resourceNode();

	Optional<PreferencesNode> sceneNode();
}
