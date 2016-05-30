package com.gurella.engine.scene.spatial;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.scene.renderable.RenderableComponent;

public class Spatial implements Poolable {
	public int nodeId;
	public RenderableComponent renderableComponent;

	public void init(RenderableComponent renderableComponent) {
		this.nodeId = renderableComponent.getNodeId();
		this.renderableComponent = renderableComponent;
	}

	@Override
	public void reset() {
		nodeId = -1;
		renderableComponent = null;
	}
}
