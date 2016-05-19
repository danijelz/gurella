package com.gurella.studio.editor.subscriptions;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.Scene;

//TODO unused
public interface SceneLoadedListener extends EventSubscription {
	void onSceneLoaded(Scene scene);
}
