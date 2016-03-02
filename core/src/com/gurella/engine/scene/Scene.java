package com.gurella.engine.scene;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.event.EventService;
import com.gurella.engine.resource.DependencyMap;
import com.gurella.engine.scene.audio.AudioSystem;
import com.gurella.engine.scene.bullet.BulletPhysicsSystem;
import com.gurella.engine.scene.input.InputSystem;
import com.gurella.engine.scene.layer.LayerManager;
import com.gurella.engine.scene.manager.ComponentManager;
import com.gurella.engine.scene.manager.NodeManager;
import com.gurella.engine.scene.renderable.RenderSystem;
import com.gurella.engine.scene.spatial.SpatialPartitioningSystem;
import com.gurella.engine.scene.spatial.bvh.BvhSpatialPartitioningSystem;
import com.gurella.engine.scene.tag.TagManager;
import com.gurella.engine.subscriptions.scene.SceneActivityListener;
import com.gurella.engine.utils.IdentityOrderedSet;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ImmutableIntMapValues;
import com.gurella.engine.utils.Values;

public final class Scene extends ManagedObject {
	public final IntArray initialSystems = new IntArray();
	public final IntArray initialNodes = new IntArray();

	private final Array<Object> tempListeners = new Array<Object>(64);

	private final IntMap<SceneSystem2> _allSystems = new IntMap<SceneSystem2>();
	public transient final ImmutableIntMapValues<SceneSystem2> allSystems = ImmutableIntMapValues.with(_allSystems);
	private final IdentityOrderedSet<SceneSystem2> activeSystemsInternal = new IdentityOrderedSet<SceneSystem2>();
	public transient final ImmutableArray<SceneSystem2> activeSystems = activeSystemsInternal.orderedItems();

	private final IdentityOrderedSet<SceneNode> _allNodes = new IdentityOrderedSet<SceneNode>();
	public transient final ImmutableArray<SceneNode> allNodes = _allNodes.orderedItems();
	private final IdentityOrderedSet<SceneNode> _activeNodes = new IdentityOrderedSet<SceneNode>();
	public transient final ImmutableArray<SceneNode> activeNodes = _activeNodes.orderedItems();

	private final IdentityOrderedSet<SceneNodeComponent> _allComponents = new IdentityOrderedSet<SceneNodeComponent>();
	public transient final ImmutableArray<SceneNodeComponent> allComponents = _allComponents.orderedItems();
	private final IdentityOrderedSet<SceneNodeComponent> _activeComponents = new IdentityOrderedSet<SceneNodeComponent>();
	public transient final ImmutableArray<SceneNodeComponent> activeComponents = _activeComponents.orderedItems();

	public final ComponentManager componentManager = new ComponentManager();
	public final NodeManager nodeManager = new NodeManager();
	public final TagManager tagManager = new TagManager();
	public final LayerManager layerManager = new LayerManager();

	public final SpatialPartitioningSystem<?> spatialPartitioningSystem = new BvhSpatialPartitioningSystem();
	public final InputSystem inputSystem = new InputSystem();
	public final RenderSystem renderSystem = new RenderSystem();
	public final AudioSystem audioSystem = new AudioSystem();
	public final BulletPhysicsSystem bulletPhysicsSystem = new BulletPhysicsSystem();

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

	public void start(DependencyMap initialResources) {
		if (isActive()) {
			throw new GdxRuntimeException("Scene is already active.");
		}

		/*
		 * addSystemSafely(componentManager); addSystemSafely(nodeManager); addSystemSafely(tagManager);
		 * addSystemSafely(layerManager);
		 * 
		 * addSystemSafely(spatialPartitioningSystem); addSystemSafely(inputSystem); addSystemSafely(renderSystem);
		 * addSystemSafely(audioSystem); addSystemSafely(bulletPhysicsSystem);
		 * 
		 * for (int i = 0; i < initialSystems.size; i++) { int initialSystemId = initialSystems.get(i); SceneSystem
		 * system = initialResources.getResource(initialSystemId); addSystemSafely(system); }
		 * 
		 * for (int i = 0; i < initialNodes.size; i++) { int initialNodeId = initialNodes.get(i); SceneNode node =
		 * initialResources.getResource(initialNodeId); addNodeSafely(node); }
		 */

		activate();

		Array<SceneActivityListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(SceneActivityListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).sceneStared(this);
		}
	}

	public void stop() {
		deactivate();

		/*
		 * Array<SceneActivityListener> globalListeners = Values.cast(tempListeners);
		 * EventService.getSubscribers(SceneActivityListener.class, globalListeners); for (int i = 0; i <
		 * globalListeners.size; i++) { globalListeners.get(i).sceneStopped(this); }
		 * 
		 * for (int i = 0; i < allNodesInternal.size; i++) { SceneNode node = allNodesInternal.get(i); if
		 * (node.getParent() == null) { removeNode(node); } }
		 * 
		 * for (int i = 0; i < allSystemsInternal.size; i++) { SceneSystem system = null;//allSystemsInternal.get(i);
		 * removeSystem(system); }
		 */

		// cleanup();
		// releaseResources();
	}

	@Override
	protected final void childAdded(ManagedObject child) {
		// TODO Auto-generated method stub
		if (child instanceof SceneSystem2) {
			SceneSystem2 system = (SceneSystem2) child;
			int baseSystemType = system.baseSystemType;
			if (_allSystems.containsKey(baseSystemType)) {
				throw new IllegalArgumentException("Scene already contains system: " + system.getClass().getName());
			}
			system.scene = this;
			_allSystems.put(baseSystemType, system);
		}
	}

	public void addSystem(SceneSystem2 system) {
		system.setParent(this);
	}

	void systemActivated(SceneSystem2 system) {
		activeSystemsInternal.add(system);
	}

	public void removeSystem(SceneSystem2 system) {
		if (isDefaultSystem(system)) {
			throw new GdxRuntimeException("Can't remove default system.");
		}
		system.destroy();
	}

	private boolean isDefaultSystem(SceneSystem2 system) {
		// TODO Auto-generated method stub
		return false;
	}

	public <T extends SceneSystem2> T getSystem(Class<T> systemClass) {
		T system = getSystem(SceneSystemType.getBaseSystemType(systemClass));
		// TODO fast check without reflection
		if (system == null || ClassReflection.isAssignableFrom(systemClass, system.getClass())) {
			return system;
		} else {
			return null;
		}
	}

	public <T extends SceneSystem2> T getSystem(int systemType) {
		return Values.cast(_allSystems.get(systemType));
	}

	public void addNode(SceneNode2 node) {

	}

	public void removeNode(SceneNode2 node) {

	}
}
