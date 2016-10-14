package com.gurella.studio.editor.scene.event;

import com.gurella.engine.event.Event;
import com.gurella.engine.scene.Scene;
import com.gurella.studio.editor.subscription.SceneLoadedListener;

public class SceneLoadedEvent implements Event<SceneLoadedListener> {
	private Scene scene;

	public SceneLoadedEvent(Scene scene) {
		this.scene = scene;
	}

	@Override
	public Class<SceneLoadedListener> getSubscriptionType() {
		return SceneLoadedListener.class;
	}

	@Override
	public void dispatch(SceneLoadedListener subscriber) {
		subscriber.sceneLoaded(scene);
	}
}
