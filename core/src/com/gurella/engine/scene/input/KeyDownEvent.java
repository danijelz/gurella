package com.gurella.engine.scene.input;

import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.subscriptions.scene.input.SceneKeyListener;

class KeyDownEvent implements Event<SceneKeyListener> {
	private final Scene scene;
	int keycode;

	KeyDownEvent(Scene scene) {
		this.scene = scene;
	}

	void post(int keycode) {
		this.keycode = keycode;
		EventService.post(scene.getInstanceId(), this);
	}

	@Override
	public void dispatch(SceneKeyListener listener) {
		listener.onKeyDown(keycode);
	}

	@Override
	public Class<SceneKeyListener> getSubscriptionType() {
		return SceneKeyListener.class;
	}
}