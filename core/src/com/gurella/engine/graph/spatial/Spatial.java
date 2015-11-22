package com.gurella.engine.graph.spatial;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.graph.layer.Layer;
import com.gurella.engine.graph.renderable.RenderableComponent;

public class Spatial implements Poolable {
	public int nodeId;
	public RenderableComponent renderableComponent;
	public Layer layer;
	public boolean dirty;

	@SuppressWarnings("hiding")
	public void init(RenderableComponent renderableComponent) {
		this.nodeId = renderableComponent.getNode().id;
		this.renderableComponent = renderableComponent;
		this.layer = renderableComponent.layer;
	}

	@Override
	public void reset() {
		nodeId = -1;
		renderableComponent = null;
		layer = null;
		dirty = false;
	}
}
