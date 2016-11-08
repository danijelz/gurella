package com.gurella.engine.math.geometry.shape;

import com.badlogic.gdx.math.collision.BoundingBox;

public class Capsule extends Shape {
	float radius;
	float height;

	public Capsule() {
	}

	public Capsule(float radius, float height) {
		this.radius = radius;
		this.height = height;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
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
		float y = height * 0.5f;
		bounds.ext(radius, y, radius);
		bounds.ext(-radius, -y, -radius);
	}
}
