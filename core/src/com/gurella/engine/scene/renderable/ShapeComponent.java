package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.gurella.engine.scene.renderable.shape.ShapeModel;

public class ShapeComponent extends RenderableComponent3d {
	private ShapeModel shape;
	protected transient Material material;

	public ShapeComponent() {
	}

	public ShapeModel getShape() {
		return shape;
	}

	public void setShape(ShapeModel shape) {
		if (this.shape == shape) {
			return;
		}

		if (this.shape != null) {
			this.shape.dispose();
		}

		this.shape = shape;
		if (transformComponent != null && shape != null && shape.getModelInstance() != null) {
			transformComponent.getWorldTransform(shape.getModelInstance().transform);
		}
	}

	@Override
	protected ModelInstance getModelInstance() {
		return shape == null ? null : shape.getModelInstance();
	}

	@Override
	public void reset() {
		super.reset();
		if (shape != null) {
			shape.dispose();
			shape = null;
		}
	}
}
