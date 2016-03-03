package com.gurella.engine.scene.tag;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.resource.model.ResourceProperty;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.utils.ImmutableBits;

public class TagComponent extends SceneNodeComponent2 implements Poolable {
	@ResourceProperty(descriptiveName = "tags")
	final Bits _tags = new Bits();
	public final transient ImmutableBits tags = new ImmutableBits(_tags);

	public void addTag(Tag tag) {
		int tagId = tag.id;
		if (!_tags.getAndSet(tagId)) {
			tagAdded(tagId);
		}
	}

	public void addTags(Tag... tags) {
		for (Tag tag : tags) {
			int tagId = tag.id;
			if (!_tags.getAndSet(tagId)) {
				tagAdded(tagId);
			}
		}
	}

	private void tagAdded(int tagId) {
		if (!isActive()) {
			return;
		}

		Scene scene = getScene();
		if (scene == null) {
			return;
		}

		scene.tagManager.tagAdded(this, tagId);
	}

	public void removeTag(Tag tag) {
		int tagId = tag.id;
		if (_tags.getAndClear(tagId)) {
			tagRemoved(tagId);
		}
	}

	public void removeTags(Tag... tags) {
		for (Tag tag : tags) {
			int tagId = tag.id;
			if (_tags.getAndClear(tagId)) {
				tagRemoved(tagId);
			}
		}
	}

	private void tagRemoved(int tagId) {
		if (!isActive()) {
			return;
		}

		Scene scene = getScene();
		if (scene == null) {
			return;
		}

		scene.tagManager.tagRemoved(this, tagId);
	}

	@Override
	public void reset() {
		_tags.clear();
	}
}
