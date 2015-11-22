package com.gurella.engine.application;

import com.gurella.engine.event.Event;

public class UpdateEvent implements Event<UpdateListener> {
	public static UpdateEvent instance = new UpdateEvent();
	
	private UpdateEvent() {
	}

	@Override
	public void notify(UpdateListener listener) {
		listener.update();
	}
}
