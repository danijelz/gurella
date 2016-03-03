package com.gurella.engine.scene.layer;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNodeComponent2;

public class LayerComponent extends SceneNodeComponent2 implements Poolable {
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
	public void reset() {
		layer = Layer.DEFAULT;
	}
}
