package com.gurella.engine.math.geometry.shape;

import com.badlogic.gdx.utils.Array;

public class CompositeShape extends Shape {
	private Array<Shape> shapes = new Array<Shape>();

	public void add(Shape shape) {
		shapes.add(shape);
		bounds.ext(shape.getBounds());
	}

	public boolean remove(Shape shape) {
		if (shapes.removeValue(shape, true)) {
			boundsDirty = true;
			return true;
		} else {
			return false;
		}
	}
}
