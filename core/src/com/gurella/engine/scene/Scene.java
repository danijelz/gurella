package com.gurella.engine.scene;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.application.Application;
import com.gurella.engine.application.CommonUpdateOrder;
import com.gurella.engine.event.Signal0.Signal0Impl;
import com.gurella.engine.event.Signal1.Signal1Impl;
import com.gurella.engine.resource.ResourceMap;
import com.gurella.engine.resource.SceneElementsResourceContext;
import com.gurella.engine.scene.audio.AudioSystem;
import com.gurella.engine.scene.event.EventManager;
import com.gurella.engine.scene.input.InputSystem;
import com.gurella.engine.scene.layer.LayerManager;
import com.gurella.engine.scene.manager.ComponentManager;
import com.gurella.engine.scene.manager.SceneEventsSignal;
import com.gurella.engine.scene.manager.NodeManager;
import com.gurella.engine.scene.renderable.RenderSystem;
import com.gurella.engine.scene.spatial.SpatialPartitioningSystem;
import com.gurella.engine.scene.spatial.bvh.BvhSpatialPartitioningSystem;
import com.gurella.engine.scene.tag.TagManager;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ImmutableIntMapValues;

public class Scene extends SceneElementsResourceContext {
	private static final String ID_TAG = "id";
	private static final String GROUP_TAG = "group";
	private static final String INITIAL_SYSTEMS_TAG = "initialSystems";
	private static final String INITIAL_NODES_TAG = "initialNodes";

	public static final String defaultGroup = "Default";

	private String id;
	private String group = defaultGroup;
	public final IntArray initialSystems = new IntArray();
	public final IntArray initialNodes = new IntArray();

	public final Signal0Impl startSignal = new Signal0Impl();
	public final Signal0Impl stopSignal = new Signal0Impl();

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

	public final EventManager eventManager = new EventManager();

	public final ComponentManager componentManager = new ComponentManager();
	public final NodeManager nodeManager = new NodeManager();
	public final TagManager tagManager = new TagManager();
	public final LayerManager layerManager = new LayerManager();

	public final SpatialPartitioningSystem<?> spatialPartitioningSystem = new BvhSpatialPartitioningSystem();
	public final InputSystem inputSystem = new InputSystem();
	public final RenderSystem renderSystem = new RenderSystem();
	public final AudioSystem audioSystem = new AudioSystem();
	//TODO physics system

	private final SceneEventsSignal sceneEventsSignal = new SceneEventsSignal(eventManager);

	public Scene(Application application, String id) {
		super(application);
		this.id = id;
	}

	public Application getApplication() {
		return (Application) getParent();
	}

	public String getId() {
		return id;
	}

	public String getGroup() {
		return group;
	}

	public void addInitialSystem(int systemId) {
		initialSystems.add(systemId);
	}

	public void removeInitialSystem(int systemId) {
		initialSystems.removeValue(systemId);
	}

	public IntArray getInitialSystems() {
		return initialSystems;
	}

	public void addInitialNode(int nodeId) {
		initialNodes.add(nodeId);
	}

	public void removeInitialNode(int nodeId) {
		initialNodes.removeValue(nodeId);
	}

	public IntArray getInitialNodes() {
		return initialNodes;
	}

	public void start(ResourceMap initialResources) {
		addSystemSafely(componentManager);
		addSystemSafely(nodeManager);
		addSystemSafely(tagManager);
		addSystemSafely(layerManager);

		addSystemSafely(spatialPartitioningSystem);
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

		startSignal.dispatch();
	}

	public void stop() {
		stopSignal.dispatch();
		releaseResources();

		// TODO remove only root nodes
		for (int i = 0; i < allNodesInternal.size; i++) {
			SceneNode node = allNodesInternal.get(i);
			removeNodeSafely(node);
		}

		for (int i = 0; i < allSystemsInternal.size; i++) {
			SceneSystem system = allSystemsInternal.get(i);
			removeSystemSafely(system);
		}
	}

	public void addSystem(SceneSystem system) {
		pendingOperations.add(GraphOperation.obtain().addSystem(this, system));
	}

	void addSystemSafely(SceneSystem system) {
		if (system.scene != null) {
			throw new IllegalArgumentException("System is already attached to graph.");
		}

		int systemType = system.baseSystemType;
		if (allSystemsInternal.containsKey(systemType)) {
			throw new IllegalArgumentException("Graph already contains system: " + system.getClass().getName());
		}

		allSystemsInternal.put(systemType, system);
		system.scene = this;
		attachElement(system);

		if (system.isEnabled()) {
			activateSystemSafely(system);
		}
	}

	private static void attachElement(SceneElement element) {
		if (!element.initialized) {
			element.initialized = true;
			element.init();
			element.lifecycleSignal.attached();
		}
	}

	void activateSystem(SceneSystem system) {
		if (system.scene != this) {
			throw new IllegalArgumentException("System does not belong to graph.");
		}

		pendingOperations.add(GraphOperation.obtain().activateSystem(system));
	}

	void activateSystemSafely(SceneSystem system) {
		if (!system.active) {
			system.active = true;
			system.lifecycleSignal.activated();
			activeSystemsInternal.add(system);
			sceneEventsSignal.systemActivated(system);
		}
	}

	void deactivateSystem(SceneSystem system) {
		if (system.scene != this) {
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
		if (system.scene != this) {
			throw new IllegalArgumentException("Node does not belong to graph.");
		}

		pendingOperations.add(GraphOperation.obtain().detachSystem(system));
	}

	void removeSystemSafely(SceneSystem system) {
		deactivateSystemSafely(system);
		system.lifecycleSignal.detached();
		system.scene = null;
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
		if (component.scene != null || component.node != null) {
			throw new IllegalStateException();
		}

		pendingOperations.add(GraphOperation.obtain().attachComponent(this, node, component));
	}

	void addComponentSafely(SceneNode node, SceneNodeComponent component) {
		allComponentsInternal.add(component);
		component.scene = this;
		component.node = node;

		node.componentsInternal.put(component.baseComponentType, component);
		node.componentBitsInternal.set(component.componentType);
		attachElement(component);
		sceneGraphListenerSignal.componentAdded(component);
		node.nodeChangedSignal.componentAdded(component);
		activateComponentSafely(component);
	}

	void activateComponent(SceneNodeComponent component) {
		if (component.scene != this) {
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
		if (component.scene != this) {
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
		if (component.scene != this) {
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

		node.componentsInternal.remove(component.baseComponentType);
		node.componentBitsInternal.clear(component.componentType);
		allComponentsInternal.removeValue(component, true);
	}

	public void addNode(SceneNode node) {
		if (node.scene != null) {
			throw new IllegalArgumentException();
		}

		pendingOperations.add(GraphOperation.obtain().attachNode(this, node));
	}

	void addNodeSafely(SceneNode node) {
		if (node.scene != null) {
			return;
		}

		allNodesInternal.add(node);
		node.scene = this;
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
		if (node.scene != this) {
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
		if (node.scene != this) {
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
		if (node.scene != this) {
			throw new IllegalArgumentException();
		}

		pendingOperations.add(GraphOperation.obtain().removeNode(node));
	}

	void removeNodeSafely(SceneNode node) {
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
		allNodesInternal.removeValue(node, true);
	}

	private void removeComponentFromGraph(SceneNodeComponent component) {
		sceneGraphListenerSignal.componentRemoved(component);
		component.lifecycleSignal.detached();
		component.scene = null;
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

	@Override
	public void write(Json json) {
		json.writeValue(ID_TAG, id);
		json.writeValue(GROUP_TAG, group);

		json.writeArrayStart(INITIAL_SYSTEMS_TAG);
		for (int i = 0; i < initialSystems.size; i++) {
			int initialSystemId = initialSystems.get(i);
			json.writeValue(Integer.valueOf(initialSystemId));
		}
		json.writeArrayEnd();

		json.writeArrayStart(INITIAL_NODES_TAG);
		for (int i = 0; i < initialNodes.size; i++) {
			int initialNodeId = initialNodes.get(i);
			json.writeValue(Integer.valueOf(initialNodeId));
		}
		json.writeArrayEnd();

		super.write(json);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		id = jsonData.getString(ID_TAG);
		group = jsonData.getString(GROUP_TAG);

		JsonValue values = jsonData.get(INITIAL_SYSTEMS_TAG);
		for (JsonValue value : values) {
			addInitialSystem(value.asInt());
		}

		values = jsonData.get(INITIAL_NODES_TAG);
		for (JsonValue value : values) {
			addInitialNode(value.asInt());
		}

		super.read(json, jsonData);
	}

	@Override
	public String toString() {
		return id;
	}
}
