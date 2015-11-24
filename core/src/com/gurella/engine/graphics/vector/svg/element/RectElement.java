package com.gurella.engine.graphics.vector.svg.element;

import com.gurella.engine.graphics.vector.Path;
import com.gurella.engine.graphics.vector.svg.property.PropertyType;
import com.gurella.engine.graphics.vector.svg.property.value.Length;

public class RectElement extends ShapeElement {
	public RectElement() {
		super(ElementType.rect);
	}

	public float getX() {
		return this.<Length> getPropertyOrDefault(PropertyType.x).getPixels();
	}

	public float getY() {
		return this.<Length> getPropertyOrDefault(PropertyType.y).getPixels();
	}

	public float getWidth() {
		return this.<Length> getProperty(PropertyType.width).getPixels();
	}

	public float getHeight() {
		return this.<Length> getProperty(PropertyType.height).getPixels();
	}

	public float getRx() {
		Length radiusX = getProperty(PropertyType.rx);

		if (radiusX == null) {
			radiusX = getProperty(PropertyType.ry);
		}

		float radius = radiusX == null ? 0 : radiusX.getPixels();
		float halfWidth = getWidth() * 0.5f;
		return radius < 0 ? 0 : radius > halfWidth ? halfWidth : radius;
	}

	public float getRy() {
		Length radiusY = getProperty(PropertyType.ry);

		if (radiusY == null) {
			radiusY = getProperty(PropertyType.rx);
		}

		float radius = radiusY == null ? 0 : radiusY.getPixels();
		float halfHeight = getHeight() * 0.5f;
		return radius < 0 ? 0 : radius > halfHeight ? halfHeight : radius;
	}

	@Override
	protected void initPath(Path out) {
		out.roundedRect(getX(), getY(), getWidth(), getHeight(), getRx(), getRy());
	}
}
