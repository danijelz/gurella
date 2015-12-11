package com.gurella.engine.scene.tag;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneGraphListener;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.SceneSystem;
import com.gurella.engine.scene.manager.ComponentManager;
import com.gurella.engine.scene.manager.ComponentManager.ComponentFamily;
import com.gurella.engine.scene.manager.ComponentTypePredicate;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;

//TODO attach listeners
public class TagManager extends SceneSystem implements SceneGraphListener {
	private static final ComponentFamily<TagComponent> family = new ComponentFamily<TagComponent>(
			new ComponentTypePredicate(TagComponent.class));

	private IntMap<ArrayExt<SceneNode>> nodesByTag = new IntMap<ArrayExt<SceneNode>>();
	private IntMap<FamilyNodes> families = new IntMap<FamilyNodes>();

	@Override
	protected void activated() {
		ComponentManager componentManager = getScene().componentManager;
		componentManager.registerComponentFamily(family);
		ImmutableArray<? extends TagComponent> components = componentManager.getComponents(family);
		for (int i = 0; i < components.size(); i++) {
			componentActivated(components.get(i));
		}
	}

	@Override
	protected void deactivated() {
		getScene().componentManager.unregisterComponentFamily(family);

		for (ArrayExt<SceneNode> nodes : nodesByTag.values()) {
			nodes.clear();
		}

		for (FamilyNodes familyNodes : families.values()) {
			Pools.free(familyNodes);
		}
		families.clear();
	}

	@Override
	public void componentAdded(SceneNodeComponent component) {
	}

	@Override
	public void componentRemoved(SceneNodeComponent component) {
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		if (component instanceof TagComponent) {
			TagComponent tagComponent = (TagComponent) component;
			updateFamilies(tagComponent);
			Bits tags = tagComponent.tagsInternal;
			int tagId = tags.nextSetBit(0);
			while (tagId != -1) {
				getNodesArray(tagId).add(component.getNode());
				tagId = tags.nextSetBit(tagId);
			}
		}
	}

	private void updateFamilies(TagComponent component) {
		for (FamilyNodes familyNodes : families.values()) {
			familyNodes.handle(component);
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		if (component instanceof TagComponent) {
			TagComponent tagComponent = (TagComponent) component;
			removeFromFamilies(tagComponent);
			Bits tags = tagComponent.tagsInternal;
			int tagId = tags.nextSetBit(0);
			while (tagId != -1) {
				getNodesArray(tagId).removeValue(component.getNode(), true);
				tagId = tags.nextSetBit(tagId);
			}
		}
	}

	private void removeFromFamilies(TagComponent component) {
		SceneNode node = component.getNode();
		for (FamilyNodes familyNodes : families.values()) {
			familyNodes.remove(node);
		}
	}

	public void registerFamily(TagFamily tagFamily) {
		int familyId = tagFamily.id;
		if (families.containsKey(familyId)) {
			return;
		}
		FamilyNodes familyNodes = Pools.obtain(FamilyNodes.class);
		familyNodes.family = tagFamily;
		families.put(familyId, familyNodes);

		Scene scene = getScene();
		if (scene == null) {
			return;
		}

		ImmutableArray<TagComponent> components = scene.componentManager.getComponents(family);
		for (int i = 0; i < components.size(); i++) {
			familyNodes.handle(components.get(i));
		}
	}

	public void unregisterFamily(TagFamily family) {
		FamilyNodes familyNodes = families.remove(family.id);
		if (familyNodes != null) {
			Pools.free(familyNodes);
		}
	}

	private ArrayExt<SceneNode> getNodesArray(int tagId) {
		ArrayExt<SceneNode> nodes = nodesByTag.get(tagId);

		if (nodes == null) {
			nodes = new ArrayExt<SceneNode>();
			nodesByTag.put(tagId, nodes);
		}

		return nodes;
	}

	void tagAdded(TagComponent component, int tagId) {
		getNodesArray(tagId).add(component.getNode());
	}

	void tagRemoved(TagComponent component, int tagId) {
		nodesByTag.get(tagId).removeValue(component.getNode(), true);
	}

	public ImmutableArray<SceneNode> getNodes(Tag tag) {
		int tagId = tag.id;
		ArrayExt<SceneNode> nodes = nodesByTag.get(tagId);
		return nodes == null ? ImmutableArray.<SceneNode> empty() : nodes.immutable();
	}

	public boolean belongsToFamily(SceneNode node, TagFamily family) {
		return getNodes(family).contains(node, true);
	}

	public ImmutableArray<SceneNode> getNodes(TagFamily family) {
		FamilyNodes familyNodes = families.get(family.id);
		return familyNodes == null ? ImmutableArray.<SceneNode> empty() : familyNodes.immutableNodes;
	}

	public SceneNode getSingleNodeByTag(Tag tag) {
		int tagId = tag.id;
		ArrayExt<SceneNode> nodes = nodesByTag.get(tagId);

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

	public static final class TagFamily {
		private static int INDEXER = 0;

		public final int id;
		private final Bits all = new Bits();
		private final Bits exclude = new Bits();
		private final Bits any = new Bits();

		public TagFamily() {
			id = INDEXER++;
		}

		public boolean matches(TagComponent component) {
			Bits tags = component.tagsInternal;

			if (!tags.containsAll(all)) {
				return false;
			}

			if (!any.isEmpty() && !any.intersects(tags)) {
				return false;
			}

			if (!exclude.isEmpty() && exclude.intersects(tags)) {
				return false;
			}

			return true;
		}

		public static Builder all(Tag... tags) {
			return new Builder().all(tags);
		}

		public static Builder exclude(Tag... tags) {
			return new Builder().exclude(tags);
		}

		public static Builder any(Tag... tags) {
			return new Builder().any(tags);
		}

		public static class Builder implements Poolable {
			private final Bits all = new Bits();
			private final Bits exclude = new Bits();
			private final Bits any = new Bits();

			private Builder() {
			}

			public Builder all(Tag... tags) {
				for (Tag tag : tags) {
					all.set(tag.id);
				}
				return this;
			}

			public Builder any(Tag... tags) {
				for (Tag tag : tags) {
					any.set(tag.id);
				}
				return this;
			}

			public Builder exclude(Tag... tags) {
				for (Tag tag : tags) {
					exclude.set(tag.id);
				}
				return this;
			}

			public TagFamily build() {
				TagFamily family = new TagFamily();
				family.all.or(all);
				family.exclude.or(exclude);
				family.any.or(any);
				return family;
			}

			@Override
			public boolean equals(Object o) {
				if (this == o)
					return true;
				if (o == null || getClass() != o.getClass())
					return false;

				Builder builder = (Builder) o;

				if (!all.equals(builder.all))
					return false;
				if (!exclude.equals(builder.exclude))
					return false;
				if (!any.equals(builder.any))
					return false;

				return true;
			}

			@Override
			public int hashCode() {
				int result = all.hashCode();
				result = 31 * result + exclude.hashCode();
				result = 31 * result + any.hashCode();
				return result;
			}

			@Override
			public void reset() {
				all.clear();
				exclude.clear();
				any.clear();
			}
		}
	}

	private static class FamilyNodes implements Poolable {
		private TagFamily family;
		private final Array<SceneNode> nodes = new Array<SceneNode>();
		private final ImmutableArray<SceneNode> immutableNodes = new ImmutableArray<SceneNode>(nodes);

		private void handle(TagComponent component) {
			SceneNode node = component.getNode();
			boolean belongsToFamily = family.matches(component);
			boolean containsNode = nodes.contains(node, true);
			if (belongsToFamily && !containsNode) {
				nodes.add(node);
			} else if (!belongsToFamily && containsNode) {
				nodes.removeValue(node, true);
			}
		}

		private void remove(SceneNode node) {
			nodes.removeValue(node, true);
		}

		@Override
		public void reset() {
			family = null;
			nodes.clear();
		}
	}
}
