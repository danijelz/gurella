package com.gurella.engine.scene.tag;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.utils.ImmutableBits;

public final class TagComponent extends SceneNodeComponent2 implements Poolable {
	final transient Bits _tags = new Bits();
	public final transient ImmutableBits tagBits = new ImmutableBits(_tags);

	public void addTag(Tag tag) {
		int tagId = tag.id;
		if (!_tags.getAndSet(tagId) && isActive()) {
			getScene().tagManager.tagAdded(this, tagId);
		}
	}

	public void addTags(Tag... tags) {
		boolean active = isActive();
		for (Tag tag : tags) {
			int tagId = tag.id;
			if (!_tags.getAndSet(tagId) && active) {
				getScene().tagManager.tagAdded(this, tagId);
			}
		}
	}

	public void removeTag(Tag tag) {
		int tagId = tag.id;
		if (_tags.getAndClear(tagId) && isActive()) {
			getScene().tagManager.tagRemoved(this, tagId);
		}
	}

	public void removeTags(Tag... tags) {
		boolean active = isActive();
		for (Tag tag : tags) {
			int tagId = tag.id;
			if (_tags.getAndClear(tagId) && active) {
				getScene().tagManager.tagRemoved(this, tagId);
			}
		}
	}

	@Override
	public void reset() {
		_tags.clear();
	}

	public static void addTag(SceneNode2 node, Tag tag) {
		TagComponent component = node.getComponent(TagComponent.class, true);
		if (component == null) {
			component = node.newComponent(TagComponent.class);
		}
		component.addTag(tag);
	}

	public static void addTags(SceneNode2 node, Tag... tags) {
		TagComponent component = node.getComponent(TagComponent.class, true);
		if (component == null) {
			component = node.newComponent(TagComponent.class);
		}
		component.addTags(tags);
	}

	public static void removeTag(SceneNode2 node, Tag tag) {
		TagComponent component = node.getComponent(TagComponent.class, true);
		if (component != null) {
			component.removeTag(tag);
		}
	}

	public static void removeTags(SceneNode2 node, Tag... tags) {
		TagComponent component = node.getComponent(TagComponent.class, true);
		if (component != null) {
			component.removeTags(tags);
		}
	}

	public String[] getTags() {
		int index = 0;
		int length = 0;
		while ((index = tagBits.nextSetBit(index)) > 0) {
			length++;
		}

		String[] tagNames = new String[length];
		index = 0;
		length = 0;
		while ((index = tagBits.nextSetBit(index)) > 0) {
			Tag tag = Tag.get(index);
			tagNames[length++] = tag.name;
		}

		return tagNames;
	}

	public void setTags(String[] tags) {
		if (tags == null) {
			return;
		}

		boolean active = isActive();
		for (int i = 0; i < tags.length; i++) {
			Tag tag = Tag.get(tags[i]);
			int tagId = tag.id;
			if (!_tags.getAndSet(tagId) && active) {
				getScene().tagManager.tagAdded(this, tagId);
			}
		}
	}
}
