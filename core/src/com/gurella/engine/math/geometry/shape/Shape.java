package com.gurella.engine.math.geometry.shape;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public abstract class Shape {
	protected boolean boundsDirty;
	protected final BoundingBox bounds = new BoundingBox().clr();

	public BoundingBox getBounds() {
		if (boundsDirty) {
			updateBounds();
		}
		return bounds;
	}

	protected void updateBounds() {
	}

	public boolean contains(Vector2 point) {
		return contains(point.x, point.y, 0f);
	}

	public boolean contains(float x, float y) {
		return contains(x, y, 0f);
	}

	public boolean contains(Vector3 point) {
		return contains(point.x, point.y, point.z);
	}

	public boolean contains(float x, float y, float z) {
		Vector3 min = bounds.min;
		Vector3 max = bounds.max;
		return min.x <= x && max.x >= x && min.y <= y && max.y >= y && min.z <= z && max.z >= z;
	}

	public boolean intersects(Shape geometry) {
		return getBounds().intersects(geometry.getBounds());
	}
}
