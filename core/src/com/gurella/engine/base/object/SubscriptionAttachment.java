package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.event.EventService;
import com.gurella.engine.pool.PoolService;

class SubscriptionAttachment extends Attachment<Object> implements Poolable {
	static SubscriptionAttachment obtain(Object subscriber) {
		SubscriptionAttachment attachment = PoolService.obtain(SubscriptionAttachment.class);
		attachment.value = subscriber;
		return attachment;
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
