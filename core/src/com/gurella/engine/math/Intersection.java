package com.gurella.engine.math;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Intersection implements Poolable {
	public final Vector3 location = new Vector3(Float.NaN, Float.NaN, Float.NaN);
	public float distance = Float.MAX_VALUE;

	@Override
	public void reset() {
		location.set(Float.NaN, Float.NaN, Float.NaN);
		distance = Float.MAX_VALUE;
	}
}
