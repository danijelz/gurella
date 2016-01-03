package com.gurella.engine.scene.spatial.bvh;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.gurella.engine.scene.movement.TransformComponent;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.utils.SynchronizedPools;

public class BvhSpatial extends Spatial {
	BvhNode node;
	private final Vector3 translate = new Vector3();
	private final BoundingBox bounds = new BoundingBox();

	public static BvhSpatial obtain(RenderableComponent renderableComponent) {
		BvhSpatial spatial = SynchronizedPools.obtain(BvhSpatial.class);
		spatial.init(renderableComponent);
		return spatial;
	}

	Vector3 getPosition() {
		TransformComponent transformComponent = renderableComponent.getTransformComponent();
		return transformComponent == null ? translate.setZero() : transformComponent.getWorldTranslation(translate);
	}

	public BoundingBox getBounds() {
		renderableComponent.getBounds(bounds.inf());
		return bounds;
	}

	public void free() {
		SynchronizedPools.free(this);
	}

	@Override
	public void reset() {
		super.reset();
		bounds.inf();
		translate.setZero();
	}
}
