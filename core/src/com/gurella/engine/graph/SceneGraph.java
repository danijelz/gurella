package com.gurella.engine.graph;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.application.UpdateEvent;
import com.gurella.engine.application.UpdateListener;
import com.gurella.engine.application.UpdateOrder;
import com.gurella.engine.event.EventBus;
import com.gurella.engine.graph.input.InputSystem;
import com.gurella.engine.graph.layer.LayerManager;
import com.gurella.engine.graph.manager.ComponentManager;
import com.gurella.engine.graph.manager.SceneNodeManager;
import com.gurella.engine.graph.renderable.RenderSystem;
import com.gurella.engine.graph.script.ScriptManager;
import com.gurella.engine.graph.spatial.SpatialPartitioningManager;
import com.gurella.engine.graph.spatial.bvh.BvhSpatialPartitioningManager;
import com.gurella.engine.graph.tag.TagManager;
import com.gurella.engine.scene2.Scene;
import com.gurella.engine.signal.AbstractSignal;
import com.gurella.engine.signal.Listener0;

public class SceneGraph implements UpdateListener {
	private Scene scene;
	private SceneStartListener sceneStartListener = new SceneStartListener();
	private SceneStopListener sceneStopListener = new SceneStopListener();

	public Array<SceneNode> allNodes = new Array<SceneNode>();
	public Array<SceneNode> activeNodes = new Array<SceneNode>();

	public Array<SceneNodeComponent> allComponents = new Array<SceneNodeComponent>();
	public Array<SceneNodeComponent> activeComponents = new Array<SceneNodeComponent>();

	private IntMap<SceneSystem> allSystems = new IntMap<SceneSystem>();
	private Array<SceneSystem> activeSystems = new Array<SceneSystem>();

	private Array<GraphOperation> pendingOperations = new Array<GraphOperation>();

	// TODO add seperate state listeners
	private SceneGraphListenerSignal sceneGraphListenerSignal = new SceneGraphListenerSignal();

	public final ComponentManager componentManager;
	public final SceneNodeManager nodeManager;
	public final TagManager tagManager;
	public final LayerManager layerManager;
	public final ScriptManager scriptManager;
	public final SpatialPartitioningManager<?> spatialPartitioningManager;
	public final InputSystem inputSystem;
	public final RenderSystem renderSystem;

	public SceneGraph(Scene scene) {
		this.scene = scene;
		this.scene.startSignal.addListener(sceneStartListener);
		this.scene.stopSignal.addListener(sceneStopListener);

		componentManager = new ComponentManager();
		addSystemSafely(componentManager);

		nodeManager = new SceneNodeManager();
		addSystemSafely(nodeManager);

		tagManager = new TagManager();
		addSystemSafely(tagManager);

		layerManager = new LayerManager();
		addSystemSafely(layerManager);

		scriptManager = new ScriptManager();
		addSystemSafely(scriptManager);

		spatialPartitioningManager = new BvhSpatialPartitioningManager();
		addSystemSafely(spatialPartitioningManager);
		
		inputSystem = new InputSystem();
		addSystemSafely(inputSystem);
		
		renderSystem = new RenderSystem();
		addSystemSafely(renderSystem);
	}

	public void addListener(SceneGraphListener listener) {
		sceneGraphListenerSignal.addListener(listener);
	}

	public void removeListener(SceneGraphListener listener) {
		sceneGraphListenerSignal.removeListener(listener);
	}

	public void addSystem(SceneSystem system) {
		if (system.graph != null) {
			throw new IllegalArgumentException("System is already attached to graph.");
		}

		pendingOperations.add(GraphOperation.obtain().addSystem(this, system));
	}

	void addSystemSafely(SceneSystem system) {
		int systemType = system.systemType;

		if (allSystems.containsKey(systemType)) {
			throw new IllegalArgumentException("Graph already contains system: " + system.getClass().getName());
		}

		allSystems.put(systemType, system);
		system.scene = scene;
		system.graph = this;
		attachElement(system);

		if (system.isEnabled()) {
			activateSystemSafely(system);
		}
	}

	private static void attachElement(SceneGraphElement element) {
		if (!element.initialized) {
			element.initialized = true;
			element.init();
			element.lifecycleSignal.attached();
		}
	}

	void activateSystem(SceneSystem system) {
		if (system.graph != this) {
			throw new IllegalArgumentException();
		}

		pendingOperations.add(GraphOperation.obtain().activateSystem(system));
	}

	void activateSystemSafely(SceneSystem system) {
		if (!system.active) {
			system.active = true;
			system.lifecycleSignal.activated();
			activeSystems.add(system);
		}
	}

	void deactivateSystem(SceneSystem system) {
		if (system.graph != this) {
			throw new IllegalArgumentException();
		}

		pendingOperations.add(GraphOperation.obtain().deactivateSystem(system));
	}

	void deactivateSystemSafely(SceneSystem system) {
		if (system.active) {
			system.active = false;
			system.lifecycleSignal.deactivated();
			activeSystems.removeValue(system, true);
		}
	}

	public void removeSystem(SceneSystem system) {
		if (system.graph != this) {
			throw new IllegalArgumentException();
		}

		pendingOperations.add(GraphOperation.obtain().detachSystem(system));
	}

	void removeSystemSafely(SceneSystem system) {
		deactivateSystemSafely(system);
		system.lifecycleSignal.detached();
		system.scene = null;
		system.graph = null;
		allSystems.remove(system.systemType);
	}

	public <T extends SceneSystem> T getSystem(Class<T> systemClass) {
		T system = getSystem(SceneSystem.getSystemType(systemClass));
		if (system == null || ClassReflection.isAssignableFrom(systemClass, system.getClass())) {
			return system;
		} else {
			return null;
		}
	}

	public <T extends SceneSystem> T getSystem(int systemType) {
		@SuppressWarnings("unchecked")
		T casted = (T) allSystems.get(systemType);
		return casted;
	}

	void addComponent(SceneNode node, SceneNodeComponent component) {
		if (component.graph != null || component.node != null) {
			throw new IllegalStateException();
		}

		pendingOperations.add(GraphOperation.obtain().attachComponent(this, node, component));
	}

	void addComponentSafely(SceneNode node, SceneNodeComponent component) {
		allComponents.add(component);
		component.scene = scene;
		component.graph = this;
		component.node = node;

		node.components.put(component.getComponentType(), component);
		node.componentBits.set(component.componentType);
		attachElement(component);
		sceneGraphListenerSignal.componentAdded(component);
		node.nodeChangedSignal.componentAdded(component);
		activateComponentSafely(component);
	}

	void activateComponent(SceneNodeComponent component) {
		if (component.graph != this) {
			throw new IllegalArgumentException();
		}

		pendingOperations.add(GraphOperation.obtain().activateComponent(component));
	}

	void activateComponentSafely(SceneNodeComponent component) {
		if (!component.active && component.isHierarchyEnabled() && component.node.active) {
			component.active = true;
			activeComponents.add(component);
			component.node.componentBits.set(component.componentType);
			component.lifecycleSignal.activated();
			sceneGraphListenerSignal.componentActivated(component);
			component.node.componentActivatedSignal.dispatch(component);
		}
	}

	void deactivateComponent(SceneNodeComponent component) {
		if (component.graph != this) {
			throw new IllegalArgumentException();
		}

		pendingOperations.add(GraphOperation.obtain().deactivateComponent(component));
	}

	void deactivateComponentSafely(SceneNodeComponent component) {
		if (component.active) {
			component.active = false;
			component.lifecycleSignal.deactivated();
			component.node.componentBits.clear(component.componentType);
			activeComponents.removeValue(component, true);
			sceneGraphListenerSignal.componentDeactivated(component);
			component.node.componentDeactivatedSignal.dispatch(component);
		}
	}

	void removeComponent(SceneNodeComponent component) {
		if (component.graph != this) {
			throw new IllegalArgumentException();
		}

		pendingOperations.add(GraphOperation.obtain().detachComponent(component));
	}

	void removeComponentSafely(SceneNodeComponent component) {
		deactivateComponentSafely(component);
		sceneGraphListenerSignal.componentRemoved(component);
		SceneNode node = component.node;
		node.nodeChangedSignal.componentRemoved(component);
		component.lifecycleSignal.detached();
		component.scene = null;
		component.graph = null;

		node.components.remove(component.getComponentType());
		node.componentBits.clear(component.componentType);
		allComponents.removeValue(component, true);
	}

	public void addNode(SceneNode node) {
		if (node.graph != null) {
			throw new IllegalArgumentException();
		}

		pendingOperations.add(GraphOperation.obtain().attachNode(this, node));
	}

	void addNodeSafely(SceneNode node) {
		if (node.graph != null) {
			return;
		}

		allNodes.add(node);
		node.scene = scene;
		node.graph = this;
		attachElement(node);

		if (node.parent != null) {
			node.nodeChangedSignal.parentChanged(node.parent);
		}

		if (node.isHierarchyEnabled()) {
			activateNodeSafely(node);
		}

		for (SceneNodeComponent component : node.components.values()) {
			addComponentSafely(node, component);
		}

		for (SceneNode child : node.children) {
			addNodeSafely(child);
		}
	}

	void activateNode(SceneNode node) {
		if (node.graph != this) {
			throw new IllegalArgumentException();
		}

		pendingOperations.add(GraphOperation.obtain().activateNode(node));
	}

	private void activateNodeSafely(SceneNode node) {
		if (!node.active && node.isHierarchyEnabled() && (node.parent == null || node.parent.active)) {
			node.active = true;
			node.lifecycleSignal.activated();
			activeNodes.add(node);
		}
	}

	void activateNodeHierarchySafely(SceneNode node) {
		activateNodeSafely(node);

		for (SceneNodeComponent component : node.components.values()) {
			activateComponentSafely(component);
		}

		for (SceneNode child : node.children) {
			activateNodeHierarchySafely(child);
		}
	}

	void deactivateNode(SceneNode node) {
		if (node.graph != this) {
			throw new IllegalArgumentException();
		}

		pendingOperations.add(GraphOperation.obtain().deactivateNode(node));
	}

	void deactivateNodeSafely(SceneNode node) {
		for (SceneNode child : node.children) {
			deactivateNodeSafely(child);
		}

		for (SceneNodeComponent component : node.components.values()) {
			deactivateComponentSafely(component);
		}

		if (node.active) {
			node.active = false;
			node.lifecycleSignal.deactivated();
			activeNodes.removeValue(node, true);
		}
	}

	public void removeNode(SceneNode node) {
		if (node.graph != this) {
			throw new IllegalArgumentException();
		}

		pendingOperations.add(GraphOperation.obtain().removeNode(node));
	}

	void removeNodeSafely(SceneNode node) {
		node.nodeChangedSignal.parentChanged(null);

		deactivateNodeSafely(node);
		removeNodeFromGraph(node);

		if (node.parent != null) {
			node.parent.children.removeValue(node, true);
			node.parent = null;
		}
	}

	private void removeNodeFromGraph(SceneNode node) {
		for (SceneNode child : node.children) {
			removeNodeFromGraph(child);
		}

		for (SceneNodeComponent component : node.components.values()) {
			removeComponentFromGraph(component);
		}

		node.lifecycleSignal.detached();
		node.scene = null;
		node.graph = null;
		allNodes.removeValue(node, true);
	}

	private void removeComponentFromGraph(SceneNodeComponent component) {
		sceneGraphListenerSignal.componentRemoved(component);
		component.lifecycleSignal.detached();
		component.scene = null;
		component.graph = null;
		allComponents.removeValue(component, true);
	}

	@Override
	public void update() {
		cleanup();
	}

	private void cleanup() {
		pendingOperations.sort();

		for (GraphOperation graphOperation : pendingOperations) {
			graphOperation.execute();
			graphOperation.free();
		}

		pendingOperations.clear();
	}

	@Override
	public int getOrdinal() {
		return UpdateOrder.CLEANUP;
	}

	private class SceneStartListener implements Listener0 {
		@Override
		public void handle() {
			cleanup();
			EventBus.GLOBAL.addListener(UpdateEvent.class, SceneGraph.this);
		}
	}

	private class SceneStopListener implements Listener0 {
		@Override
		public void handle() {
			EventBus.GLOBAL.removeListener(UpdateEvent.class, SceneGraph.this);
			cleanup();
		}
	}

	private static class SceneGraphListenerSignal extends AbstractSignal<SceneGraphListener> {
		public void componentActivated(SceneNodeComponent component) {
			for (SceneGraphListener listener : listeners) {
				listener.componentActivated(component);
			}
		}

		public void componentDeactivated(SceneNodeComponent component) {
			for (SceneGraphListener listener : listeners) {
				listener.componentDeactivated(component);
			}
		}

		public void componentAdded(SceneNodeComponent component) {
			for (SceneGraphListener listener : listeners) {
				listener.componentAdded(component);
			}
		}

		public void componentRemoved(SceneNodeComponent component) {
			for (SceneGraphListener listener : listeners) {
				listener.componentRemoved(component);
			}
		}
	}
}
