package com.gurella.studio.editor.event;

import com.gurella.engine.event.Event;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.studio.editor.subscription.NodeNameChangedListener;

public class NodeNameChangedEvent implements Event<NodeNameChangedListener> {
	private SceneNode2 node;

	public NodeNameChangedEvent(SceneNode2 node) {
		this.node = node;
	}

	@Override
	public Class<NodeNameChangedListener> getSubscriptionType() {
		return NodeNameChangedListener.class;
	}

	@Override
	public void dispatch(NodeNameChangedListener subscriber) {
		subscriber.nodeNameChanged(node);
	}
}
