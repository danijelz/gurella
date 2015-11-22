package com.gurella.engine.graph.spatial.bvh;

import java.util.Arrays;

import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.ObjectSet.ObjectSetIterator;
import com.gurella.engine.graph.layer.Layer;
import com.gurella.engine.graph.spatial.Spatial;
import com.gurella.engine.utils.ValueUtils;

public class Bvh {
	public BvhNode rootNode;
	public int nodeCount = 0;

	int LEAF_OBJ_MAX;
	final ObjectSet<BvhNode> refitNodes = new ObjectSet<BvhNode>();
	private Array<BvhNode> sweepNodes = new Array<BvhNode>(BvhNode.class);

	/**
	 * 
	 * @param objects
	 * @param LEAF_OBJ_MAX WARNING! currently this must be 1 to use dynamic BVH update
	 */
	public Bvh(Array<BvhSpatial> objects, int LEAF_OBJ_MAX) {
		this.LEAF_OBJ_MAX = LEAF_OBJ_MAX;
		init(objects);
	}
	
	public Bvh(int LEAF_OBJ_MAX) {
		this.LEAF_OBJ_MAX = LEAF_OBJ_MAX;
	}

	public void init(Array<BvhSpatial> objects) {
		if(rootNode != null) {
			throw new IllegalStateException("Bvh already initialized.");
		}
		
		if (objects.size > 0) {
			rootNode = new BvhNode(this, objects);
		} else {
			rootNode = new BvhNode(this);
			// it's a leaf, so give it an empty object list
			rootNode.spatials = new Array<BvhSpatial>(BvhSpatial.class);
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
	
	public void traverseSpatials(Ray ray, Array<Spatial> result, Layer... layers) {
		traverseSpatials(rootNode, new RayHitTest(ray), result, layers);
	}

	public void traverseSpatials(Frustum frustum, Array<Spatial> result, Layer... layers) {
		traverseSpatials(rootNode, new FrustumHitTest(frustum), result, layers);
	}

	public void traverseSpatials(BoundingBox volume, Array<Spatial> result, Layer... layers) {
		traverseSpatials(rootNode, new BoundingBoxHitTest(volume), result, layers);
	}
	
	private void traverseSpatials(BvhNode node, NodeTest hitTest, Array<Spatial> result, Layer... layers) {
		if (node == null) {
			return;
		}

		if (hitTest.intersects(node.box)) {
			if (node.spatials != null) {
				for(int i = 0; i < node.spatials.size; i++) {
					BvhSpatial spatial = node.spatials.get(i);
					if(ValueUtils.isEmpty(layers) || Arrays.binarySearch(layers, spatial.layer) >= 0) {
						result.add(spatial);
					}
				}
			}
			traverseSpatials(node.left, hitTest, result);
			traverseSpatials(node.right, hitTest, result);
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
				refitNodes.remove(sweepNodes.get(i));
			}

			for (int i = 0; i < sweepNodes.size; i++) {
				sweepNodes.get(i).tryRotate();
			}
			
			sweepNodes.clear();
		}
	}

	private void initSweepNodes() {
		int maxDepth = 0;
		ObjectSetIterator<BvhNode> iterator = refitNodes.iterator();
		while (iterator.hasNext()) {
			BvhNode node = iterator.next();
			if (maxDepth < node.depth) {
				sweepNodes.clear();
				maxDepth = node.depth;
			} else if (maxDepth == node.depth) {
				sweepNodes.add(node);
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
		rootNode = null;//TODO add to pool
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

	//TODO poolable
	private static class RayHitTest implements NodeTest {
		Ray ray;
		final Vector3 center = new Vector3();
		final Vector3 dimensions = new Vector3();

		public RayHitTest(Ray ray) {
			this.ray = ray;
		}

		@Override
		public boolean intersects(BoundingBox box) {
			box.getCenter(center);
			box.getDimensions(dimensions);
			return Intersector.intersectRayBoundsFast(ray, center, dimensions);
		}
	}

	//TODO poolable
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
