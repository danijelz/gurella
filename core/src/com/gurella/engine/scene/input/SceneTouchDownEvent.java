package com.gurella.engine.scene.input;

import com.gurella.engine.event.Event;
import com.gurella.engine.subscriptions.scene.input.SceneTouchListener;

class SceneTouchDownEvent implements Event<SceneTouchListener> {
	private final PointerInfo pointerInfo;

	SceneTouchDownEvent(PointerInfo pointerInfo) {
		this.pointerInfo = pointerInfo;
	}

	@Override
	public Class<SceneTouchListener> getSubscriptionType() {
		return SceneTouchListener.class;
	}

	@Override
	public void dispatch(SceneTouchListener subscriber) {
		subscriber.onTouchDown(pointerInfo);
	}
}