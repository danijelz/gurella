package com.gurella.engine.math.geometry.shape;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Cylinder extends Shape {
	float width;
	float height;
	float depth;

	public Cylinder() {
	}

	public Cylinder(Vector3 extents) {
		width = extents.x;
		height = extents.y;
		depth = extents.z;
	}

	public Cylinder(float extents) {
		width = extents;
		height = extents;
		depth = extents;
	}

	public Cylinder(float width, float height, float depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
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

	public float getDepth() {
		return depth;
	}

	public void setDepth(float depth) {
		this.depth = depth;
		boundsDirty = true;
	}

	@Override
	protected void updateBounds(BoundingBox bounds) {
		float x = width * 0.5f;
		float y = height * 0.5f;
		float z = depth * 0.5f;
		bounds.ext(x, y, z);
		bounds.ext(-x, -y, -z);
	}
}
