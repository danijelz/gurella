package com.gurella.engine.graph;

//TODO rename
public abstract class GraphListenerSystem extends SceneSystem implements SceneGraphListener {
	public GraphListenerSystem() {
		SceneGraphUtils.asSceneGraphListener(this);
	}

	@Override
	public void componentAdded(SceneNodeComponent component) {
	}

	@Override
	public void componentRemoved(SceneNodeComponent component) {
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
	}
}
