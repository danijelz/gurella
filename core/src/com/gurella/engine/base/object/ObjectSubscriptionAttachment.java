package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.event.EventService;
import com.gurella.engine.pool.PoolService;

//TODO unused 
class ObjectSubscriptionAttachment extends Attachment<Object> implements Poolable {
	private int objectId;

	static ObjectSubscriptionAttachment obtain(Object subscriber, int objectId) {
		ObjectSubscriptionAttachment attachment = PoolService.obtain(ObjectSubscriptionAttachment.class);
		attachment.value = subscriber;
		attachment.objectId = objectId;
		return attachment;
	}

	@Override
	protected void attach() {
		EventService.subscribe(objectId, value);
	}

	@Override
	protected void detach() {
		EventService.unsubscribe(objectId, value);
	}

	@Override
	public void reset() {
		objectId = -1;
		value = null;
	}

	void free() {
		PoolService.free(this);
	}
}
