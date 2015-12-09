package com.gurella.engine.scene.events;

import com.gurella.engine.graph.event.EventCallback;

public interface SceneStopListener {
	@EventCallback
	void onSceneStop();
}
