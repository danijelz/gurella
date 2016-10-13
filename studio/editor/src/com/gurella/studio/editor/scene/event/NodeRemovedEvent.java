package com.gurella.studio.editor.scene.event;

import com.gurella.engine.event.Event;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.studio.editor.subscription.EditorSceneListener;

public class NodeRemovedEvent implements Event<EditorSceneListener> {
	final Scene scene;
	final SceneNode2 parentNode;
	final SceneNode2 node;

	public NodeRemovedEvent(Scene scene, SceneNode2 parentNode, SceneNode2 node) {
		super();
		this.scene = scene;
		this.parentNode = parentNode;
		this.node = node;
	}

	@Override
	public Class<EditorSceneListener> getSubscriptionType() {
		return EditorSceneListener.class;
	}

	@Override
	public void dispatch(EditorSceneListener subscriber) {
		subscriber.nodeRemoved(scene, parentNode, node);
	}
}
