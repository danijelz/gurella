package com.gurella.engine.scene.tag;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.event.Event;
import com.gurella.engine.subscriptions.scene.tag.TagActivityListener;

class TagRemovedEvent implements Event<TagActivityListener>, Poolable {
	TagComponent component;
	Tag tag;

	@Override
	public Class<TagActivityListener> getSubscriptionType() {
		return TagActivityListener.class;
	}

	@Override
	public void dispatch(TagActivityListener listener) {
		listener.tagRemoved(component, tag);
	}

	@Override
	public void reset() {
		component = null;
		tag = null;
	}
}
