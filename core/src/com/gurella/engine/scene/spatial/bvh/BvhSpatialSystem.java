package com.gurella.engine.scene.spatial.bvh;

import java.util.Iterator;

import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.scene.spatial.SpatialSystem;
import com.gurella.engine.subscriptions.scene.update.CleanupUpdateListener;

public class BvhSpatialSystem extends SpatialSystem<BvhSpatial> implements CleanupUpdateListener {
	private final Bvh bvh = new Bvh(1);

	public BvhSpatialSystem(Scene scene) {
		super(scene);
	}

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
		bvh.traverse(bounds, predicate, out);
	}

	@Override
	protected void doGetSpatials(Frustum frustum, Array<Spatial> out, Predicate<RenderableComponent> predicate) {
		bvh.traverse(frustum, predicate, out);
	}

	@Override
	protected void doGetSpatials(Ray ray, Array<Spatial> out, Predicate<RenderableComponent> predicate) {
		bvh.traverse(ray, predicate, out);
	}

	@Override
	protected void doGetSpatials(Ray ray, float maxDistance, Array<Spatial> out,
			Predicate<RenderableComponent> predicate) {
		bvh.traverse(ray, maxDistance, predicate, out);
	}

	@Override
	protected BvhSpatial createSpatial(RenderableComponent drawableComponent) {
		return BvhSpatial.obtain(drawableComponent);
	}

	@Override
	protected void initSpatials() {
		bvh.init(addedSpatials.values().toArray());
		addedSpatials.clear();
	}

	@Override
	protected void clearSpatials() {
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
