package com.gurella.engine.graph.layer;

import com.gurella.engine.graph.SceneNodeComponent;

public class LayerComponent extends SceneNodeComponent {
	public Layer layer;

	public LayerComponent(Layer layer) {
		this.layer = layer;
	}
}
