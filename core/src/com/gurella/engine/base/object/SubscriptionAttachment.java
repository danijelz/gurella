package com.gurella.engine.base.object;

import com.gurella.engine.event.EventService;

//TODO unused + Poolable
class SubscriptionAttachment<T> extends Attachment<T> {
	public SubscriptionAttachment(T value) {
		super(value);
	}

	@Override
	protected void attach() {
		EventService.subscribe(value);
	}

	@Override
	protected void detach() {
		EventService.unsubscribe(value);
	}
}
