package com.gurella.engine.graph;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pools.SynchronizedPools;

class GraphOperation implements Comparable<GraphOperation>, Poolable {
	private enum ElementType {
		SYSTEM, NODE, COMPONENT
	}

	private enum GraphOperationType {
		ATTACH, ACTIVATE, DEACTIVATE, REMOVE
	}

	GraphOperation.ElementType elementType;
	GraphOperation.GraphOperationType graphOperationType;

	SceneGraph graph;
	SceneNode node;
	SceneSystem system;
	SceneNodeComponent component;

	static GraphOperation obtain() {
		return SynchronizedPools.obtain(GraphOperation.class);
	}

	void free() {
		SynchronizedPools.free(this);
	}

	GraphOperation addSystem(SceneGraph sceneGraph, SceneSystem sceneSystem) {
		elementType = ElementType.SYSTEM;
		graphOperationType = GraphOperationType.ATTACH;
		this.graph = sceneGraph;
		this.system = sceneSystem;
		return this;
	}

	GraphOperation activateSystem(SceneSystem sceneSystem) {
		elementType = ElementType.SYSTEM;
		graphOperationType = GraphOperationType.ACTIVATE;
		this.system = sceneSystem;
		return this;
	}

	GraphOperation deactivateSystem(SceneSystem sceneSystem) {
		elementType = ElementType.SYSTEM;
		graphOperationType = GraphOperationType.DEACTIVATE;
		this.system = sceneSystem;
		return this;
	}

	GraphOperation detachSystem(SceneSystem sceneSystem) {
		elementType = ElementType.SYSTEM;
		graphOperationType = GraphOperationType.REMOVE;
		this.system = sceneSystem;
		return this;
	}

	GraphOperation attachNode(SceneGraph sceneGraph, SceneNode sceneNode) {
		elementType = ElementType.NODE;
		graphOperationType = GraphOperationType.ATTACH;
		this.graph = sceneGraph;
		this.node = sceneNode;
		return this;
	}

	GraphOperation activateNode(SceneNode sceneNode) {
		elementType = ElementType.NODE;
		graphOperationType = GraphOperationType.ACTIVATE;
		this.node = sceneNode;
		return this;
	}

	GraphOperation deactivateNode(SceneNode sceneNode) {
		elementType = ElementType.NODE;
		graphOperationType = GraphOperationType.DEACTIVATE;
		this.node = sceneNode;
		return this;
	}

	GraphOperation removeNode(SceneNode sceneNode) {
		elementType = ElementType.NODE;
		graphOperationType = GraphOperationType.REMOVE;
		this.node = sceneNode;
		return this;
	}

	GraphOperation attachComponent(SceneGraph sceneGraph, SceneNode sceneNode, SceneNodeComponent sceneComponent) {
		elementType = ElementType.COMPONENT;
		graphOperationType = GraphOperationType.ATTACH;
		this.graph = sceneGraph;
		this.node = sceneNode;
		this.component = sceneComponent;
		return this;
	}

	GraphOperation activateComponent(SceneNodeComponent sceneComponent) {
		elementType = ElementType.COMPONENT;
		graphOperationType = GraphOperationType.ACTIVATE;
		this.component = sceneComponent;
		return this;
	}

	GraphOperation deactivateComponent(SceneNodeComponent sceneComponent) {
		elementType = ElementType.COMPONENT;
		graphOperationType = GraphOperationType.DEACTIVATE;
		this.component = sceneComponent;
		return this;
	}

	GraphOperation detachComponent(SceneNodeComponent sceneComponent) {
		elementType = ElementType.COMPONENT;
		graphOperationType = GraphOperationType.REMOVE;
		this.component = sceneComponent;
		return this;
	}

	void execute() {
		switch (elementType) {
		case SYSTEM:
			executeSystemOperation();
			break;
		case COMPONENT:
			executeComponentOperation();
			break;
		case NODE:
			executeNodeOperation();
			break;
		}
	}

	private void executeSystemOperation() {
		switch (graphOperationType) {
		case ATTACH:
			graph.addSystemSafely(system);
			break;
		case ACTIVATE:
			system.graph.activateSystemSafely(system);
			break;
		case DEACTIVATE:
			system.graph.deactivateSystemSafely(system);
			break;
		case REMOVE:
			system.graph.removeSystemSafely(system);
			break;
		}
	}

	private void executeNodeOperation() {
		switch (graphOperationType) {
		case ATTACH:
			graph.addNodeSafely(node);
			break;
		case ACTIVATE:
			node.graph.activateNodeHierarchySafely(node);
			break;
		case DEACTIVATE:
			node.graph.deactivateNodeSafely(node);
			break;
		case REMOVE:
			node.graph.removeNodeSafely(node);
			break;
		}
	}

	private void executeComponentOperation() {
		switch (graphOperationType) {
		case ATTACH:
			graph.addComponentSafely(node, component);
			break;
		case ACTIVATE:
			component.graph.activateComponentSafely(component);
			break;
		case DEACTIVATE:
			component.graph.deactivateComponentSafely(component);
			break;
		case REMOVE:
			component.graph.removeComponentSafely(component);
			break;
		}
	}

	@Override
	public void reset() {
		graph = null;
		elementType = null;
		graphOperationType = null;

		node = null;
		system = null;
		component = null;
	}

	public int getOrder() {
		return (elementType.ordinal() * 10) + graphOperationType.ordinal();
	}

	@Override
	public int compareTo(GraphOperation other) {
		return Integer.compare(getOrder(), other.getOrder());
	}
}