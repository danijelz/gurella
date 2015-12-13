package com.gurella.engine.scene;

public interface SceneListener {
	//TODO add nodeAdded and nodeRemoved...
	void componentActivated(SceneNodeComponent component);

	void componentDeactivated(SceneNodeComponent component);
	
	void componentAdded(SceneNodeComponent component);

	void componentRemoved(SceneNodeComponent component);
}
