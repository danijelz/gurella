package com.gurella.engine.graph.layer;

import com.gurella.engine.graph.SceneGraph;
import com.gurella.engine.graph.SceneNodeComponent;

public class LayerComponent extends SceneNodeComponent {
	Layer layer = Layer.DEFAULT;

	public Layer getLayer() {
		return layer;
	}

	public void setLayer(Layer layer) {
		Layer oldLayer = this.layer;
		Layer newLayer = layer == null ? Layer.DEFAULT : layer;

		if (oldLayer != newLayer) {
			this.layer = newLayer;
			SceneGraph graph = getGraph();
			if (graph != null && isActive()) {
				graph.layerManager.layerChanged(this, oldLayer, newLayer);
			}
		}
	}

	@Override
	protected void resetted() {
		layer = Layer.DEFAULT;
	}
}
