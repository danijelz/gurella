package com.gurella.engine.scene.input;

import com.gurella.engine.math.Intersection;
import com.gurella.engine.scene.SceneNode2;

public class PickResult extends Intersection {
	public SceneNode2 node;

	@Override
	public void reset() {
		super.reset();
		node = null;
	}
}
