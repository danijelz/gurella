package com.gurella.engine.graph.tag;

import com.badlogic.gdx.utils.Bits;
import com.gurella.engine.graph.SceneGraph;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.resource.model.ResourceProperty;
import com.gurella.engine.resource.model.TransientProperty;
import com.gurella.engine.utils.ImmutableBits;

public class TagComponent extends SceneNodeComponent {
	@ResourceProperty(descriptiveName = "tags")
	final Bits tagsInternal = new Bits();
	@TransientProperty
	public final ImmutableBits tags = new ImmutableBits(tagsInternal);

	public void addTag(Tag tag) {
		int tagId = tag.id;
		if (!tagsInternal.getAndSet(tagId)) {
			tagAdded(tagId);
		}
	}

	public void addTags(Tag... tags) {
		for (Tag tag : tags) {
			int tagId = tag.id;
			if (!tagsInternal.getAndSet(tagId)) {
				tagAdded(tagId);
			}
		}
	}

	private void tagAdded(int tagId) {
		if (!isActive()) {
			return;
		}

		SceneGraph graph = getGraph();
		if (graph == null) {
			return;
		}

		graph.tagManager.tagAdded(this, tagId);
	}

	public void removeTag(Tag tag) {
		int tagId = tag.id;
		if (tagsInternal.getAndClear(tagId)) {
			tagRemoved(tagId);
		}
	}

	public void removeTags(Tag... tags) {
		for (Tag tag : tags) {
			int tagId = tag.id;
			if (tagsInternal.getAndClear(tagId)) {
				tagRemoved(tagId);
			}
		}
	}

	private void tagRemoved(int tagId) {
		if (!isActive()) {
			return;
		}

		SceneGraph graph = getGraph();
		if (graph == null) {
			return;
		}

		graph.tagManager.tagRemoved(this, tagId);
	}

	@Override
	protected void resetted() {
		tagsInternal.clear();
	}
}
