package com.gurella.engine.scene.events;

import com.gurella.engine.graph.event.EventCallback;

public interface SceneStartListener {
	@EventCallback
	void onSceneStart();
}
