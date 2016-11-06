package com.gurella.engine.scene.input;

import com.gurella.engine.event.Event;
import com.gurella.engine.subscriptions.scene.input.NodeTouchListener;

class NodeTouchUpEvent implements Event<NodeTouchListener> {
	private final TouchInfo touchInfo;

	NodeTouchUpEvent(TouchInfo touchInfo) {
		this.touchInfo = touchInfo;
	}

	@Override
	public Class<NodeTouchListener> getSubscriptionType() {
		return NodeTouchListener.class;
	}

	@Override
	public void dispatch(NodeTouchListener subscriber) {
		subscriber.onTouchUp(touchInfo);
	}
}