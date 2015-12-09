package com.gurella.engine.application.events;

import com.gurella.engine.graph.event.EventCallback;
import com.gurella.engine.scene.Scene;

public interface TransitionSceneStopListener {
	@EventCallback
	void onSceneStop(Scene scene);
}
