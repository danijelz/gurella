package com.gurella.engine.managedobject;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.pool.PoolService;

class SubscriptionAttachment extends Attachment<EventSubscription> implements Poolable {
	static SubscriptionAttachment obtain(EventSubscription subscriber) {
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
