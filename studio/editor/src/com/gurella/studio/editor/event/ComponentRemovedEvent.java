package com.gurella.studio.editor.event;

import com.gurella.engine.event.Event;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.studio.editor.subscription.EditorSceneActivityListener;

public class ComponentRemovedEvent implements Event<EditorSceneActivityListener> {
	final SceneNode2 node;
	final SceneNodeComponent2 component;

	public ComponentRemovedEvent(SceneNode2 node, SceneNodeComponent2 component) {
		this.node = node;
		this.component = component;
	}

	@Override
	public Class<EditorSceneActivityListener> getSubscriptionType() {
		return EditorSceneActivityListener.class;
	}

	@Override
	public void dispatch(EditorSceneActivityListener listener) {
		listener.componentRemoved(node, component);
	}
}
