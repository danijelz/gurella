package com.gurella.engine.graph;

public interface SceneGraphListener {
	//TODO add nodeAdded and nodeRemoved...
	void componentActivated(SceneNodeComponent component);

	void componentDeactivated(SceneNodeComponent component);
	
	void componentAdded(SceneNodeComponent component);

	void componentRemoved(SceneNodeComponent component);
}
