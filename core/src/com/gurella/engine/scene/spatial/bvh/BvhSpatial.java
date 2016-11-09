package com.gurella.engine.scene.spatial.bvh;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.scene.transform.TransformComponent;

public class BvhSpatial extends Spatial {
	BvhNode node;
	private final BoundingBox bounds = new BoundingBox().inf();

	public static BvhSpatial obtain(RenderableComponent renderableComponent) {
		BvhSpatial spatial = PoolService.obtain(BvhSpatial.class);
		spatial.init(renderableComponent);
		return spatial;
	}

	float getPositionX() {
		TransformComponent transformComponent = renderable.getTransformComponent();
		return transformComponent == null ? 0 : transformComponent.getWorldTranslationX();
	}

	float getPositionY() {
		TransformComponent transformComponent = renderable.getTransformComponent();
		return transformComponent == null ? 0 : transformComponent.getWorldTranslationY();
	}

	float getPositionZ() {
		TransformComponent transformComponent = renderable.getTransformComponent();
		return transformComponent == null ? 0 : transformComponent.getWorldTranslationZ();
	}

	public BoundingBox getBounds() {
		renderable.getBounds(bounds.inf());
		TransformComponent transformComponent = renderable.getTransformComponent();
		if (transformComponent != null) {
			transformComponent.transformBoundsToWorld(bounds);
		}
		return bounds;
	}

	public void free() {
		PoolService.free(this);
	}

	@Override
	public void reset() {
		super.reset();
		bounds.inf(); // TODO remove
	}
}
