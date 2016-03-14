package com.gurella.engine.scene.tag;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.SceneNode2;
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

	public static void addTag(SceneNode2 node, Tag tag) {
		TagComponent component = node.getComponent(TagComponent.class);
		if (component == null) {
			component = node.newComponent(TagComponent.class);
		}
		component.addTag(tag);
	}

	public static void addTags(SceneNode2 node, Tag... tags) {
		TagComponent component = node.getComponent(TagComponent.class);
		if (component == null) {
			component = node.newComponent(TagComponent.class);
		}
		component.addTags(tags);
	}

	public static void removeTag(SceneNode2 node, Tag tag) {
		TagComponent component = node.getComponent(TagComponent.class);
		if (component != null) {
			component.removeTag(tag);
		}
	}

	public static void removeTags(SceneNode2 node, Tag... tags) {
		TagComponent component = node.getComponent(TagComponent.class);
		if (component != null) {
			component.removeTags(tags);
		}
	}
}
