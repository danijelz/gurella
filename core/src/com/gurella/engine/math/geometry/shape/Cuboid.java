package com.gurella.engine.math.geometry.shape;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Cuboid extends Shape {
	public final Vector3 center = new Vector3();
	public float width;
	public float height;
	public float depth;
	public float halfWidth;
	public float halfHeigth;
	public float halfDepth;

	public Cuboid(Vector3 center, float width, float height, float depth) {
		this(center.x, center.y, center.z, width, height, depth);
	}

	public Cuboid(float x, float y, float z, float width, float height, float depth) {
		this.center.set(x, y, z);
		this.width = width;
		this.height = height;
		this.depth = depth;
		halfWidth = width / 2;
		halfHeigth = height / 2;
		halfDepth = depth / 2;
		bounds.min.set(center.x - halfWidth, center.y - halfHeigth, center.z - halfDepth);
		bounds.max.set(center.x + halfWidth, center.y + halfHeigth, center.z + halfDepth);
	}

	public Cuboid(BoundingBox bounds) {
		this.bounds.set(bounds);
		this.width = bounds.max.x - bounds.min.x;
		this.height = bounds.max.y - bounds.min.y;
		this.depth = bounds.max.z - bounds.min.z;
		halfWidth = width / 2;
		halfHeigth = height / 2;
		halfDepth = depth / 2;
		bounds.getCenter(center);
	}
}
