package com.gurella.engine.math.geometry.shape;

import com.badlogic.gdx.math.Vector3;

public class Sphere extends Shape {
	Vector3 center = new Vector3();
	float radius;

	public Vector3 getCenter(Vector3 out) {
		return out.set(center);
	}

	public Vector3 getCenter() {
		return center;
	}

	public void setCenter(Vector3 center) {
		this.center.set(center);
		boundsDirty = true;
	}

	public void setCenter(float x, float y, float z) {
		this.center.set(x, y, z);
		boundsDirty = true;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
		boundsDirty = true;
	}
}
