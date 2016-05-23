package com.gurella.engine.scene.spatial.bvh;

import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.ObjectSet.ObjectSetIterator;
import com.gurella.engine.scene.layer.LayerMask;
import com.gurella.engine.scene.spatial.Spatial;

public class Bvh {
	public BvhNode rootNode;
	public int nodeCount = 0;

	int LEAF_OBJ_MAX;
	final ObjectSet<BvhNode> refitNodes = new ObjectSet<BvhNode>();
	private Array<BvhNode> sweepNodes = new Array<BvhNode>(BvhNode.class);

	/**
	 * 
	 * @param objects
	 * @param LEAF_OBJ_MAX
	 *            WARNING! currently this must be 1 to use dynamic BVH update
	 */
	public Bvh(Array<BvhSpatial> objects, int LEAF_OBJ_MAX) {
		this.LEAF_OBJ_MAX = LEAF_OBJ_MAX;
		init(objects);
	}

	public Bvh(int LEAF_OBJ_MAX) {
		this.LEAF_OBJ_MAX = LEAF_OBJ_MAX;
	}

	public void init(Array<BvhSpatial> objects) {
		if (rootNode != null) {
			throw new IllegalStateException("Bvh already initialized.");
		}

		if (objects.size > 0) {
			rootNode = new BvhNode(this, objects);
		} else {
			rootNode = new BvhNode(this);
			// it's a leaf, so give it an empty object list
			rootNode.spatials = new Array<BvhSpatial>(BvhSpatial.class);// TODO garbage
		}
	}

	public void traverse(Ray ray, Array<BvhNode> result) {
		traverse(rootNode, new RayHitTest(ray), result);
	}

	public void traverse(Frustum frustum, Array<BvhNode> result) {
		traverse(rootNode, new FrustumHitTest(frustum), result);
	}

	public void traverse(BoundingBox volume, Array<BvhNode> result) {
		traverse(rootNode, new BoundingBoxHitTest(volume), result);
	}

	private void traverse(BvhNode node, NodeTest hitTest, Array<BvhNode> result) {
		if (node == null) {
			return;
		}

		if (hitTest.intersects(node.box)) {
			result.add(node);
			traverse(node.left, hitTest, result);
			traverse(node.right, hitTest, result);
		}
	}

	public void traverseSpatials(Ray ray, Array<Spatial> result, LayerMask mask) {
		traverseSpatials(rootNode, new RayHitTest(ray), result, mask);
	}

	public void traverseSpatials(Ray ray, float maxDistance, Array<Spatial> result, LayerMask mask) {
		traverseSpatials(rootNode, new PickRayHitTest(ray, maxDistance), result, mask);
	}

	public void traverseSpatials(Frustum frustum, Array<Spatial> result, LayerMask mask) {
		traverseSpatials(rootNode, new FrustumHitTest(frustum), result, mask);
	}

	public void traverseSpatials(BoundingBox volume, Array<Spatial> result, LayerMask mask) {
		traverseSpatials(rootNode, new BoundingBoxHitTest(volume), result, mask);
	}

	private void traverseSpatials(BvhNode node, NodeTest hitTest, Array<Spatial> result, LayerMask mask) {
		if (node == null) {
			return;
		}

		if (hitTest.intersects(node.box)) {
			if (node.spatials != null) {
				for (int i = 0; i < node.spatials.size; i++) {
					BvhSpatial spatial = node.spatials.get(i);
					if (mask == null || mask.isValid(spatial.layer)) {
						result.add(spatial);
					}
				}
			}

			traverseSpatials(node.left, hitTest, result, mask);
			traverseSpatials(node.right, hitTest, result, mask);
		}
	}

	// Call this to batch-optimize any object-changes notified through
	// ssBVHNode.refit_ObjectChanged(..). For example, in a game-loop,
	// call this once per frame.
	public synchronized void optimize() {
		if (LEAF_OBJ_MAX != 1) {
			throw new IllegalStateException("In order to use optimize, you must set LEAF_OBJ_MAX=1");
		}

		while (refitNodes.size > 0) {
			initSweepNodes();

			for (int i = 0; i < sweepNodes.size; i++) {
				BvhNode node = sweepNodes.get(i);
				node.tryRotate();
				if (!node.isLeaf()) {
				}
			}

			sweepNodes.clear();
		}
	}

	private void initSweepNodes() {
		int maxDepth = 0;
		ObjectSetIterator<BvhNode> iterator = refitNodes.iterator();
		while (iterator.hasNext()) {
			BvhNode node = iterator.next();
			maxDepth = Math.max(maxDepth, node.depth);
		}

		iterator = refitNodes.iterator();
		while (iterator.hasNext()) {
			BvhNode node = iterator.next();
			if (maxDepth == node.depth) {
				iterator.remove();
				sweepNodes.add(node);
			} else {
			}
		}
	}

	public void addObject(BvhSpatial newSpatial) {
		rootNode.addObject(newSpatial);
	}

	public void removeObject(BvhSpatial newSpatial) {
		newSpatial.node.removeObject(newSpatial);
	}

	public int countBVHNodes() {
		return rootNode.countBvhNodes();
	}

	public void clear() {
		rootNode = null;// TODO add to pool
	}

	// TODO poolable
	private interface NodeTest {
		boolean intersects(BoundingBox box);
	}

	private static class BoundingBoxHitTest implements NodeTest {
		BoundingBox volume;

		public BoundingBoxHitTest(BoundingBox volume) {
			this.volume = volume;
		}

		@Override
		public boolean intersects(BoundingBox box) {
			return box.intersects(volume);
		}
	}

	// TODO poolable
	private static class RayHitTest implements NodeTest {
		Ray ray;

		public RayHitTest(Ray ray) {
			this.ray = ray;
		}

		@Override
		public boolean intersects(BoundingBox box) {
			return Intersector.intersectRayBoundsFast(ray, box);
		}
	}

	// TODO poolable
	private static class PickRayHitTest implements NodeTest {
		Ray ray;
		float maxDistance2;
		final Vector3 intersection = new Vector3();

		public PickRayHitTest(Ray ray, float maxDistance) {
			this.ray = ray;
			this.maxDistance2 = maxDistance * maxDistance;
		}

		@Override
		public boolean intersects(BoundingBox box) {
			return Intersector.intersectRayBounds(ray, box, intersection)
					&& ray.origin.dst2(intersection) <= maxDistance2;
		}
	}

	// TODO poolable
	private static class FrustumHitTest implements NodeTest {
		Frustum frustum;
		final Vector3 center = new Vector3();
		final Vector3 dimensions = new Vector3();

		public FrustumHitTest(Frustum frustum) {
			this.frustum = frustum;
		}

		@Override
		public boolean intersects(BoundingBox box) {
			box.getCenter(center);
			box.getDimensions(dimensions);
			return frustum.boundsInFrustum(center, dimensions);
		}
	}
}
