package com.gurella.engine.scene.tag;

import com.gurella.engine.event.Event;
import com.gurella.engine.subscriptions.scene.tag.TagActivityListener;

class TagAddedEvent implements Event<TagActivityListener, Void> {
	TagComponent component;
	int tagId;

	@Override
	public Class<TagActivityListener> getSubscriptionType() {
		return TagActivityListener.class;
	}

	@Override
	public void notify(TagActivityListener listener, Void data) {
		listener.tagAdded(component, tagId);
	}
}
