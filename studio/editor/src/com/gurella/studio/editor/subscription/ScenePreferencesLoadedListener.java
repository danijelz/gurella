package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.studio.editor.preferences.PreferencesNode;

public interface ScenePreferencesLoadedListener extends EventSubscription {
	void scenePreferencesLoaded(PreferencesNode scenePreferences);
}
