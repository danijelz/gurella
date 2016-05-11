package com.gurella.engine.math.geometry;

import com.badlogic.gdx.math.Vector3;

public enum Axis {
	x(1, 0, 0), y(0, 1, 0), z(0, 0, 1);

	private final Vector3 axisVector;

	private Axis(float x, float y, float z) {
		this.axisVector = new Vector3(x, y, z);
	}

	public Vector3 getAxisVector(Vector3 out) {
		return out.set(axisVector);
	}
}
