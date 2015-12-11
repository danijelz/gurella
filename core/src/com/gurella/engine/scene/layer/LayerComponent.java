package com.gurella.engine.scene.layer;

import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNodeComponent;

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
			Scene scene = getScene();
			if (scene != null && isActive()) {
				scene.layerManager.layerChanged(this, oldLayer, newLayer);
			}
		}
	}

	@Override
	protected void resetted() {
		layer = Layer.DEFAULT;
	}
}
