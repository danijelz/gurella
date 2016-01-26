package com.gurella.engine.scene.spatial;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.scene.layer.Layer;
import com.gurella.engine.scene.renderable.RenderableComponent;

public class Spatial implements Poolable {
	public int nodeId;
	public RenderableComponent renderableComponent;
	public Layer layer;
	public boolean dirty;

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
