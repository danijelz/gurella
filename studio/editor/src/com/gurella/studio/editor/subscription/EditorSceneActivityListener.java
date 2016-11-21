package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;

public interface EditorSceneActivityListener extends EventSubscription {
	void nodeAdded(Scene scene, SceneNode2 parentNode, SceneNode2 node);

	void nodeRemoved(Scene scene, SceneNode2 parentNode, SceneNode2 node);

	void nodeParentChanged(SceneNode2 node, SceneNode2 newParent);

	void nodeIndexChanged(SceneNode2 node, int newIndex);

	void componentAdded(SceneNode2 node, SceneNodeComponent2 component);

	void componentRemoved(SceneNode2 node, SceneNodeComponent2 component);

	void componentIndexChanged(SceneNodeComponent2 component, int newIndex);
}
