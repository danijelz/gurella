package com.gurella.engine.graph.layer;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.graph.GraphListenerSystem;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;

public class LayerManager extends GraphListenerSystem {
	private IntMap<Array<SceneNode>> nodesByLayer = new IntMap<Array<SceneNode>>();
	private IntIntMap nodeLayers = new IntIntMap();

	public Layer getNodeLayer(SceneNode node) {
		int nodeId = node.id;
		int layerId = nodeLayers.get(nodeId, -1);

		if (layerId == -1) {
			LayerComponent layerComponent = node.getComponent(LayerComponent.class);

			if (layerComponent == null) {
				return Layer.DEFAULT;
			} else {
				Layer layer = layerComponent.layer;
				nodeLayers.put(nodeId, layer.id);
				return layer;
			}
		} else {
			return Layer.getLayerByType(layerId);
		}
	}

	public <T extends SceneNode> Array<T> getNodesByLayer(Layer layer) {
		return getNodesByLayer(layer.id);
	}

	public <T extends SceneNode> Array<T> getNodesByLayer(int layerType) {
		@SuppressWarnings("unchecked")
		Array<T> casted = (Array<T>) nodesByLayer.get(layerType);
		return casted;
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		if (component instanceof LayerComponent) {
			LayerComponent layerComponent = (LayerComponent) component;
			int layerType = layerComponent.layer.id;
			SceneNode node = component.getNode();

			getNodes(layerType).add(node);
			nodeLayers.put(node.id, layerType);
		}
	}

	private Array<SceneNode> getNodes(int layerType) {
		Array<SceneNode> nodes = nodesByLayer.get(layerType);

		if (nodes == null) {
			nodes = new Array<SceneNode>();
			nodesByLayer.put(layerType, nodes);
		}

		return nodes;
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		if (component instanceof LayerComponent) {
			LayerComponent layerComponent = (LayerComponent) component;
			int layerType = layerComponent.layer.id;
			Array<SceneNode> nodes = nodesByLayer.get(layerType);
			SceneNode node = component.getNode();

			nodes.removeValue(node, true);
			nodeLayers.remove(node.id, 0);

			if (nodes.size < 1) {
				nodesByLayer.remove(layerType);
			}
		}
	}
}
