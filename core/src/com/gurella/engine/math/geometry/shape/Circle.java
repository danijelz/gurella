package com.gurella.engine.math.geometry.shape;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Circle extends Shape {
	float radius;

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
		boundsDirty = true;
	}

	@Override
	public boolean contains(float x, float y, float z) {
		Matrix4 transform = getGlobalTransform();
		final float val[] = transform.val;
		float tx = val[Matrix4.M03];
		float ty = val[Matrix4.M13];
		float tz = val[Matrix4.M23];
		return tz == z && Vector3.dst2(tx, ty, tz, x, y, z) <= (radius * radius);
	}

	@Override
	protected void updateBounds(BoundingBox bounds) {
		bounds.ext(radius, radius, 0);
		bounds.ext(-radius, -radius, 0);
	}
}
