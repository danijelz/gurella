package com.gurella.engine.graphics.vector.svg.property.value;

public class StdDeviation {
	public static final StdDeviation zero = new StdDeviation(0, 0);

	private float x;
	private float y;

	public StdDeviation(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public StdDeviation(float xy) {
		this.x = xy;
		this.y = Float.NaN;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return Float.NaN == y ? x : y;
	}
}
