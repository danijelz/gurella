package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.event.EventService;

//TODO unused 
class SubscriptionAttachment<T> extends Attachment<T> implements Poolable {
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

	@Override
	public void reset() {
		value = null;
	}
}
