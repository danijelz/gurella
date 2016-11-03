package com.gurella.studio.editor.event;

import com.gurella.engine.event.Event;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.studio.editor.subscription.EditorSceneActivityListener;

public class NodeAddedEvent implements Event<EditorSceneActivityListener> {
	final Scene scene;
	final SceneNode2 parentNode;
	final SceneNode2 node;

	public NodeAddedEvent(Scene scene, SceneNode2 parentNode, SceneNode2 node) {
		super();
		this.scene = scene;
		this.parentNode = parentNode;
		this.node = node;
	}

	@Override
	public Class<EditorSceneActivityListener> getSubscriptionType() {
		return EditorSceneActivityListener.class;
	}

	@Override
	public void dispatch(EditorSceneActivityListener subscriber) {
		subscriber.nodeAdded(scene, parentNode, node);
	}
}
