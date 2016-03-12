package com.gurella.engine.scene.tag;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.utils.ImmutableBits;

public final class TagComponent extends SceneNodeComponent2 implements Poolable {
	@PropertyDescriptor(descriptiveName = "tags")
	final Bits _tags = new Bits();
	public final transient ImmutableBits tags = new ImmutableBits(_tags);

	public void addTag(Tag tag) {
		int tagId = tag.id;
		if (!_tags.getAndSet(tagId) && isActive()) {
			EventService.notify(getScene().getInstanceId(), TagAddedEvent.obtain(this, tagId));
		}
	}

	public void addTags(Tag... tags) {
		boolean active = isActive();
		for (Tag tag : tags) {
			int tagId = tag.id;
			if (!_tags.getAndSet(tagId) && active) {
				EventService.notify(getScene().getInstanceId(), TagAddedEvent.obtain(this, tagId));
			}
		}
	}

	public void removeTag(Tag tag) {
		int tagId = tag.id;
		if (_tags.getAndClear(tagId) && isActive()) {
			EventService.notify(getScene().getInstanceId(), TagRemovedEvent.obtain(this, tagId));
		}
	}

	public void removeTags(Tag... tags) {
		boolean active = isActive();
		for (Tag tag : tags) {
			int tagId = tag.id;
			if (_tags.getAndClear(tagId) && active) {
				EventService.notify(getScene().getInstanceId(), TagRemovedEvent.obtain(this, tagId));
			}
		}
	}

	@Override
	public void reset() {
		_tags.clear();
	}
}
