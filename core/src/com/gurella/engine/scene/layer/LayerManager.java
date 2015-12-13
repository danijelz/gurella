package com.gurella.engine.scene.layer;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.scene.SceneListener;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.SceneSystem;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;

//TODO attach listeners
public class LayerManager extends SceneSystem implements SceneListener {
	private IntMap<ArrayExt<SceneNode>> nodesByLayer = new IntMap<ArrayExt<SceneNode>>();
	private IntIntMap nodeLayers = new IntIntMap();

	@Override
	public void componentAdded(SceneNodeComponent component) {
	}

	@Override
	public void componentRemoved(SceneNodeComponent component) {
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		if (component instanceof LayerComponent) {
			LayerComponent layerComponent = (LayerComponent) component;
			addNode(layerComponent, layerComponent.layer);
		}
	}

	private void addNode(LayerComponent layerComponent, Layer layer) {
		if (layer != Layer.DEFAULT) {
			int layerId = layer.id;
			SceneNode node = layerComponent.getNode();
			getNodes(layerId).add(node);
			nodeLayers.put(node.id, layerId);
		}
	}

	private Array<SceneNode> getNodes(int layerType) {
		ArrayExt<SceneNode> nodes = nodesByLayer.get(layerType);
		if (nodes == null) {
			nodes = new ArrayExt<SceneNode>();
			nodesByLayer.put(layerType, nodes);
		}
		return nodes;
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		if (component instanceof LayerComponent) {
			LayerComponent layerComponent = (LayerComponent) component;
			removeNode(layerComponent, layerComponent.layer);
		}
	}

	private void removeNode(LayerComponent layerComponent, Layer layer) {
		if (layer != Layer.DEFAULT) {
			int layerId = layer.id;
			Array<SceneNode> nodes = nodesByLayer.get(layerId);
			SceneNode node = layerComponent.getNode();
			nodes.removeValue(node, true);
			nodeLayers.remove(node.id, 0);
		}
	}

	void layerChanged(LayerComponent layerComponent, Layer oldLayer, Layer newLayer) {
		if (oldLayer != Layer.DEFAULT) {
			removeNode(layerComponent, oldLayer);
		}

		if (newLayer != Layer.DEFAULT) {
			addNode(layerComponent, newLayer);
		}
	}

	public Layer getNodeLayer(SceneNode node) {
		int nodeId = node.id;
		int layerId = nodeLayers.get(nodeId, -1);
		return layerId == -1 ? Layer.DEFAULT : Layer.getLayer(layerId);
	}

	public ImmutableArray<SceneNode> getNodesByLayer(Layer layer) {
		ArrayExt<SceneNode> layerNodes = nodesByLayer.get(layer.id);
		return layerNodes == null ? ImmutableArray.<SceneNode> empty() : layerNodes.immutable();
	}

	public ImmutableArray<SceneNode> getNodesByLayer(int layerId) {
		ArrayExt<SceneNode> layerNodes = nodesByLayer.get(layerId);
		return layerNodes == null ? ImmutableArray.<SceneNode> empty() : layerNodes.immutable();
	}
}
