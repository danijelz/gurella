package com.gurella.engine.graph;

import com.gurella.engine.application.UpdateListener;

//TODO rename
public abstract class SceneProcessor extends SceneSystem implements UpdateListener {
	public SceneProcessor() {
		SceneGraphUtils.asUpdateListener(this);
	}
}
