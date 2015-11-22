package com.gurella.engine.graph.tag;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.gurella.engine.graph.GraphListenerSystem;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;

//TODO add tag family so singletone tags by family can replace layer
public class TagManager extends GraphListenerSystem {
	private static int TAG_TYPE_INDEX = 0;
	private static ObjectIntMap<Tag> TAG_TYPE_INEXES_BY_CLASS = new ObjectIntMap<Tag>();
	private static IntMap<Tag> TAGS_BY_TYPE = new IntMap<Tag>();

	private IntMap<Array<SceneNode>> nodesByTag = new IntMap<Array<SceneNode>>();

	public static int getTagType(Tag tag) {
		int tagType = TAG_TYPE_INEXES_BY_CLASS.get(tag, -1);

		if (tagType == -1) {
			tagType = TAG_TYPE_INDEX++;
			TAG_TYPE_INEXES_BY_CLASS.put(tag, tagType);
			TAGS_BY_TYPE.put(tagType, tag);
		}

		return tagType;
	}

	public static Tag getTagByType(int tagType) {
		return TAGS_BY_TYPE.get(tagType);
	}

	@Override
	public void componentAdded(SceneNodeComponent component) {
		if (component instanceof TagComponent) {
			TagComponent tagComponent = (TagComponent) component;
			for (Tag tag : tagComponent.tags) {
				getNodes(tag).add(component.getNode());
			}
		}
	}

	private Array<SceneNode> getNodes(Tag tag) {
		int tagType = tag.getTagType();
		Array<SceneNode> nodes = nodesByTag.get(tagType);

		if (nodes == null) {
			nodes = new Array<SceneNode>();
			nodesByTag.put(tagType, nodes);
		}

		return nodes;
	}

	@Override
	public void componentRemoved(SceneNodeComponent component) {
		if (component instanceof TagComponent) {
			TagComponent tagComponent = (TagComponent) component;
			for (Tag tag : tagComponent.tags) {
				int tagType = tag.getTagType();
				Array<SceneNode> nodes = nodesByTag.get(tagType);
				nodes.removeValue(component.getNode(), true);

				if (nodes.size < 1) {
					nodesByTag.remove(tagType);
				}
			}
		}
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
	}

	public <T extends SceneNode> Array<T> getNodesByTag(Tag tag) {
		return getNodesByTag(tag.getTagType());
	}

	public <T extends SceneNode> Array<T> getNodesByTag(int tagType) {
		@SuppressWarnings("unchecked")
		Array<T> casted = (Array<T>) nodesByTag.get(tagType);
		return casted;
	}

	public <T extends SceneNode> T getSingleNodeByTag(Tag tag) {
		return getSingleNodeByTag(tag.getTagType());
	}

	public <T extends SceneNode> T getSingleNodeByTag(int tagType) {
		Array<SceneNode> nodes = getNodesByTag(tagType);

		if (nodes == null) {
			return null;
		} else {
			@SuppressWarnings("unchecked")
			T casted = (T) nodes.iterator().next();
			return casted;
		}
	}
}
