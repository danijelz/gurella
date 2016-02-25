package com.gurella.engine.scene.event;

import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventCallbackIdentifier;

public abstract class CallbackEvent<LISTENER> implements Event<LISTENER> {
	public final EventCallbackIdentifier<LISTENER> eventCallbackIdentifier;

	public CallbackEvent(Class<LISTENER> declaringClass, String id) {
		eventCallbackIdentifier = EventCallbackIdentifier.get(declaringClass, id);
	}

	public CallbackEvent(EventCallbackIdentifier<LISTENER> eventCallbackSignature) {
		this.eventCallbackIdentifier = eventCallbackSignature;
	}
}
