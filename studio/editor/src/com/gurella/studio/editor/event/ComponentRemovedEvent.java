package com.gurella.studio.editor.event;

import com.gurella.engine.event.Event;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.studio.editor.subscription.EditorSceneListener;

public class ComponentRemovedEvent implements Event<EditorSceneListener> {
	final SceneNode2 node;
	final SceneNodeComponent2 component;

	public ComponentRemovedEvent(SceneNode2 node, SceneNodeComponent2 component) {
		this.node = node;
		this.component = component;
	}

	@Override
	public Class<EditorSceneListener> getSubscriptionType() {
		return EditorSceneListener.class;
	}

	@Override
	public void dispatch(EditorSceneListener subscriber) {
		subscriber.componentRemoved(node, component);
	}
}
