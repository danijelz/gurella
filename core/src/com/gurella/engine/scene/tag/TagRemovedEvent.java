package com.gurella.engine.scene.tag;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.event.SubscriptionEvent;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.subscriptions.scene.tag.TagActivityListener;

class TagRemovedEvent extends SubscriptionEvent<TagActivityListener> implements Poolable {
	TagComponent component;
	int tagId;

	static TagRemovedEvent obtain(TagComponent component, int tagId) {
		TagRemovedEvent event = PoolService.obtain(TagRemovedEvent.class);
		event.component = component;
		event.tagId = tagId;
		return event;
	}

	TagRemovedEvent() {
		super(TagActivityListener.class);
	}

	@Override
	protected void notify(TagActivityListener listener) {
		listener.tagRemoved(component, tagId);
	}

	@Override
	public void reset() {
		component = null;
		tagId = -1;
	}
}
