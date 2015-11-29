package com.gurella.engine.graph;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.application.UpdateEvent;
import com.gurella.engine.application.UpdateListener;
import com.gurella.engine.application.CommonUpdateOrder;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graph.input.InputSystem;
import com.gurella.engine.graph.layer.LayerManager;
import com.gurella.engine.graph.manager.ComponentsManager;
import com.gurella.engine.graph.manager.NodesManager;
import com.gurella.engine.graph.renderable.RenderSystem;
import com.gurella.engine.graph.script.ScriptManager;
import com.gurella.engine.graph.spatial.SpatialPartitioningManager;
import com.gurella.engine.graph.spatial.bvh.BvhSpatialPartitioningManager;
import com.gurella.engine.graph.tag.TagManager;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.signal.AbstractSignal;
import com.gurella.engine.signal.Listener0;
import com.gurella.engine.utils.ImmutableArray;

public class SceneGraph implements UpdateListener {
	private Scene scene;
	private SceneStartListener sceneStartListener = new SceneStartListener();
	private SceneStopListener sceneStopListener = new SceneStopListener();

	private Array<SceneNode> allNodesInternal = new Array<SceneNode>();
	public ImmutableArray<SceneNode> allNodes = ImmutableArray.with(allNodesInternal);
	private Array<SceneNode> activeNodesInternal = new Array<SceneNode>();
	public ImmutableArray<SceneNode> activeNodes = ImmutableArray.with(activeNodesInternal);

	private Array<SceneNodeComponent> allComponentsInternal = new Array<SceneNodeComponent>();
	public ImmutableArray<SceneNodeComponent> allComponents = ImmutableArray.with(allComponentsInternal);
	private Array<SceneNodeComponent> activeComponentsInternal = new Array<SceneNodeComponent>();
	public ImmutableArray<SceneNodeComponent> activeComponents = ImmutableArray.with(activeComponentsInternal);

	private IntMap<SceneSystem> allSystems = new IntMap<SceneSystem>();
	private Array<SceneSystem> activeSystems = new Array<SceneSystem>();

	private Array<GraphOperation> pendingOperations = new Array<GraphOperation>();

	// TODO add seperate state listeners
	private SceneGraphListenerSignal sceneGraphListenerSignal = new SceneGraphListenerSignal();

	public final ComponentsManager componentsManager;
	public final NodesManager nodesManager;
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

		componentsManager = new ComponentsManager();
		addSystemSafely(componentsManager);

		nodesManager = new NodesManager();
		addSystemSafely(nodesManager);

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
			throw new IllegalArgumentException();
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
