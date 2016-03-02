package com.gurella.engine.scene;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.event.EventService;
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
	public final IntArray initialNodes = new IntArray();

	private final Array<Object> tempListeners = new Array<Object>(64);

	private final IntMap<SceneSystem2> _systems = new IntMap<SceneSystem2>();
	public transient final ImmutableIntMapValues<SceneSystem2> systems = ImmutableIntMapValues.with(_systems);
	private transient final IdentityOrderedSet<SceneSystem2> _activeSystems = new IdentityOrderedSet<SceneSystem2>();
	public transient final ImmutableArray<SceneSystem2> activeSystems = _activeSystems.orderedItems();

	private final IdentityOrderedSet<SceneNode> _nodes = new IdentityOrderedSet<SceneNode>();
	public transient final ImmutableArray<SceneNode> nodes = _nodes.orderedItems();
	private transient final IdentityOrderedSet<SceneNode> _activeNodes = new IdentityOrderedSet<SceneNode>();
	public transient final ImmutableArray<SceneNode> activeNodes = _activeNodes.orderedItems();

	private transient final IdentityOrderedSet<SceneNodeComponent> _components = new IdentityOrderedSet<SceneNodeComponent>();
	public transient final ImmutableArray<SceneNodeComponent> components = _components.orderedItems();
	private transient final IdentityOrderedSet<SceneNodeComponent> _activeComponents = new IdentityOrderedSet<SceneNodeComponent>();
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

	public void addInitialNode(int nodeId) {
		initialNodes.add(nodeId);
	}

	public void removeInitialNode(int nodeId) {
		initialNodes.removeValue(nodeId);
	}

	public IntArray getInitialNodes() {
		return initialNodes;
	}

	public final void start() {
		if (isActive()) {
			throw new GdxRuntimeException("Scene is already active.");
		}

		activate();

		Array<SceneActivityListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(SceneActivityListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).sceneStared(this);
		}
	}

	public final void stop() {
		Array<SceneActivityListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(SceneActivityListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).sceneStopped(this);
		}
		destroy();
		// TODO releaseResources();
	}

	@Override
	protected final void childAdded(ManagedObject child) {
		if (child instanceof SceneSystem2) {
			SceneSystem2 system = (SceneSystem2) child;
			int baseSystemType = system.baseSystemType;
			if (_systems.containsKey(baseSystemType)) {
				throw new IllegalArgumentException("Scene already contains system: " + system.getClass().getName());
			}
			system.scene = this;
			_systems.put(baseSystemType, system);
		} else {
			SceneNode2 node = (SceneNode2) child;
			node.scene = this;
			// TODO Auto-generated method stub
			_nodes.add(null);
		}
	}

	@Override
	protected void childRemoved(ManagedObject child) {
		if (child instanceof SceneSystem2) {
			SceneSystem2 system = (SceneSystem2) child;
			system.scene = null;
			_systems.remove(system.baseSystemType);
		} else {
			SceneNode2 node = (SceneNode2) child;
			node.scene = null;
			// TODO Auto-generated method stub
			_nodes.remove(null);
		}
	}

	public void addSystem(SceneSystem2 system) {
		system.setParent(this);
	}

	void systemActivated(SceneSystem2 system) {
		_activeSystems.add(system);
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
		return Values.cast(_systems.get(systemType));
	}

	public void addNode(SceneNode2 node) {

	}

	public void removeNode(SceneNode2 node) {

	}

	public String getDiagnostics() {
		StringBuilder builder = new StringBuilder();
		builder.append("Systems [");
		for (SceneSystem2 system : systems) {
			builder.append("/n/t");
			if (!system.isActive()) {
				builder.append("*");
			}
			builder.append(system.getClass().getSimpleName());
		}
		builder.append("]");

		return builder.toString();
	}
}
