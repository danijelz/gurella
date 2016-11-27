package com.gurella.engine.scene.tag;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.editor.model.ModelEditorDescriptor;
import com.gurella.engine.event.EventService;
import com.gurella.engine.metatype.ModelDescriptor;
import com.gurella.engine.metatype.PropertyDescriptor;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.utils.ImmutableBits;
import com.gurella.engine.utils.Values;

@ModelDescriptor(descriptiveName = "Tags")
@ModelEditorDescriptor(factory = TagComponentEditorFactory.class)
public final class TagComponent extends SceneNodeComponent2 implements Poolable {
	final transient Bits _tags = new Bits();
	public final transient ImmutableBits tagBits = new ImmutableBits(_tags);

	public void addTag(String tagName) {
		addTag(Tag.valueOf(tagName));
	}

	public void addTag(Tag tag) {
		if (!_tags.getAndSet(tag.id) && isActive()) {
			TagAddedEvent tagAddedEvent = PoolService.obtain(TagAddedEvent.class);
			tagAddedEvent.component = this;
			tagAddedEvent.tag = tag;
			EventService.post(getScene().getInstanceId(), tagAddedEvent);
			PoolService.free(tagAddedEvent);
		}
	}

	public void addTags(Tag... tags) {
		if (Values.isEmptyArray(tags)) {
			return;
		}

		boolean changed = false;

		for (int i = 0, n = tags.length; i < n; i++) {
			Tag tag = tags[i];
			if (_tags.getAndClear(tag.id)) {
				changed = true;
			}
		}

		if (isActive() && changed) {
			TagsAddedEvent tagsAddedEvent = PoolService.obtain(TagsAddedEvent.class);
			tagsAddedEvent.component = this;
			tagsAddedEvent.tags.addAll(tags);
			EventService.post(getScene().getInstanceId(), tagsAddedEvent);
			PoolService.free(tagsAddedEvent);
		}
	}

	public void removeTag(String tagName) {
		removeTag(Tag.valueOf(tagName));
	}

	public void removeTag(Tag tag) {
		if (_tags.getAndClear(tag.id) && isActive()) {
			TagRemovedEvent tagRemovedEvent = PoolService.obtain(TagRemovedEvent.class);
			tagRemovedEvent.component = this;
			tagRemovedEvent.tag = tag;
			EventService.post(getScene().getInstanceId(), tagRemovedEvent);
			PoolService.free(tagRemovedEvent);
		}
	}

	public void removeTags(Tag... tags) {
		if (Values.isEmptyArray(tags)) {
			return;
		}

		boolean changed = false;

		for (int i = 0, n = tags.length; i < n; i++) {
			Tag tag = tags[i];
			if (_tags.getAndClear(tag.id)) {
				changed = true;
			}
		}

		if (isActive() && changed) {
			TagsRemovedEvent tagsRemovedEvent = PoolService.obtain(TagsRemovedEvent.class);
			tagsRemovedEvent.component = this;
			tagsRemovedEvent.tags.addAll(tags);
			EventService.post(getScene().getInstanceId(), tagsRemovedEvent);
			PoolService.free(tagsRemovedEvent);
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

	@PropertyDescriptor(nullable = false)
	public String[] getTags() {
		int temp = 0;
		int tagId;
		for (tagId = _tags.nextSetBit(0); tagId >= 0; tagId = _tags.nextSetBit(tagId + 1)) {
			temp++;
		}

		String[] tagNames = new String[temp];
		temp = 0;
		for (tagId = _tags.nextSetBit(0); tagId >= 0; tagId = _tags.nextSetBit(tagId + 1)) {
			Tag tag = Tag.valueOf(tagId);
			tagNames[temp++] = tag.name;
		}

		return tagNames;
	}

	public void setTags(String[] tags) {
		_tags.clear();

		if (Values.isEmptyArray(tags)) {
			return;
		}

		boolean active = isActive();
		for (int i = 0; i < tags.length; i++) {
			String tagName = tags[i];
			if (tagName != null) {
				Tag tag = Tag.valueOf(tagName);
				if (!_tags.getAndSet(tag.id) && active) {
					getScene().tagManager.tagAdded(this, tag);
				}
			}
		}
	}
}
