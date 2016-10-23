package com.gurella.engine.scene.tag;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.event.Event;
import com.gurella.engine.subscriptions.scene.tag.TagActivityListener;

class TagsAddedEvent implements Event<TagActivityListener>, Poolable {
	TagComponent component;
	final Array<Tag> tags = new Array<Tag>();

	@Override
	public Class<TagActivityListener> getSubscriptionType() {
		return TagActivityListener.class;
	}

	@Override
	public void dispatch(TagActivityListener listener) {
		for (int i = 0, n = tags.size; i < n; i++) {
			listener.tagAdded(component, tags.get(i));
		}
	}

	@Override
	public void reset() {
		component = null;
		tags.clear();
	}
}
