package com.gurella.engine.scene.tag;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.event.EventService;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.SceneService2;
import com.gurella.engine.scene.manager.ComponentManager.ComponentFamily;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.OrderedIdentitySet;

//TODO EntitySubscription -> TagSubscription
public class TagManager extends SceneService2 implements ComponentActivityListener {
	private static final ComponentFamily tagComponentfamily = ComponentFamily.fromComponentType(TagComponent.class);

	private final Array<Tag> tags = new Array<Tag>();

	private final IntMap<OrderedIdentitySet<SceneNode2>> nodesByTag = new IntMap<OrderedIdentitySet<SceneNode2>>();
	private final IntMap<FamilyNodes> families = new IntMap<FamilyNodes>();

	private final TagAddedEvent tagAddedEvent = new TagAddedEvent();
	private final TagRemovedEvent tagRemovedEvent = new TagRemovedEvent();

	public TagManager(Scene scene) {
		super(scene);
	}

	@Override
	protected void serviceActivated() {
		scene.componentManager.registerComponentFamily(tagComponentfamily);
	}

	@Override
	protected void serviceDeactivated() {
		scene.componentManager.unregisterComponentFamily(tagComponentfamily);
		for (FamilyNodes familyNodes : families.values()) {
			PoolService.free(familyNodes);
		}
		families.clear();
		nodesByTag.clear();
		tags.clear();
	}

	Tag register(String name) {
		if (isRegistered(name)) {
			return null;
		}

		Tag tag = new Tag(name);
		tags.add(tag);
		return tag;
	}

	Tag unregister(String name) {
		for (int i = 0; i < tags.size; i++) {
			Tag tag = tags.get(i);
			if (tag.name.equals(name)) {
				tags.removeIndex(i);
				return tag;
			}
		}
		return null;
	}

	boolean isRegistered(String name) {
		for (int i = 0; i < tags.size; i++) {
			Tag tag = tags.get(i);
			if (tag.name.equals(name)) {
				return true;
			}
		}

		return false;
	}

	public void registerAll(String[] newTags) {
		for (int i = 0; i < newTags.length; i++) {
			register(newTags[i]);
		}
	}

	public String[] getRegistered() {
		String[] all = new String[tags.size];
		for (int i = 0; i < tags.size; i++) {
			all[i] = tags.get(i).name;
		}
		return all;
	}

	@Override
	public void componentActivated(SceneNodeComponent2 component) {
		if (component instanceof TagComponent) {
			TagComponent tagComponent = (TagComponent) component;
			updateFamilies(tagComponent);
			Bits tags = tagComponent._tags;
			int tagId = tags.nextSetBit(0);
			while (tagId != -1) {
				tagAdded(tagComponent, tagId);
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
				tagRemoved(tagComponent, tagId);
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

		FamilyNodes familyNodes = PoolService.obtain(FamilyNodes.class);
		familyNodes.family = tagFamily;
		families.put(familyId, familyNodes);

		if (scene == null) {
			return;
		}

		ImmutableArray<TagComponent> components = scene.componentManager.getComponents(tagComponentfamily);
		for (int i = 0; i < components.size(); i++) {
			familyNodes.handle(components.get(i));
		}
	}

	public void unregisterFamily(TagFamily family) {
		FamilyNodes familyNodes = families.remove(family.id);
		if (familyNodes != null) {
			PoolService.free(familyNodes);
		}
	}

	private OrderedIdentitySet<SceneNode2> getNodesByTag(int tagId) {
		OrderedIdentitySet<SceneNode2> nodes = nodesByTag.get(tagId);

		if (nodes == null) {
			nodes = new OrderedIdentitySet<SceneNode2>();
			nodesByTag.put(tagId, nodes);
		}

		return nodes;
	}

	public void tagAdded(TagComponent component, int tagId) {
		getNodesByTag(tagId).add(component.getNode());
		tagAddedEvent.component = component;
		tagAddedEvent.tagId = tagId;
		EventService.notify(scene.getInstanceId(), tagAddedEvent);
		tagAddedEvent.component = null;
	}

	public void tagRemoved(TagComponent component, int tagId) {
		nodesByTag.get(tagId).remove(component.getNode());
		tagRemovedEvent.component = component;
		tagRemovedEvent.tagId = tagId;
		EventService.notify(scene.getInstanceId(), tagRemovedEvent);
		tagRemovedEvent.component = null;
	}

	public ImmutableArray<SceneNode2> getNodes(Tag tag) {
		int tagId = tag.id;
		OrderedIdentitySet<SceneNode2> nodes = nodesByTag.get(tagId);
		return nodes == null ? ImmutableArray.<SceneNode2> empty() : nodes.orderedItems();
	}

	public boolean belongsToFamily(SceneNode2 node, TagFamily family) {
		return getNodes(family).contains(node, true);
	}

	public ImmutableArray<SceneNode2> getNodes(TagFamily family) {
		FamilyNodes familyNodes = families.get(family.id);
		return familyNodes == null ? ImmutableArray.<SceneNode2> empty() : familyNodes.nodes.immutable();
	}

	public SceneNode2 getSingleNodeByTag(Tag tag) {
		int tagId = tag.id;
		OrderedIdentitySet<SceneNode2> nodes = nodesByTag.get(tagId);

		if (nodes == null || nodes.size == 0) {
			return null;
		} else {
			return nodes.get(0);
		}
	}

	private static class FamilyNodes implements Poolable {
		private TagFamily family;
		private final ArrayExt<SceneNode2> nodes = new ArrayExt<SceneNode2>();

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
