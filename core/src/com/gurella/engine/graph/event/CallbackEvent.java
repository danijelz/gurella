package com.gurella.engine.graph.event;

import com.gurella.engine.event.Event;

public abstract class CallbackEvent<LISTENER> implements Event<LISTENER> {
	public final EventCallbackIdentifier<LISTENER> eventCallbackIdentifier;

	public CallbackEvent(Class<LISTENER> declaringClass, String id) {
		eventCallbackIdentifier = EventCallbackIdentifier.get(declaringClass, id);
	}

	public CallbackEvent(EventCallbackIdentifier<LISTENER> eventCallbackSignature) {
		this.eventCallbackIdentifier = eventCallbackSignature;
	}
}
