package com.gurella.engine.scene.tag;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.SceneSystem2;
import com.gurella.engine.scene.manager.ComponentManager;
import com.gurella.engine.scene.manager.ComponentManager.ComponentFamily;
import com.gurella.engine.scene.manager.ComponentTypePredicate;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;

public class TagManager extends SceneSystem2 implements ComponentActivityListener, Poolable {
	private static final ComponentFamily family = new ComponentFamily(new ComponentTypePredicate(TagComponent.class));

	private IntMap<ArrayExt<SceneNode2>> nodesByTag = new IntMap<ArrayExt<SceneNode2>>();
	private IntMap<FamilyNodes> families = new IntMap<FamilyNodes>();

	@Override
	protected void onActivate() {
		super.onActivate();
		ComponentManager componentManager = getScene().componentManager;
		componentManager.registerComponentFamily(family);
		ImmutableArray<? extends TagComponent> components = componentManager.getComponents(family);
		for (int i = 0; i < components.size(); i++) {
			componentActivated(components.get(i));
		}
	}

	@Override
	protected void onDeactivate() {
		super.onDeactivate();
		getScene().componentManager.unregisterComponentFamily(family);
		for (FamilyNodes familyNodes : families.values()) {
			Pools.free(familyNodes);
		}
		families.clear();
		nodesByTag.clear();
	}

	@Override
	public void componentActivated(SceneNodeComponent2 component) {
		if (component instanceof TagComponent) {
			TagComponent tagComponent = (TagComponent) component;
			updateFamilies(tagComponent);
			Bits tags = tagComponent._tags;
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
	public void componentDeactivated(SceneNodeComponent2 component) {
		if (component instanceof TagComponent) {
			TagComponent tagComponent = (TagComponent) component;
			removeFromFamilies(tagComponent);
			Bits tags = tagComponent._tags;
			int tagId = tags.nextSetBit(0);
			while (tagId != -1) {
				getNodesArray(tagId).removeValue(component.getNode(), true);
				tagId = tags.nextSetBit(tagId);
			}
		}
	}

	private void removeFromFamilies(TagComponent component) {
		SceneNode2 node = component.getNode();
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

	private ArrayExt<SceneNode2> getNodesArray(int tagId) {
		ArrayExt<SceneNode2> nodes = nodesByTag.get(tagId);

		if (nodes == null) {
			nodes = new ArrayExt<SceneNode2>();
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

	public ImmutableArray<SceneNode2> getNodes(Tag tag) {
		int tagId = tag.id;
		ArrayExt<SceneNode2> nodes = nodesByTag.get(tagId);
		return nodes == null ? ImmutableArray.<SceneNode2> empty() : nodes.immutable();
	}

	public boolean belongsToFamily(SceneNode2 node, TagFamily family) {
		return getNodes(family).contains(node, true);
	}

	public ImmutableArray<SceneNode2> getNodes(TagFamily family) {
		FamilyNodes familyNodes = families.get(family.id);
		return familyNodes == null ? ImmutableArray.<SceneNode2> empty() : familyNodes.immutableNodes;
	}

	public SceneNode2 getSingleNodeByTag(Tag tag) {
		int tagId = tag.id;
		ArrayExt<SceneNode2> nodes = nodesByTag.get(tagId);

		if (nodes == null || nodes.size == 0) {
			return null;
		} else {
			return nodes.get(0);
		}
	}

	@Override
	public void reset() {
		for (FamilyNodes familyNodes : families.values()) {
			Pools.free(familyNodes);
		}
		families.clear();
		nodesByTag.clear();
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
			Bits tags = component._tags;

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
		private final Array<SceneNode2> nodes = new Array<SceneNode2>();
		private final ImmutableArray<SceneNode2> immutableNodes = new ImmutableArray<SceneNode2>(nodes);

		private void handle(TagComponent component) {
			SceneNode2 node = component.getNode();
			boolean belongsToFamily = family.matches(component);
			boolean containsNode = nodes.contains(node, true);
			if (belongsToFamily && !containsNode) {
				nodes.add(node);
			} else if (!belongsToFamily && containsNode) {
				nodes.removeValue(node, true);
			}
		}

		private void remove(SceneNode2 node) {
			nodes.removeValue(node, true);
		}

		@Override
		public void reset() {
			family = null;
			nodes.clear();
		}
	}
}
