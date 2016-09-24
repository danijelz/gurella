package com.gurella.engine.scene.tag;

import com.gurella.engine.event.Event2;
import com.gurella.engine.subscriptions.scene.tag.TagActivityListener;

class TagRemovedEvent implements Event2<TagActivityListener, TagComponent, Tag> {
	@Override
	public Class<TagActivityListener> getSubscriptionType() {
		return TagActivityListener.class;
	}

	@Override
	public void notify(TagActivityListener listener, TagComponent component, Tag tag) {
		listener.tagRemoved(component, tag);
	}
}
