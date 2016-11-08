package com.gurella.engine.math.geometry.shape;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

public class Polygon extends Shape {
	private final Array<Vector3> points = new Array<Vector3>();

	public void add(Vector3 point) {
		points.add(point);
		boundsDirty = true;
	}

	public boolean remove(Vector3 point) {
		if (points.removeValue(point, true)) {
			boundsDirty = true;
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void updateBounds(BoundingBox bounds) {
		for (int i = 0, n = points.size; i < n; i++) {
			bounds.ext(points.get(i));
		}
	}
}
