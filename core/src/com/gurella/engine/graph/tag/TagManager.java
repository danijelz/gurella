package com.gurella.engine.graph.tag;

import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.graph.GraphListenerSystem;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;

//TODO add tag family so singletone tags by family can replace layer
public class TagManager extends GraphListenerSystem {
	private IntMap<ArrayExt<SceneNode>> nodesByTag = new IntMap<ArrayExt<SceneNode>>();

	@Override
	public void componentActivated(SceneNodeComponent component) {
		if (component instanceof TagComponent) {
			TagComponent tagComponent = (TagComponent) component;
			for (Tag tag : tagComponent.tags) {
				getNodes(tag).add(component.getNode());
			}
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		if (component instanceof TagComponent) {
			TagComponent tagComponent = (TagComponent) component;
			for (Tag tag : tagComponent.tags) {
				nodesByTag.get(tag.id).removeValue(component.getNode(), true);
			}
		}
	}

	private ArrayExt<SceneNode> getNodes(Tag tag) {
		int tagId = tag.id;
		ArrayExt<SceneNode> nodes = nodesByTag.get(tagId);

		if (nodes == null) {
			nodes = new ArrayExt<SceneNode>();
			nodesByTag.put(tagId, nodes);
		}

		return nodes;
	}

	void tagAdded(TagComponent component, Tag tag) {
		getNodes(tag).add(component.getNode());
	}

	void tagRemoved(TagComponent component, Tag tag) {
		nodesByTag.get(tag.id).removeValue(component.getNode(), true);
	}

	public ImmutableArray<SceneNode> getNodesByTag(Tag tag) {
		return getNodesByTag(tag.id);
	}

	public ImmutableArray<SceneNode> getNodesByTag(int tagType) {
		ArrayExt<SceneNode> nodes = nodesByTag.get(tagType);
		return nodes == null ? ImmutableArray.<SceneNode> empty() : nodes.immutable();
	}

	public SceneNode getSingleNodeByTag(Tag tag) {
		return getSingleNodeByTag(tag.id);
	}

	public SceneNode getSingleNodeByTag(int tagType) {
		ArrayExt<SceneNode> nodes = nodesByTag.get(tagType);

		if (nodes == null || nodes.size == 0) {
			return null;
		} else {
			return nodes.get(0);
		}
	}

	@Override
	protected void resetted() {
		for (ArrayExt<SceneNode> nodes : nodesByTag.values()) {
			nodes.clear();
		}
	}
}
