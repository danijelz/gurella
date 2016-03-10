package com.gurella.engine.scene.manager;

import java.util.Comparator;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.SceneSystem2;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;

public class NodeManager extends SceneSystem2 implements ComponentActivityListener, Poolable {
	private IntMap<FamilyNodes> families = new IntMap<FamilyNodes>();

	@Override
	public void componentActivated(SceneNodeComponent2 component) {
		handleComponent(component);
	}

	@Override
	public void componentDeactivated(SceneNodeComponent2 component) {
		handleComponent(component);
	}

	private void handleComponent(SceneNodeComponent2 component) {
		SceneNode2 node = component.getNode();
		for (FamilyNodes familyNodes : families.values()) {
			familyNodes.handle(node);
		}
	}

	public void registerFamily(SceneNodeFamily family) {
		int familyId = family.id;
		if (families.containsKey(familyId)) {
			return;
		}
		FamilyNodes familyNodes = Pools.obtain(FamilyNodes.class);
		familyNodes.family = family;
		families.put(familyId, familyNodes);

		Scene scene = getScene();
		if (scene == null) {
			return;
		}

		ImmutableArray<SceneNode2> nodes = scene.nodes;
		for (int i = 0; i < nodes.size(); i++) {
			familyNodes.handle(nodes.get(i));
		}
	}

	public void unregisterFamily(SceneNodeFamily family) {
		FamilyNodes familyNodes = families.remove(family.id);
		if (familyNodes != null) {
			Pools.free(familyNodes);
		}
	}

	public boolean belongsToFamily(SceneNode2 node, SceneNodeFamily family) {
		return getNodes(family).contains(node, true);
	}

	public ImmutableArray<SceneNode2> getNodes(SceneNodeFamily family) {
		FamilyNodes familyNodes = families.get(family.id);
		return familyNodes == null ? ImmutableArray.<SceneNode2> empty() : familyNodes.nodes.immutable();
	}

	@Override
	public void reset() {
		for (FamilyNodes familyNodes : families.values()) {
			Pools.free(familyNodes);
		}
		families.clear();
	}

	public static final class SceneNodeFamily {
		private static int INDEXER = 0;

		public final int id;
		public final Comparator<SceneNode2> comparator;
		public final Predicate<SceneNode2> predicate;

		public SceneNodeFamily(Predicate<SceneNode2> predicate) {
			id = INDEXER++;
			this.predicate = predicate;
			comparator = null;
		}

		public SceneNodeFamily(Predicate<SceneNode2> predicate, Comparator<SceneNode2> comparator) {
			id = INDEXER++;
			this.predicate = predicate;
			this.comparator = comparator;
		}
	}

	private static class FamilyNodes implements Poolable {
		private SceneNodeFamily family;
		private final ArrayExt<SceneNode2> nodes = new ArrayExt<SceneNode2>();

		private void handle(SceneNode2 node) {
			boolean belongsToFamily = family.predicate.evaluate(node);
			boolean containsNode = nodes.contains(node, true);
			if (belongsToFamily && !containsNode) {
				nodes.add(node);
				if (family.comparator != null) {
					nodes.sort(family.comparator);
				}
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
