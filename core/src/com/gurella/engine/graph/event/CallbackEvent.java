package com.gurella.engine.graph.event;

import com.gurella.engine.event.Event;

public abstract class CallbackEvent<LISTENER> implements Event<LISTENER> {
	public final EventCallbackSignature<LISTENER> eventCallbackSignature;

	public CallbackEvent(Class<LISTENER> declaringClass, String id) {
		eventCallbackSignature = EventCallbackSignature.get(declaringClass, id);
	}

	public CallbackEvent(EventCallbackSignature<LISTENER> eventCallbackSignature) {
		this.eventCallbackSignature = eventCallbackSignature;
	}
}
