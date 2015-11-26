package com.gurella.engine.graph.manager;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.graph.GraphListenerSystem;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.utils.ImmutableArray;

//TODO add nodeAdded and nodeRemoved...
public class SceneNodeManager extends GraphListenerSystem {
	private IntMap<FamilyNodes> families = new IntMap<FamilyNodes>();

	@Override
	public void componentAdded(SceneNodeComponent component) {
		handleComponent(component);
	}

	private void handleComponent(SceneNodeComponent component) {
		SceneNode node = component.getNode();
		for (FamilyNodes familyNodes : families.values()) {
			familyNodes.handle(node);
		}
	}

	@Override
	public void componentRemoved(SceneNodeComponent component) {
		handleComponent(component);
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		handleComponent(component);
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		handleComponent(component);
	}

	public void registerFamily(SceneNodeFamily family) {
		int familyId = family.id;
		if (families.containsKey(familyId)) {
			return;
		}
		FamilyNodes familyNodes = Pools.obtain(FamilyNodes.class);
		families.put(familyId, familyNodes);

		ImmutableArray<SceneNode> nodes = getGraph().allNodes;
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

	public ImmutableArray<SceneNode> getNodes(SceneNodeFamily family) {
		FamilyNodes familyNodes = families.get(family.id);
		return familyNodes == null ? ImmutableArray.<SceneNode> empty() : familyNodes.immutableNodes;
	}
	
	@Override
	protected void resetted() {
		for (FamilyNodes familyNodes : families.values()) {
			Pools.free(familyNodes);
		}
		families.clear();
	}

	public static final class SceneNodeFamily {
		private static int INDEXER = 0;

		public final int id;
		public final Comparator<SceneNode> comparator;
		public final Predicate<SceneNode> predicate;

		public SceneNodeFamily(Predicate<SceneNode> predicate) {
			id = INDEXER++;
			this.predicate = predicate;
			comparator = null;
		}

		public SceneNodeFamily(Predicate<SceneNode> predicate, Comparator<SceneNode> comparator) {
			id = INDEXER++;
			this.predicate = predicate;
			this.comparator = comparator;
		}
	}

	private static class FamilyNodes implements Poolable {
		private SceneNodeFamily family;

		private final Array<SceneNode> nodes = new Array<SceneNode>();
		private final ImmutableArray<SceneNode> immutableNodes = new ImmutableArray<SceneNode>(nodes);

		private void handle(SceneNode node) {
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
