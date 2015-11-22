package com.gurella.engine.application;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

//TODO remove
public class Units {
	public static final float PIXELS_PER_METER = 32;
	public static final float WIDTH_IN_METERS = Gdx.graphics.getWidth() / PIXELS_PER_METER;
	public static final float HEIGHT_IN_METERS = Gdx.graphics.getHeight() / PIXELS_PER_METER;

	public static float metersToPixels(float meters) {
		return meters * PIXELS_PER_METER;
	}

	public static float pixelsToMeters(float pixels) {
		return pixels / PIXELS_PER_METER;
	}

	public static Vector3 metersToPixels(Vector3 point) {
		return point.set(metersToPixels(point.x), metersToPixels(point.y), metersToPixels(point.z));
	}

	public static Vector3 pixelsToMeters(Vector3 point) {
		return point.set(pixelsToMeters(point.x), pixelsToMeters(point.y), pixelsToMeters(point.z));
	}
}
