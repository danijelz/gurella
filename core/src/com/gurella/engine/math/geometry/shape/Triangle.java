package com.gurella.engine.math.geometry.shape;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Triangle extends Shape {
	final Vector3 x = new Vector3();
	final Vector3 y = new Vector3();
	final Vector3 z = new Vector3();

	public Vector3 getX() {
		return x;
	}

	public void setX(Vector3 x) {
		this.x.set(x);
		boundsDirty = true;
	}

	public Vector3 getY() {
		return y;
	}

	public void setY(Vector3 y) {
		this.y.set(y);
		boundsDirty = true;
	}

	public Vector3 getZ() {
		return z;
	}

	public void setZ(Vector3 z) {
		this.z.set(z);
		boundsDirty = true;
	}

	@Override
	protected void updateBounds(BoundingBox bounds) {
		bounds.ext(x);
		bounds.ext(y);
		bounds.ext(z);
	}
}
