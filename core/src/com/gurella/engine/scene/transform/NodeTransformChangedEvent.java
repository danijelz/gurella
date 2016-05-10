package com.gurella.engine.scene.transform;

import com.gurella.engine.event.SubscriptionEvent;
import com.gurella.engine.subscriptions.scene.movement.NodeTransformChangedListener;

//TODO unused
public class NodeTransformChangedEvent extends SubscriptionEvent<NodeTransformChangedListener> {
	public static final NodeTransformChangedEvent instance = new NodeTransformChangedEvent();

	public NodeTransformChangedEvent() {
		super(NodeTransformChangedListener.class);
	}

	@Override
	protected void notify(NodeTransformChangedListener listener) {
		listener.onNodeTransformChanged();
	}
}
