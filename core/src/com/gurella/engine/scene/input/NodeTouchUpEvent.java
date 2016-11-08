package com.gurella.engine.scene.input;

import com.gurella.engine.event.Event;
import com.gurella.engine.subscriptions.scene.input.NodeTouchListener;

class NodeTouchUpEvent implements Event<NodeTouchListener> {
	private final PointerInfo pointerInfo;

	NodeTouchUpEvent(PointerInfo pointerInfo) {
		this.pointerInfo = pointerInfo;
	}

	@Override
	public Class<NodeTouchListener> getSubscriptionType() {
		return NodeTouchListener.class;
	}

	@Override
	public void dispatch(NodeTouchListener subscriber) {
		subscriber.onTouchUp(pointerInfo);
	}
}