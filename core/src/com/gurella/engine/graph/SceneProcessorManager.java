package com.gurella.engine.graph;

import com.gurella.engine.application.UpdateListener;

public abstract class SceneProcessorManager extends GraphListenerSystem implements UpdateListener {
	public SceneProcessorManager() {
		SceneGraphUtils.asUpdateListener(this);
	}
}
