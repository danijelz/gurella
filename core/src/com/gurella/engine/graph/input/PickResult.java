package com.gurella.engine.graph.input;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.graph.SceneNode;

public class PickResult implements Poolable {
	public SceneNode node;
	public final Vector3 intersection = new Vector3();

	@Override
	public void reset() {
		node = null;
		intersection.set(Float.NaN, Float.NaN, Float.NaN);
	}
}
