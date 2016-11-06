package com.gurella.engine.scene.input;

import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.subscriptions.scene.input.PointerActivityListener;

public abstract class PointerProcessor implements PointerActivityListener {
	protected final Scene scene;
	protected int sceneId = -1;

	public PointerProcessor(Scene scene) {
		this.scene = scene;
	}

	public void sceneActivated() {
		sceneId = scene.getInstanceId();
		EventService.subscribe(sceneId, this);
	}

	public void sceneDeactivated() {
		EventService.unsubscribe(sceneId, this);
		sceneId = -1;
	}
}
