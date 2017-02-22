package com.gurella.engine.scene.input;

import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.subscriptions.scene.input.SceneKeyTypedListener;

class KeyTypedEvent implements Event<SceneKeyTypedListener> {
	private final Scene scene;
	char character;

	KeyTypedEvent(Scene scene) {
		this.scene = scene;
	}

	void post(char character) {
		this.character = character;
		EventService.post(scene.getInstanceId(), this);
	}

	@Override
	public void dispatch(SceneKeyTypedListener listener) {
		listener.onKeyTyped(character);
	}

	@Override
	public Class<SceneKeyTypedListener> getSubscriptionType() {
		return SceneKeyTypedListener.class;
	}
}