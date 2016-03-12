package com.gurella.engine.scene.tag;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.event.SubscriptionEvent;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.subscriptions.scene.tag.TagActivityListener;

class TagAddedEvent extends SubscriptionEvent<TagActivityListener> implements Poolable {
	TagComponent component;
	int tagId;

	static TagAddedEvent obtain(TagComponent component, int tagId) {
		TagAddedEvent event = PoolService.obtain(TagAddedEvent.class);
		event.component = component;
		event.tagId = tagId;
		return event;
	}

	TagAddedEvent() {
		super(TagActivityListener.class);
	}

	@Override
	protected void notify(TagActivityListener listener) {
		listener.tagAdded(component, tagId);
	}

	@Override
	public void reset() {
		component = null;
		tagId = -1;
	}
}
