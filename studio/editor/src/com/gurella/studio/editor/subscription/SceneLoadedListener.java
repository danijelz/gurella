package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.Scene;

public interface SceneLoadedListener extends EventSubscription {
	void sceneLoaded(Scene scene);
}
