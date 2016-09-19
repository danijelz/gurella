package com.gurella.engine.scene.tag;

import com.gurella.engine.event.SubscriptionEvent;
import com.gurella.engine.subscriptions.scene.tag.TagActivityListener;

class TagRemovedEvent extends SubscriptionEvent<TagActivityListener> {
	TagComponent component;
	int tagId;

	TagRemovedEvent() {
		super(TagActivityListener.class);
	}

	@Override
	protected void notify(TagActivityListener listener) {
		listener.tagRemoved(component, tagId);
	}
}
