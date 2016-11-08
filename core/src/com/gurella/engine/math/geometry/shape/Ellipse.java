package com.gurella.engine.math.geometry.shape;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Ellipse extends Shape {
	float width;
	float height;

	public Ellipse() {
	}

	public Ellipse(Vector2 extents) {
		width = extents.x;
		height = extents.y;
	}

	public Ellipse(float extents) {
		width = extents;
		height = extents;
	}

	public Ellipse(float width, float height) {
		this.width = width;
		this.height = height;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
		boundsDirty = true;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
		boundsDirty = true;
	}

	@Override
	protected void updateBounds(BoundingBox bounds) {
		float x = width * 0.5f;
		float y = height * 0.5f;
		bounds.ext(x, y, 0);
		bounds.ext(-x, -y, 0);
	}
}
