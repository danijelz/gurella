package com.gurella.engine.geometry.solid;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.geometry.Bounds;
import com.gurella.engine.geometry.Geometry;

public class Cuboid implements Geometry<Vector3> {
	public Vector3 center;
	public float width;
	public float height;
	public float depth;
	public float halfWidth;
	public float halfHeigth;
	public float halfDepth;
	public Bounds<Vector3> bounds;

	public Cuboid(Vector3 center, float width, float height, float depth) {
		this.center = center;
		this.width = width;
		this.height = height;
		this.depth = depth;
		halfWidth = width / 2;
		halfHeigth = height / 2;
		halfDepth = depth / 2;
		bounds = new Bounds<Vector3>();
		bounds.min = new Vector3(center.x - halfWidth, center.y - halfHeigth, center.z - halfDepth);
		bounds.max = new Vector3(center.x + halfWidth, center.y + halfHeigth, center.z + halfDepth);
	}

	public Cuboid(Bounds<Vector3> bounds) {
		this.width = bounds.max.x - bounds.min.x;
		this.height = bounds.max.y - bounds.min.y;
		this.depth = bounds.max.z - bounds.min.z;
		halfWidth = width / 2;
		halfHeigth = height / 2;
		halfDepth = depth / 2;
		this.center = new Vector3(bounds.min.x + halfWidth, bounds.min.y + halfHeigth, bounds.min.z + halfDepth);
		this.bounds = bounds;
	}

	@Override
	public Bounds<Vector3> getBounds() {
		return bounds;
	}

	@Override
	public boolean contains(Vector3 point) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean intersects(Geometry<Vector3> geometry) {
		// TODO Auto-generated method stub
		return false;
	}
}
