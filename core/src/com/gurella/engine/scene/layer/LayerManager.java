package com.gurella.engine.scene.layer;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.SceneService2;
import com.gurella.engine.scene.manager.ComponentManager.ComponentFamily;
import com.gurella.engine.scene.manager.ComponentTypePredicate;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;

//TODO not needed
public class LayerManager extends SceneService2 implements ComponentActivityListener {
	private static final ComponentFamily family = new ComponentFamily(new ComponentTypePredicate(LayerComponent.class));

	private IntMap<ArrayExt<SceneNode2>> nodesByLayer = new IntMap<ArrayExt<SceneNode2>>();
	private IntIntMap nodeLayers = new IntIntMap();

	public LayerManager(Scene scene) {
		super(scene);
	}

	@Override
	protected void serviceActivated() {
		scene.componentManager.registerComponentFamily(family);
	}

	@Override
	protected void serviceDeactivated() {
		scene.componentManager.unregisterComponentFamily(family);
		nodesByLayer.clear();
		nodeLayers.clear();
	}

	@Override
	public void componentActivated(SceneNodeComponent2 component) {
		if (component instanceof LayerComponent) {
			LayerComponent layerComponent = (LayerComponent) component;
			addNode(layerComponent, layerComponent.layer);
		}
	}

	private void addNode(LayerComponent layerComponent, Layer layer) {
		if (layer != Layer.DEFAULT) {
			int layerId = layer.id;
			SceneNode2 node = layerComponent.getNode();
			getNodes(layerId).add(node);
			nodeLayers.put(node.getInstanceId(), layerId);
		}
	}

	private Array<SceneNode2> getNodes(int layerType) {
		ArrayExt<SceneNode2> nodes = nodesByLayer.get(layerType);
		if (nodes == null) {
			nodes = new ArrayExt<SceneNode2>();
			nodesByLayer.put(layerType, nodes);
		}
		return nodes;
	}

	@Override
	public void componentDeactivated(SceneNodeComponent2 component) {
		if (component instanceof LayerComponent) {
			LayerComponent layerComponent = (LayerComponent) component;
			removeNode(layerComponent, layerComponent.layer);
		}
	}

	private void removeNode(LayerComponent layerComponent, Layer layer) {
		if (layer != Layer.DEFAULT) {
			int layerId = layer.id;
			Array<SceneNode2> nodes = nodesByLayer.get(layerId);
			SceneNode2 node = layerComponent.getNode();
			nodes.removeValue(node, true);
			nodeLayers.remove(node.getInstanceId(), 0);
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

	public Layer getNodeLayer(SceneNode2 node) {
		int nodeId = node.getInstanceId();
		int layerId = nodeLayers.get(nodeId, -1);
		return layerId == -1 ? Layer.DEFAULT : Layer.getLayer(layerId);
	}

	public ImmutableArray<SceneNode2> getNodesByLayer(Layer layer) {
		ArrayExt<SceneNode2> layerNodes = nodesByLayer.get(layer.id);
		return layerNodes == null ? ImmutableArray.<SceneNode2> empty() : layerNodes.immutable();
	}

	public ImmutableArray<SceneNode2> getNodesByLayer(int layerId) {
		ArrayExt<SceneNode2> layerNodes = nodesByLayer.get(layerId);
		return layerNodes == null ? ImmutableArray.<SceneNode2> empty() : layerNodes.immutable();
	}
}
