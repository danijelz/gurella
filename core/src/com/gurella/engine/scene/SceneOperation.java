package com.gurella.engine.scene;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.ValueUtils;

class SceneOperation implements Comparable<SceneOperation>, Poolable {
	private enum ElementType {
		SYSTEM, NODE, COMPONENT
	}

	private enum SceneOperationType {
		ATTACH, ACTIVATE, DEACTIVATE, REMOVE
	}

	SceneOperation.ElementType elementType;
	SceneOperation.SceneOperationType sceneOperationType;

	Scene scene;
	SceneNode node;
	SceneSystem system;
	SceneNodeComponent component;

	static SceneOperation obtain() {
		return PoolService.obtain(SceneOperation.class);
	}

	void free() {
		PoolService.free(this);
	}

	SceneOperation addSystem(Scene scene, SceneSystem sceneSystem) {
		elementType = ElementType.SYSTEM;
		sceneOperationType = SceneOperationType.ATTACH;
		this.scene = scene;
		this.system = sceneSystem;
		return this;
	}

	SceneOperation activateSystem(SceneSystem sceneSystem) {
		elementType = ElementType.SYSTEM;
		sceneOperationType = SceneOperationType.ACTIVATE;
		this.system = sceneSystem;
		return this;
	}

	SceneOperation deactivateSystem(SceneSystem sceneSystem) {
		elementType = ElementType.SYSTEM;
		sceneOperationType = SceneOperationType.DEACTIVATE;
		this.system = sceneSystem;
		return this;
	}

	SceneOperation detachSystem(SceneSystem sceneSystem) {
		elementType = ElementType.SYSTEM;
		sceneOperationType = SceneOperationType.REMOVE;
		this.system = sceneSystem;
		return this;
	}

	SceneOperation attachNode(Scene scene, SceneNode sceneNode) {
		elementType = ElementType.NODE;
		sceneOperationType = SceneOperationType.ATTACH;
		this.scene = scene;
		this.node = sceneNode;
		return this;
	}

	SceneOperation activateNode(SceneNode sceneNode) {
		elementType = ElementType.NODE;
		sceneOperationType = SceneOperationType.ACTIVATE;
		this.node = sceneNode;
		return this;
	}

	SceneOperation deactivateNode(SceneNode sceneNode) {
		elementType = ElementType.NODE;
		sceneOperationType = SceneOperationType.DEACTIVATE;
		this.node = sceneNode;
		return this;
	}

	SceneOperation removeNode(SceneNode sceneNode) {
		elementType = ElementType.NODE;
		sceneOperationType = SceneOperationType.REMOVE;
		this.node = sceneNode;
		return this;
	}

	SceneOperation attachComponent(Scene scene, SceneNode sceneNode, SceneNodeComponent sceneComponent) {
		elementType = ElementType.COMPONENT;
		sceneOperationType = SceneOperationType.ATTACH;
		this.scene = scene;
		this.node = sceneNode;
		this.component = sceneComponent;
		return this;
	}

	SceneOperation activateComponent(SceneNodeComponent sceneComponent) {
		elementType = ElementType.COMPONENT;
		sceneOperationType = SceneOperationType.ACTIVATE;
		this.component = sceneComponent;
		return this;
	}

	SceneOperation deactivateComponent(SceneNodeComponent sceneComponent) {
		elementType = ElementType.COMPONENT;
		sceneOperationType = SceneOperationType.DEACTIVATE;
		this.component = sceneComponent;
		return this;
	}

	SceneOperation detachComponent(SceneNodeComponent sceneComponent) {
		elementType = ElementType.COMPONENT;
		sceneOperationType = SceneOperationType.REMOVE;
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
		switch (sceneOperationType) {
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
		switch (sceneOperationType) {
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
		switch (sceneOperationType) {
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
		sceneOperationType = null;

		node = null;
		system = null;
		component = null;
	}

	public int getOrder() {
		return (elementType.ordinal() * 10) + sceneOperationType.ordinal();
	}

	@Override
	public int compareTo(SceneOperation other) {
		return ValueUtils.compare(getOrder(), other.getOrder());
	}
}