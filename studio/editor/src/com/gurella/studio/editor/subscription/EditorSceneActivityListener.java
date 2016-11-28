package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;

public interface EditorSceneActivityListener extends EventSubscription {
	void nodeAdded(Scene scene, SceneNode parentNode, SceneNode node);

	void nodeRemoved(Scene scene, SceneNode parentNode, SceneNode node);

	void nodeIndexChanged(SceneNode node, int newIndex);

	void componentAdded(SceneNode node, SceneNodeComponent component);

	void componentRemoved(SceneNode node, SceneNodeComponent component);

	void componentIndexChanged(SceneNodeComponent component, int newIndex);
}
