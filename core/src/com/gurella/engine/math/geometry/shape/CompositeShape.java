package com.gurella.engine.math.geometry.shape;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedSet;

public class CompositeShape extends Shape {
	private OrderedSet<Shape> shapes = new OrderedSet<Shape>();

	public void add(Shape shape) {
		shapes.add(shape);
		shape.parent = this;
		shape.boundsDirty = true;
		shape.transformDirty = true;
		bounds.ext(shape.getBounds());
	}

	public boolean remove(Shape shape) {
		if (shapes.remove(shape)) {
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
		Array<Shape> items = shapes.orderedItems();
		for (int i = 0, n = items.size; i < n; i++) {
			Shape shape = items.get(i);
			shape.boundsDirty = true;
			shape.transformDirty = true;
			if (shape instanceof CompositeShape) {
				((CompositeShape) shape).markTransformDirty();
			}
		}
	}

	@Override
	protected void updateBounds() {
		Array<Shape> items = shapes.orderedItems();
		for (int i = 0, n = items.size; i < n; i++) {
			bounds.ext(items.get(i).getBounds());
		}
	}
}
