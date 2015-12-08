package com.gurella.engine.graph;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.application.CommonUpdateOrder;
import com.gurella.engine.application.UpdateEvent;
import com.gurella.engine.application.UpdateListener;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graph.event.EventSystem;
import com.gurella.engine.graph.input.InputSystem;
import com.gurella.engine.graph.layer.LayerManager;
import com.gurella.engine.graph.manager.ComponentManager;
import com.gurella.engine.graph.manager.NodeManager;
import com.gurella.engine.graph.renderable.RenderSystem;
import com.gurella.engine.graph.spatial.SpatialPartitioningManager;
import com.gurella.engine.graph.spatial.bvh.BvhSpatialPartitioningManager;
import com.gurella.engine.graph.tag.TagManager;
import com.gurella.engine.resource.ResourceMap;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.signal.AbstractSignal;
import com.gurella.engine.signal.Listener0;
import com.gurella.engine.signal.Signal1.Signal1Impl;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ImmutableIntMapValues;

public class SceneGraph implements UpdateListener {
	private Scene scene;
	private final SceneStartListener sceneStartListener = new SceneStartListener();
	private final SceneStopListener sceneStopListener = new SceneStopListener();

	private final Array<SceneNode> allNodesInternal = new Array<SceneNode>();
	public final ImmutableArray<SceneNode> allNodes = ImmutableArray.with(allNodesInternal);
	private final Array<SceneNode> activeNodesInternal = new Array<SceneNode>();
	public final ImmutableArray<SceneNode> activeNodes = ImmutableArray.with(activeNodesInternal);

	private final Array<SceneNodeComponent> allComponentsInternal = new Array<SceneNodeComponent>();
	public final ImmutableArray<SceneNodeComponent> allComponents = ImmutableArray.with(allComponentsInternal);
	private final Array<SceneNodeComponent> activeComponentsInternal = new Array<SceneNodeComponent>();
	public final ImmutableArray<SceneNodeComponent> activeComponents = ImmutableArray.with(activeComponentsInternal);

	private final IntMap<SceneSystem> allSystemsInternal = new IntMap<SceneSystem>();
	public final ImmutableIntMapValues<SceneSystem> allSystems = ImmutableIntMapValues.with(allSystemsInternal);
	private final Array<SceneSystem> activeSystemsInternal = new Array<SceneSystem>();
	public final ImmutableArray<SceneSystem> activeSystems = ImmutableArray.with(activeSystemsInternal);

	private final Array<GraphOperation> pendingOperations = new Array<GraphOperation>();

	//TODO unused signals
	public final Signal1Impl<SceneNode> nodeAddedSignal = new Signal1Impl<SceneNode>();
	public final Signal1Impl<SceneNode> nodeRemovedSignal = new Signal1Impl<SceneNode>();
	public final Signal1Impl<SceneNode> nodeActivatedSignal = new Signal1Impl<SceneNode>();
	public final Signal1Impl<SceneNode> nodeDeactivatedSignal = new Signal1Impl<SceneNode>();

	public final Signal1Impl<SceneNode> systemAddedSignal = new Signal1Impl<SceneNode>();
	public final Signal1Impl<SceneNode> systemRemovedSignal = new Signal1Impl<SceneNode>();
	public final Signal1Impl<SceneNode> systemActivatedSignal = new Signal1Impl<SceneNode>();
	public final Signal1Impl<SceneNode> systemDeactivatedSignal = new Signal1Impl<SceneNode>();

	public final Signal1Impl<SceneNodeComponent> componentAddedSignal = new Signal1Impl<SceneNodeComponent>();
	public final Signal1Impl<SceneNodeComponent> componentRemovedSignal = new Signal1Impl<SceneNodeComponent>();
	public final Signal1Impl<SceneNodeComponent> componentActivatedSignal = new Signal1Impl<SceneNodeComponent>();
	public final Signal1Impl<SceneNodeComponent> componentDeactivatedSignal = new Signal1Impl<SceneNodeComponent>();

	public final SceneGraphListenerSignal sceneGraphListenerSignal = new SceneGraphListenerSignal();

	public final ComponentManager componentManager;
	public final NodeManager nodeManager;
	public final TagManager tagManager;
	public final LayerManager layerManager;
	public final EventSystem eventSystem;
	public final SpatialPartitioningManager<?> spatialPartitioningManager;
	public final InputSystem inputSystem;
	public final RenderSystem renderSystem;

	public SceneGraph(Scene scene) {
		this.scene = scene;
		this.scene.startSignal.addListener(sceneStartListener);
		this.scene.stopSignal.addListener(sceneStopListener);

		componentManager = new ComponentManager();
		addSystemSafely(componentManager);

		nodeManager = new NodeManager();
		addSystemSafely(nodeManager);

		tagManager = new TagManager();
		addSystemSafely(tagManager);

		layerManager = new LayerManager();
		addSystemSafely(layerManager);

		eventSystem = new EventSystem();
		addSystemSafely(eventSystem);

		spatialPartitioningManager = new BvhSpatialPartitioningManager();
		addSystemSafely(spatialPartitioningManager);

		inputSystem = new InputSystem();
		addSystemSafely(inputSystem);

		renderSystem = new RenderSystem();
		addSystemSafely(renderSystem);
	}

	// TODO unused
	void init(IntArray initialSystems, IntArray initialNodes, ResourceMap initialResources) {
		addSystemSafely(componentManager);
		addSystemSafely(nodeManager);
		addSystemSafely(tagManager);
		addSystemSafely(layerManager);
		addSystemSafely(eventSystem);
		addSystemSafely(spatialPartitioningManager);
		addSystemSafely(inputSystem);
		addSystemSafely(renderSystem);

		for (int i = 0; i < initialSystems.size; i++) {
			int initialSystemId = initialSystems.get(i);
			SceneSystem system = initialResources.getResource(initialSystemId);
			addSystemSafely(system);
		}

		for (int i = 0; i < initialNodes.size; i++) {
			int initialNodeId = initialNodes.get(i);
			SceneNode node = initialResources.getResource(initialNodeId);
			addNodeSafely(node);
		}
	}

	public void addListener(SceneGraphListener listener) {
		sceneGraphListenerSignal.addListener(listener);
	}

	public void removeListener(SceneGraphListener listener) {
		sceneGraphListenerSignal.removeListener(listener);
	}

	public void addSystem(SceneSystem system) {
		pendingOperations.add(GraphOperation.obtain().addSystem(this, system));
	}

	void addSystemSafely(SceneSystem system) {
		if (system.graph != null) {
			throw new IllegalArgumentException("System is already attached to graph.");
		}

		int systemType = system.baseSystemType;
		if (allSystemsInternal.containsKey(systemType)) {
			throw new IllegalArgumentException("Graph already contains system: " + system.getClass().getName());
		}

		allSystemsInternal.put(systemType, system);
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
			throw new IllegalArgumentException("System does not belong to graph.");
		}

		pendingOperations.add(GraphOperation.obtain().activateSystem(system));
	}

	void activateSystemSafely(SceneSystem system) {
		if (!system.active) {
			system.active = true;
			system.lifecycleSignal.activated();
			activeSystemsInternal.add(system);
		}
	}

	void deactivateSystem(SceneSystem system) {
		if (system.graph != this) {
			throw new IllegalArgumentException("System does not belong to graph.");
		}

		pendingOperations.add(GraphOperation.obtain().deactivateSystem(system));
	}

	void deactivateSystemSafely(SceneSystem system) {
		if (system.active) {
			system.active = false;
			system.lifecycleSignal.deactivated();
			activeSystemsInternal.removeValue(system, true);
		}
	}

	public void removeSystem(SceneSystem system) {
		if (system.graph != this) {
			throw new IllegalArgumentException("Node does not belong to graph.");
		}

		pendingOperations.add(GraphOperation.obtain().detachSystem(system));
	}

	void removeSystemSafely(SceneSystem system) {
		deactivateSystemSafely(system);
		system.lifecycleSignal.detached();
		system.scene = null;
		system.graph = null;
		allSystemsInternal.remove(system.baseSystemType);
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
		T casted = (T) allSystemsInternal.get(systemType);
		return casted;
	}

	void addComponent(SceneNode node, SceneNodeComponent component) {
		if (component.graph != null || component.node != null) {
			throw new IllegalStateException();
		}

		pendingOperations.add(GraphOperation.obtain().attachComponent(this, node, component));
	}

	void addComponentSafely(SceneNode node, SceneNodeComponent component) {
		allComponentsInternal.add(component);
		component.scene = scene;
		component.graph = this;
		component.node = node;

		node.componentsInternal.put(component.baseComponentType, component);
		node.componentBitsInternal.set(component.componentType);
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
		SceneNode node = component.node;
		if (!component.active && component.isHierarchyEnabled() && node.active) {
			component.active = true;
			activeComponentsInternal.add(component);
			node.activeComponentBitsInternal.set(component.componentType);
			component.lifecycleSignal.activated();
			sceneGraphListenerSignal.componentActivated(component);
			node.componentActivatedSignal.dispatch(component);
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
			SceneNode node = component.node;
			component.active = false;
			component.lifecycleSignal.deactivated();
			node.activeComponentBitsInternal.clear(component.componentType);
			activeComponentsInternal.removeValue(component, true);
			sceneGraphListenerSignal.componentDeactivated(component);
			node.componentDeactivatedSignal.dispatch(component);
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

		node.componentsInternal.remove(component.baseComponentType);
		node.componentBitsInternal.clear(component.componentType);
		allComponentsInternal.removeValue(component, true);
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

		allNodesInternal.add(node);
		node.scene = scene;
		node.graph = this;
		attachElement(node);

		if (node.parent != null) {
			node.nodeChangedSignal.parentChanged(node.parent);
		}

		if (node.isHierarchyEnabled()) {
			activateNodeSafely(node);
		}

		for (SceneNodeComponent component : node.componentsInternal.values()) {
			addComponentSafely(node, component);
		}

		Array<SceneNode> children = node.childrenInternal;
		for (int i = 0; i < children.size; i++) {
			addNodeSafely(children.get(i));
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
			activeNodesInternal.add(node);
		}
	}

	void activateNodeHierarchySafely(SceneNode node) {
		activateNodeSafely(node);

		for (SceneNodeComponent component : node.componentsInternal.values()) {
			activateComponentSafely(component);
		}

		Array<SceneNode> children = node.childrenInternal;
		for (int i = 0; i < children.size; i++) {
			activateNodeHierarchySafely(children.get(i));
		}
	}

	void deactivateNode(SceneNode node) {
		if (node.graph != this) {
			throw new IllegalArgumentException("Node does not belong to graph.");
		}

		pendingOperations.add(GraphOperation.obtain().deactivateNode(node));
	}

	void deactivateNodeSafely(SceneNode node) {
		Array<SceneNode> children = node.childrenInternal;
		for (int i = 0; i < children.size; i++) {
			deactivateNodeSafely(children.get(i));
		}

		for (SceneNodeComponent component : node.componentsInternal.values()) {
			deactivateComponentSafely(component);
		}

		if (node.active) {
			node.active = false;
			node.lifecycleSignal.deactivated();
			activeNodesInternal.removeValue(node, true);
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
			node.parent.childrenInternal.removeValue(node, true);
			node.parent = null;
			node.nodeChangedSignal.parentChanged(null);
		}
	}

	private void removeNodeFromGraph(SceneNode node) {
		Array<SceneNode> children = node.childrenInternal;
		for (int i = 0; i < children.size; i++) {
			removeNodeFromGraph(children.get(i));
		}

		for (SceneNodeComponent component : node.componentsInternal.values()) {
			removeComponentFromGraph(component);
		}

		node.lifecycleSignal.detached();
		node.scene = null;
		node.graph = null;
		allNodesInternal.removeValue(node, true);
	}

	private void removeComponentFromGraph(SceneNodeComponent component) {
		sceneGraphListenerSignal.componentRemoved(component);
		component.lifecycleSignal.detached();
		component.scene = null;
		component.graph = null;
		allComponentsInternal.removeValue(component, true);
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
		return CommonUpdateOrder.CLEANUP;
	}

	private class SceneStartListener implements Listener0 {
		@Override
		public void handle() {
			cleanup();
			EventService.addListener(UpdateEvent.class, SceneGraph.this);
		}
	}

	private class SceneStopListener implements Listener0 {
		@Override
		public void handle() {
			EventService.removeListener(UpdateEvent.class, SceneGraph.this);
			cleanup();
		}
	}

	public class SceneGraphListenerSignal extends AbstractSignal<SceneGraphListener> {
		private void componentActivated(SceneNodeComponent component) {
			for (SceneGraphListener listener : listeners) {
				listener.componentActivated(component);
			}
			componentActivatedSignal.dispatch(component);
		}

		private void componentDeactivated(SceneNodeComponent component) {
			for (SceneGraphListener listener : listeners) {
				listener.componentDeactivated(component);
			}
			componentDeactivatedSignal.dispatch(component);
		}

		private void componentAdded(SceneNodeComponent component) {
			for (SceneGraphListener listener : listeners) {
				listener.componentAdded(component);
			}
			componentAddedSignal.dispatch(component);
		}

		private void componentRemoved(SceneNodeComponent component) {
			for (SceneGraphListener listener : listeners) {
				listener.componentRemoved(component);
			}
			componentRemovedSignal.dispatch(component);
		}
	}
}
