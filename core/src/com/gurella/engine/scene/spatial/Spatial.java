package com.gurella.engine.scene.spatial;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.scene.renderable.RenderableComponent;

public class Spatial implements Poolable {
	public int nodeId;
	public RenderableComponent renderable;

	public void init(RenderableComponent renderableComponent) {
		this.nodeId = renderableComponent.getNodeId();
		this.renderable = renderableComponent;
	}

	@Override
	public void reset() {
		nodeId = -1;
		renderable = null;
	}
}
