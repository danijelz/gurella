package com.gurella.engine.scene.spatial.bvh;

import java.util.Iterator;

import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.scene.spatial.SpatialPartitioningSystem;
import com.gurella.engine.subscriptions.scene.update.CleanupUpdateListener;

public class BvhSpatialPartitioningSystem extends SpatialPartitioningSystem<BvhSpatial>
		implements CleanupUpdateListener {
	private Bvh bvh = new Bvh(1);

	@Override
	protected void doUpdateSpatials() {
		Iterator<BvhSpatial> iterator = removedSpatials.values().iterator();
		while (iterator.hasNext()) {
			BvhSpatial spatial = iterator.next();
			bvh.removeObject(spatial);
			spatial.free();
		}

		iterator = addedSpatials.values().iterator();
		while (iterator.hasNext()) {
			BvhSpatial spatial = iterator.next();
			bvh.addObject(spatial);
		}

		iterator = dirtySpatials.values().iterator();
		while (iterator.hasNext()) {
			BvhSpatial spatial = iterator.next();
			spatial.node.refitObjectChanged();
		}
	}

	@Override
	protected void doGetSpatials(BoundingBox bounds, Array<Spatial> out, Predicate<RenderableComponent> predicate) {
		bvh.traverse(bounds, out, predicate);
	}

	@Override
	protected void doGetSpatials(Frustum frustum, Array<Spatial> out, Predicate<RenderableComponent> predicate) {
		bvh.traverse(frustum, out, predicate);
	}

	@Override
	protected void doGetSpatials(Ray ray, Array<Spatial> out, Predicate<RenderableComponent> predicate) {
		bvh.traverse(ray, out, predicate);
	}

	@Override
	protected void doGetSpatials(Ray ray, float maxDistance, Array<Spatial> out,
			Predicate<RenderableComponent> predicate) {
		bvh.traverse(ray, maxDistance, out, predicate);
	}

	@Override
	protected BvhSpatial createSpatial(RenderableComponent drawableComponent) {
		return BvhSpatial.obtain(drawableComponent);
	}

	@Override
	protected void doInitSpatials() {
		bvh.init(addedSpatials.values().toArray());
		addedSpatials.clear();
	}

	@Override
	protected void doClearSpatials() {
		bvh.clear();
	}

	@Override
	public void onCleanupUpdate() {
		if (bvh.refitNodes.size > 0) {
			bvh.optimize();
		}
	}

	@Override
	public BoundingBox getBounds(BoundingBox out) {
		return out.set(bvh.rootNode.box);
	}
}
