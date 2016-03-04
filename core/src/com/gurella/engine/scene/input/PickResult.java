package com.gurella.engine.scene.input;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.scene.SceneNode2;

public class PickResult implements Poolable {
	public SceneNode2 node;
	public final Vector3 intersection = new Vector3();

	@Override
	public void reset() {
		node = null;
		intersection.set(Float.NaN, Float.NaN, Float.NaN);
	}
}
