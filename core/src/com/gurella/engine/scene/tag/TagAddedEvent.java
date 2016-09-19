package com.gurella.engine.scene.tag;

import com.gurella.engine.event.SubscriptionEvent;
import com.gurella.engine.subscriptions.scene.tag.TagActivityListener;

class TagAddedEvent extends SubscriptionEvent<TagActivityListener> {
	TagComponent component;
	int tagId;

	TagAddedEvent() {
		super(TagActivityListener.class);
	}

	@Override
	protected void notify(TagActivityListener listener) {
		listener.tagAdded(component, tagId);
	}
}
