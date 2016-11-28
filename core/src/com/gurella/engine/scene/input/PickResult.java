package com.gurella.engine.scene.input;

import com.gurella.engine.math.Intersection;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.renderable.RenderableComponent;

public class PickResult extends Intersection {
	public SceneNode node;
	public RenderableComponent renderable;

	@Override
	public void reset() {
		super.reset();
		node = null;
		renderable = null;
	}
}
