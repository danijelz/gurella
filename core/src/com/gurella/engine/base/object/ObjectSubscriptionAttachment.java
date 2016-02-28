package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.event.EventService;

//TODO unused 
public class ObjectSubscriptionAttachment<T> extends Attachment<T> implements Poolable {
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

	@Override
	public void reset() {
		objectId = -1;
		value = null;
	}
}
