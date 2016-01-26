package com.gurella.engine.math.geometry;

import com.badlogic.gdx.math.MathUtils;

public class Angle {
	private float degrees;

	private Angle() {
	}

	public static Angle getInstance() {
		return new Angle();
	}

	public static Angle getFromDegrees(float angleInDegrees) {
		Angle angle = new Angle();
		angle.setDegrees(angleInDegrees);
		return angle;
	}

	public static Angle getFromRadians(float angleInRadians) {
		Angle angle = new Angle();
		angle.setDegrees(angleInRadians * MathUtils.radiansToDegrees);
		return angle;
	}

	public float getDegrees() {
		return degrees;
	}

	public void setDegrees(float angleInDegrees) {
		if (angleInDegrees >= 360 || angleInDegrees <= -360) {
			degrees = angleInDegrees % 360;
		} else {
			degrees = angleInDegrees;
		}
	}

	public float getRadians() {
		return degrees * MathUtils.degreesToRadians;
	}

	public void setRadians(float angleInRadians) {
		setDegrees(angleInRadians * MathUtils.radiansToDegrees);
	}
}
