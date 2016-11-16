package com.gurella.engine.math.geometry.shape;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.editor.property.PropertyEditorDescriptor;

public class CompositeShape extends Shape {
	@PropertyEditorDescriptor(genericTypes = { Shape.class })
	private Array<Shape> shapes = new Array<Shape>();

	public void add(Shape shape) {
		shapes.add(shape);
		boundsDirty = true;
		shape.parent = this;
		shape.boundsDirty = true;
		shape.transformDirty = true;
		bounds.ext(shape.getBounds());
	}

	public boolean remove(Shape shape) {
		if (shapes.removeValue(shape, true)) {
			boundsDirty = true;
			shape.boundsDirty = true;
			shape.transformDirty = true;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void setLocalTransform(Matrix4 localTransform) {
		super.setLocalTransform(localTransform);
		markTransformDirty();
	}

	private void markTransformDirty() {
		for (int i = 0, n = shapes.size; i < n; i++) {
			Shape shape = shapes.get(i);
			shape.boundsDirty = true;
			shape.transformDirty = true;
			if (shape instanceof CompositeShape) {
				((CompositeShape) shape).markTransformDirty();
			}
		}
	}

	@Override
	protected void updateBounds(BoundingBox bounds) {
		for (int i = 0, n = shapes.size; i < n; i++) {
			bounds.ext(shapes.get(i).getBounds());
		}
	}
}
