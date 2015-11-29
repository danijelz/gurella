package com.gurella.engine.graph;

import com.gurella.engine.application.UpdateListener;

//TODO rename
public abstract class UpdateListenerSystem extends SceneSystem implements UpdateListener {
	public UpdateListenerSystem() {
		SceneGraphUtils.asUpdateListener(this);
	}
}
