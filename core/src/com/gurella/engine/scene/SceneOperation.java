package com.gurella.engine.scene;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.utils.SynchronizedPools;

class SceneOperation implements Comparable<SceneOperation>, Poolable {
	private enum ElementType {
		SYSTEM, NODE, COMPONENT
	}

	private enum GraphOperationType {
		ATTACH, ACTIVATE, DEACTIVATE, REMOVE
	}

	SceneOperation.ElementType elementType;
	SceneOperation.GraphOperationType graphOperationType;

	Scene scene;
	SceneNode node;
	SceneSystem system;
	SceneNodeComponent component;

	static SceneOperation obtain() {
		return SynchronizedPools.obtain(SceneOperation.class);
	}

	void free() {
		SynchronizedPools.free(this);
	}

	SceneOperation addSystem(Scene scene, SceneSystem sceneSystem) {
		elementType = ElementType.SYSTEM;
		graphOperationType = GraphOperationType.ATTACH;
		this.scene = scene;
		this.system = sceneSystem;
		return this;
	}

	SceneOperation activateSystem(SceneSystem sceneSystem) {
		elementType = ElementType.SYSTEM;
		graphOperationType = GraphOperationType.ACTIVATE;
		this.system = sceneSystem;
		return this;
	}

	SceneOperation deactivateSystem(SceneSystem sceneSystem) {
		elementType = ElementType.SYSTEM;
		graphOperationType = GraphOperationType.DEACTIVATE;
		this.system = sceneSystem;
		return this;
	}

	SceneOperation detachSystem(SceneSystem sceneSystem) {
		elementType = ElementType.SYSTEM;
		graphOperationType = GraphOperationType.REMOVE;
		this.system = sceneSystem;
		return this;
	}

	SceneOperation attachNode(Scene scene, SceneNode sceneNode) {
		elementType = ElementType.NODE;
		graphOperationType = GraphOperationType.ATTACH;
		this.scene = scene;
		this.node = sceneNode;
		return this;
	}

	SceneOperation activateNode(SceneNode sceneNode) {
		elementType = ElementType.NODE;
		graphOperationType = GraphOperationType.ACTIVATE;
		this.node = sceneNode;
		return this;
	}

	SceneOperation deactivateNode(SceneNode sceneNode) {
		elementType = ElementType.NODE;
		graphOperationType = GraphOperationType.DEACTIVATE;
		this.node = sceneNode;
		return this;
	}

	SceneOperation removeNode(SceneNode sceneNode) {
		elementType = ElementType.NODE;
		graphOperationType = GraphOperationType.REMOVE;
		this.node = sceneNode;
		return this;
	}

	SceneOperation attachComponent(Scene scene, SceneNode sceneNode, SceneNodeComponent sceneComponent) {
		elementType = ElementType.COMPONENT;
		graphOperationType = GraphOperationType.ATTACH;
		this.scene = scene;
		this.node = sceneNode;
		this.component = sceneComponent;
		return this;
	}

	SceneOperation activateComponent(SceneNodeComponent sceneComponent) {
		elementType = ElementType.COMPONENT;
		graphOperationType = GraphOperationType.ACTIVATE;
		this.component = sceneComponent;
		return this;
	}

	SceneOperation deactivateComponent(SceneNodeComponent sceneComponent) {
		elementType = ElementType.COMPONENT;
		graphOperationType = GraphOperationType.DEACTIVATE;
		this.component = sceneComponent;
		return this;
	}

	SceneOperation detachComponent(SceneNodeComponent sceneComponent) {
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
			scene.addSystemSafely(system);
			break;
		case ACTIVATE:
			scene.activateSystemSafely(system);
			break;
		case DEACTIVATE:
			scene.deactivateSystemSafely(system);
			break;
		case REMOVE:
			scene.removeSystemSafely(system);
			break;
		}
	}

	private void executeNodeOperation() {
		switch (graphOperationType) {
		case ATTACH:
			scene.addNodeSafely(node);
			break;
		case ACTIVATE:
			scene.activateNodeHierarchySafely(node);
			break;
		case DEACTIVATE:
			scene.deactivateNodeSafely(node);
			break;
		case REMOVE:
			scene.removeNodeSafely(node);
			break;
		}
	}

	private void executeComponentOperation() {
		switch (graphOperationType) {
		case ATTACH:
			scene.addComponentSafely(node, component);
			break;
		case ACTIVATE:
			scene.activateComponentSafely(component);
			break;
		case DEACTIVATE:
			scene.deactivateComponentSafely(component);
			break;
		case REMOVE:
			scene.removeComponentSafely(component);
			break;
		}
	}

	@Override
	public void reset() {
		scene = null;
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
	public int compareTo(SceneOperation other) {
		return Integer.compare(getOrder(), other.getOrder());
	}
}