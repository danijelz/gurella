package com.gurella.engine.scene.input;

import com.gurella.engine.event.Event;
import com.gurella.engine.subscriptions.scene.input.SceneScrollListener;

class SceneScrollEvent implements Event<SceneScrollListener> {
	private final ScrollInfo scrollInfo;

	SceneScrollEvent(ScrollInfo scrollInfo) {
		this.scrollInfo = scrollInfo;
	}

	@Override
	public Class<SceneScrollListener> getSubscriptionType() {
		return SceneScrollListener.class;
	}

	@Override
	public void dispatch(SceneScrollListener subscriber) {
		subscriber.onScrolled(scrollInfo);
	}
}