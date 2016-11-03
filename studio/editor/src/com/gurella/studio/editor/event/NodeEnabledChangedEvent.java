package com.gurella.studio.editor.event;

import com.gurella.engine.event.Event;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.studio.editor.subscription.NodeEnabledChangedListener;

public class NodeEnabledChangedEvent implements Event<NodeEnabledChangedListener> {
	private SceneNode2 node;

	public NodeEnabledChangedEvent(SceneNode2 node) {
		this.node = node;
	}

	@Override
	public Class<NodeEnabledChangedListener> getSubscriptionType() {
		return NodeEnabledChangedListener.class;
	}

	@Override
	public void dispatch(NodeEnabledChangedListener listener) {
		listener.nodeEnabledChanged(node);
	}
}
