package com.gurella.engine.graph.spatial.bvh;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.gurella.engine.graph.movement.TransformComponent;
import com.gurella.engine.graph.renderable.RenderableComponent;
import com.gurella.engine.graph.spatial.Spatial;
import com.gurella.engine.pools.SynchronizedPools;

public class BvhSpatial extends Spatial {
	BvhNode node;
	private final Vector3 translate = new Vector3();
	private final BoundingBox bounds = new BoundingBox();

	public static BvhSpatial obtain(RenderableComponent renderableComponent) {
		BvhSpatial spatial = SynchronizedPools.obtain(BvhSpatial.class);
		spatial.init(renderableComponent);
		return spatial;
	}

	public void free() {
		SynchronizedPools.free(this);
	}

	Vector3 getPosition() {
		TransformComponent transformComponent = renderableComponent.getTransformComponent();
		return transformComponent == null
				? translate.setZero()
				: transformComponent.getWorldTranslation(translate);
	}

	public BoundingBox getBounds() {
		renderableComponent.getBounds(bounds.inf());
		return bounds;
	}

	@Override
	public void reset() {
		super.reset();
		bounds.inf();
		translate.setZero();
	}
}
