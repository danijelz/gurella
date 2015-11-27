package com.gurella.engine.graph.tag;

import com.badlogic.gdx.utils.OrderedSet;
import com.gurella.engine.graph.SceneGraph;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.resource.model.ResourceProperty;
import com.gurella.engine.resource.model.TransientProperty;
import com.gurella.engine.utils.ImmutableArray;

public class TagComponent extends SceneNodeComponent {
	@ResourceProperty(descriptiveName = "tags")
	final OrderedSet<Tag> tagsInternal = new OrderedSet<Tag>();
	@TransientProperty
	public final ImmutableArray<Tag> tags = new ImmutableArray<Tag>(tagsInternal.orderedItems());

	public void addTag(Tag tag) {
		if (tagsInternal.add(tag)) {
			tagAdded(tag);
		}
	}

	public void addTags(Tag... tags) {
		for (Tag tag : tags) {
			if (tagsInternal.add(tag)) {
				tagAdded(tag);
			}
		}
	}

	private void tagAdded(Tag tag) {
		if (!isActive()) {
			return;
		}

		SceneGraph graph = getGraph();
		if (graph == null) {
			return;
		}

		graph.tagManager.tagAdded(this, tag);
	}

	public void removeTag(Tag tag) {
		if (tagsInternal.remove(tag)) {
			tagRemoved(tag);
		}
	}

	public void removeTags(Tag... tags) {
		for (Tag tag : tags) {
			if (tagsInternal.remove(tag)) {
				tagRemoved(tag);
			}
		}
	}

	private void tagRemoved(Tag tag) {
		if (!isActive()) {
			return;
		}

		SceneGraph graph = getGraph();
		if (graph == null) {
			return;
		}

		graph.tagManager.tagRemoved(this, tag);
	}

	@Override
	protected void resetted() {
		tagsInternal.clear();
	}
}
