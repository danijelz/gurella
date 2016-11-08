package com.gurella.engine.scene.input;

import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.subscriptions.scene.input.PointerActivityListener;

class PointerActivityEvent implements Event<PointerActivityListener> {
	private final Scene scene;

	int pointer = -1;
	int button = -1;
	PointerTrack pointerTrack;

	PointerActivityEvent(Scene scene) {
		this.scene = scene;
	}

	void post(int pointer, int button, PointerTrack pointerTrack) {
		this.pointer = pointer;
		this.button = button;
		this.pointerTrack = pointerTrack;
		EventService.post(scene.getInstanceId(), this);
		this.pointer = -1;
		this.button = -1;
		this.pointerTrack = null;
	}

	@Override
	public void dispatch(PointerActivityListener listener) {
		listener.onPointerActivity(pointer, button, pointerTrack);
	}

	@Override
	public Class<PointerActivityListener> getSubscriptionType() {
		return PointerActivityListener.class;
	}
}