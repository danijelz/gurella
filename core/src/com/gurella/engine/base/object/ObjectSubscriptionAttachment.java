package com.gurella.engine.base.object;

import com.gurella.engine.event.EventService;

public class ObjectSubscriptionAttachment<T> extends Attachment<T> {
	private int objectId;

	public ObjectSubscriptionAttachment(T value, int objectId) {
		super(value);
		this.objectId = objectId;
	}

	@Override
	protected void attach() {
		EventService.subscribe(objectId, value);
	}

	@Override
	protected void detach() {
		EventService.unsubscribe(objectId, value);
	}
}
