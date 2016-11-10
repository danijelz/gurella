package com.gurella.engine.scene.spatial.bvh;

import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.ObjectSet.ObjectSetIterator;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.scene.spatial.Spatial;

public class Bvh {
	public BvhNode rootNode;

	int maxLeafSpatials;
	final ObjectSet<BvhNode> refitNodes = new ObjectSet<BvhNode>();
	private Array<BvhNode> sweepNodes = new Array<BvhNode>(BvhNode.class);

	/**
	 * @param objects
	 * @param maxLeafSpatials
	 *            WARNING! currently this must be 1 to use dynamic BVH update
	 */
	public Bvh(Array<BvhSpatial> objects, int maxLeafSpatials) {
		this.maxLeafSpatials = maxLeafSpatials;
		init(objects);
	}

	public Bvh(int maxLeafSpatials) {
		this.maxLeafSpatials = maxLeafSpatials;
	}

	public void init(Array<BvhSpatial> objects) {
		if (rootNode != null) {
			throw new IllegalStateException("Bvh already initialized.");
		}

		if (objects.size > 0) {
			rootNode = new BvhNode(this, objects);
		} else {
			rootNode = new BvhNode(this);
			rootNode.spatials = new Array<BvhSpatial>(BvhSpatial.class);// TODO garbage
		}
	}

	public Array<Spatial> traverse(Ray ray, Array<Spatial> result) {
		RayIntersectionTest intersectionTest = PoolService.obtain(RayIntersectionTest.class);
		intersectionTest.ray.set(ray);
		traverse(rootNode, intersectionTest, result);
		PoolService.free(intersectionTest);
		return result;
	}

	public Array<Spatial> traverse(Ray ray, float maxDistance, Array<Spatial> result) {
		RayDistanceIntersectionTest intersectionTest = PoolService.obtain(RayDistanceIntersectionTest.class);
		intersectionTest.set(ray, maxDistance);
		traverse(rootNode, intersectionTest, result);
		PoolService.free(intersectionTest);
		return result;
	}

	public Array<Spatial> traverse(Frustum frustum, Array<Spatial> result) {
		FrustumIntersectionTest intersectionTest = PoolService.obtain(FrustumIntersectionTest.class);
		intersectionTest.setFrustum(frustum);
		traverse(rootNode, intersectionTest, result);
		PoolService.free(intersectionTest);
		return result;
	}

	public Array<Spatial> traverse(BoundingBox volume, Array<Spatial> result) {
		BoundingBoxIntersectionTest intersectionTest = PoolService.obtain(BoundingBoxIntersectionTest.class);
		intersectionTest.volume.set(volume);
		traverse(rootNode, intersectionTest, result);
		PoolService.free(intersectionTest);
		return result;
	}

	private Array<Spatial> traverse(BvhNode node, NodeIntersectionTest hitTest, Array<Spatial> result) {
		if (node != null && hitTest.intersects(node.box)) {
			if (node.spatials != null) {
				result.addAll(node.spatials);
			}

			traverse(node.left, hitTest, result);
			traverse(node.right, hitTest, result);
		}

		return result;
	}

	public Array<Spatial> traverse(Ray ray, Predicate<RenderableComponent> predicate, Array<Spatial> result) {
		RayIntersectionTest intersectionTest = PoolService.obtain(RayIntersectionTest.class);
		intersectionTest.ray.set(ray);
		traverse(rootNode, intersectionTest, predicate, result);
		PoolService.free(intersectionTest);
		return result;
	}

	public Array<Spatial> traverse(Ray ray, float maxDistance, Predicate<RenderableComponent> predicate,
			Array<Spatial> result) {
		RayDistanceIntersectionTest intersectionTest = PoolService.obtain(RayDistanceIntersectionTest.class);
		intersectionTest.set(ray, maxDistance);
		traverse(rootNode, intersectionTest, predicate, result);
		PoolService.free(intersectionTest);
		return result;
	}

	public Array<Spatial> traverse(Frustum frustum, Predicate<RenderableComponent> predicate, Array<Spatial> result) {
		FrustumIntersectionTest intersectionTest = PoolService.obtain(FrustumIntersectionTest.class);
		intersectionTest.setFrustum(frustum);
		traverse(rootNode, intersectionTest, predicate, result);
		PoolService.free(intersectionTest);
		return result;
	}

	public Array<Spatial> traverse(BoundingBox volume, Predicate<RenderableComponent> predicate,
			Array<Spatial> result) {
		BoundingBoxIntersectionTest intersectionTest = PoolService.obtain(BoundingBoxIntersectionTest.class);
		intersectionTest.volume.set(volume);
		traverse(rootNode, intersectionTest, predicate, result);
		PoolService.free(intersectionTest);
		return result;
	}

	public Array<Spatial> traverse(BvhNode node, NodeIntersectionTest hitTest, Predicate<RenderableComponent> predicate,
			Array<Spatial> result) {
		if (node != null && hitTest.intersects(node.box)) {
			if (node.spatials != null) {
				for (int i = 0; i < node.spatials.size; i++) {
					BvhSpatial spatial = node.spatials.get(i);
					if (predicate == null || predicate.evaluate(spatial.renderable)) {
						result.add(spatial);
					}
				}
			}

			traverse(node.left, hitTest, predicate, result);
			traverse(node.right, hitTest, predicate, result);
		}

		return result;
	}

	public synchronized void optimize() {
		if (maxLeafSpatials != 1) {
			throw new IllegalStateException("In order to use optimize, you must set LEAF_OBJ_MAX=1");
		}

		while (refitNodes.size > 0) {
			initSweepNodes();
			for (int i = 0; i < sweepNodes.size; i++) {
				BvhNode node = sweepNodes.get(i);
				node.optimize();
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
			}
		}
	}

	public void addObject(BvhSpatial newSpatial) {
		rootNode.addSpatial(newSpatial);
	}

	public void removeObject(BvhSpatial newSpatial) {
		newSpatial.node.removeObject(newSpatial);
	}

	public void clear() {
		rootNode = null;// TODO add to pool
	}

	private interface NodeIntersectionTest {
		boolean intersects(BoundingBox box);
	}

	public static class BoundingBoxIntersectionTest implements NodeIntersectionTest {
		public final BoundingBox volume = new BoundingBox();

		public BoundingBoxIntersectionTest() {
		}

		public BoundingBoxIntersectionTest(BoundingBox volume) {
			this.volume.set(volume);
		}

		@Override
		public boolean intersects(BoundingBox box) {
			return box.intersects(volume);
		}
	}

	public static class RayIntersectionTest implements NodeIntersectionTest {
		public final Ray ray = new Ray();
		private final Vector3 center = new Vector3();
		private final Vector3 dimensions = new Vector3();

		public RayIntersectionTest() {
		}

		public RayIntersectionTest(Ray ray) {
			this.ray.set(ray);
		}

		public void setRay(Ray ray) {
			this.ray.set(ray);
		}

		@Override
		public boolean intersects(BoundingBox box) {
			box.getCenter(center);
			box.getDimensions(dimensions);
			return Intersector.intersectRayBoundsFast(ray, center, dimensions);
		}
	}

	public static class RayDistanceIntersectionTest implements NodeIntersectionTest {
		public final Ray ray = new Ray();
		private final Vector3 center = new Vector3();
		private final Vector3 dimensions = new Vector3();
		private final Vector3 intersection = new Vector3();
		private float maxDistance2;

		public RayDistanceIntersectionTest() {
		}

		public RayDistanceIntersectionTest(Ray ray, float maxDistance) {
			this.ray.set(ray);
			this.maxDistance2 = maxDistance * maxDistance;
		}

		public void setRay(Ray ray) {
			this.ray.set(ray);
		}

		public void setMaxDistance(float maxDistance) {
			this.maxDistance2 = maxDistance * maxDistance;
		}

		public void set(Ray ray, float maxDistance) {
			this.ray.set(ray);
			this.maxDistance2 = maxDistance * maxDistance;
		}

		@Override
		public boolean intersects(BoundingBox box) {
			box.getCenter(center);
			box.getDimensions(dimensions);
			return Intersector.intersectRayBoundsFast(ray, center, dimensions)
					&& ray.origin.dst2(intersection) <= maxDistance2;
		}
	}

	public static class FrustumIntersectionTest implements NodeIntersectionTest {
		public final Frustum frustum = new Frustum();
		private final Vector3 center = new Vector3();
		private final Vector3 dimensions = new Vector3();

		public FrustumIntersectionTest() {
		}

		public FrustumIntersectionTest(Frustum frustum) {
			setFrustum(frustum);
		}

		public void setFrustum(Frustum frustum) {
			for (int i = 0; i < 6; i++) {
				this.frustum.planes[i].set(frustum.planes[i]);
			}

			for (int i = 0; i < 8; i++) {
				this.frustum.planePoints[i].set(frustum.planePoints[i]);
			}
		}

		@Override
		public boolean intersects(BoundingBox box) {
			box.getCenter(center);
			box.getDimensions(dimensions);
			return frustum.boundsInFrustum(center, dimensions);
		}
	}
}
