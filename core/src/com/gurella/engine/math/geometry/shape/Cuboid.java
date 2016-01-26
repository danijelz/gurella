package com.gurella.engine.math.geometry.shape;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Cuboid extends Shape {
	public float centerX;
	public float centeryY;
	public float centerZ;
	public float width;
	public float height;
	public float depth;
	public float halfWidth;
	public float halfHeigth;
	public float halfDepth;

	public Cuboid(float centerX, float centeryY, float centerZ, float width, float height, float depth) {
		this.centerX = centerX;
		this.centeryY = centeryY;
		this.centerZ = centerZ;
		this.width = width;
		this.height = height;
		this.depth = depth;
		halfWidth = width / 2;
		halfHeigth = height / 2;
		halfDepth = depth / 2;
		bounds.min.set(centerX - halfWidth, centeryY - halfHeigth, centerZ - halfDepth);
		bounds.max.set(centerX + halfWidth, centeryY + halfHeigth, centerZ + halfDepth);
	}

	public Cuboid(BoundingBox bounds) {
		this.bounds.set(bounds);
		Vector3 min = bounds.min;
		Vector3 max = bounds.max;
		this.width = max.x - min.x;
		this.height = max.y - min.y;
		this.depth = max.z - min.z;
		halfWidth = width / 2;
		halfHeigth = height / 2;
		halfDepth = depth / 2;
		this.centerX = (min.x + max.x) * 0.5f;
		this.centeryY = (min.y + max.y) * 0.5f;
		this.centerZ = (min.z + max.z) * 0.5f;
	}
}
