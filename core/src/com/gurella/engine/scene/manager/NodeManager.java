package com.gurella.engine.scene.manager;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.BuiltinSceneSystem;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;

////TODO EntitySubscription -> NodeSubscription
public class NodeManager extends BuiltinSceneSystem implements ComponentActivityListener {
	private IntMap<FamilyNodes> families = new IntMap<FamilyNodes>();

	public NodeManager(Scene scene) {
		super(scene);
	}

	@Override
	protected void serviceDeactivated() {
		for (FamilyNodes familyNodes : families.values()) {
			PoolService.free(familyNodes);
		}
		families.clear();
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		handleComponent(component);
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		handleComponent(component);
	}

	private void handleComponent(SceneNodeComponent component) {
		SceneNode node = component.getNode();
		for (FamilyNodes familyNodes : families.values()) {
			familyNodes.handle(node);
		}
	}

	public void registerFamily(SceneNodeFamily family) {
		int familyId = family.id;
		if (families.containsKey(familyId)) {
			return;
		}
		FamilyNodes familyNodes = PoolService.obtain(FamilyNodes.class);
		familyNodes.family = family;
		families.put(familyId, familyNodes);

		if (scene == null) {
			return;
		}

		ImmutableArray<SceneNode> nodes = scene.nodes;
		for (int i = 0; i < nodes.size(); i++) {
			familyNodes.handle(nodes.get(i));
		}
	}

	public void unregisterFamily(SceneNodeFamily family) {
		FamilyNodes familyNodes = families.remove(family.id);
		if (familyNodes != null) {
			PoolService.free(familyNodes);
		}
	}

	public boolean belongsToFamily(SceneNode node, SceneNodeFamily family) {
		return getNodes(family).contains(node, true);
	}

	public ImmutableArray<SceneNode> getNodes(SceneNodeFamily family) {
		FamilyNodes familyNodes = families.get(family.id);
		return familyNodes == null ? ImmutableArray.<SceneNode> empty() : familyNodes.nodes.immutable();
	}

	public static final class SceneNodeFamily {
		private static int INDEXER = 0;

		public final int id;
		public final Predicate<SceneNode> predicate;

		public SceneNodeFamily(Predicate<SceneNode> predicate) {
			id = INDEXER++;
			this.predicate = predicate;
		}
	}

	private static class FamilyNodes implements Poolable {
		private SceneNodeFamily family;
		private final ArrayExt<SceneNode> nodes = new ArrayExt<SceneNode>();

		private void handle(SceneNode node) {
			boolean belongsToFamily = family.predicate.evaluate(node);
			boolean containsNode = nodes.contains(node, true);
			if (belongsToFamily && !containsNode) {
				nodes.add(node);
			} else if (!belongsToFamily && containsNode) {
				nodes.removeValue(node, true);
			}
		}

		@Override
		public void reset() {
			family = null;
			nodes.clear();
		}
	}
}
