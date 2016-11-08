package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.debug.DebugRenderable;
import com.gurella.engine.scene.renderable.debug.WireframeShader;

public abstract class RenderableComponent3d extends RenderableComponent implements DebugRenderable {
	protected abstract ModelInstance getModelInstance();

	@Override
	protected void updateGeometry() {
		ModelInstance instance = getModelInstance();
		if (instance == null) {
			return;
		}

		if (transformComponent == null) {
			instance.transform.idt();
		} else {
			transformComponent.getWorldTransform(instance.transform);
			instance.calculateTransforms();
		}
	}

	@Override
	protected void doRender(GenericBatch batch) {
		ModelInstance instance = getModelInstance();
		if (instance != null) {
			batch.render(instance);
		}
	}

	@Override
	protected void calculateBounds(BoundingBox bounds) {
		ModelInstance instance = getModelInstance();
		if (instance != null) {
			instance.extendBoundingBox(bounds);
		}
	}

	@Override
	public void debugRender(DebugRenderContext context) {
		ModelInstance instance = getModelInstance();
		if (instance != null) {
			context.batch.render(instance, WireframeShader.getInstance());
		}
	}
}
