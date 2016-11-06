package com.gurella.engine.scene.input;

import com.gurella.engine.event.Event;
import com.gurella.engine.subscriptions.scene.input.NodeScrollListener;

class NodeScrollEvent implements Event<NodeScrollListener> {
	private final ScrollInfo scrollInfo;

	NodeScrollEvent(ScrollInfo scrollInfo) {
		this.scrollInfo = scrollInfo;
	}

	@Override
	public Class<NodeScrollListener> getSubscriptionType() {
		return NodeScrollListener.class;
	}

	@Override
	public void dispatch(NodeScrollListener subscriber) {
		subscriber.onScrolled(scrollInfo);
	}
}