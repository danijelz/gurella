package com.gurella.engine.scene;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.audio.AudioSystem;
import com.gurella.engine.scene.input.InputSystem;
import com.gurella.engine.scene.layer.LayerManager;
import com.gurella.engine.scene.manager.ComponentManager;
import com.gurella.engine.scene.manager.NodeManager;
import com.gurella.engine.scene.renderable.RenderSystem;
import com.gurella.engine.scene.spatial.SpatialPartitioningSystem;
import com.gurella.engine.scene.spatial.bvh.BvhSpatialPartitioningSystem;
import com.gurella.engine.scene.tag.TagManager;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.OrderedIdentitySet;
import com.gurella.engine.utils.OrderedValuesIntMap;
import com.gurella.engine.utils.Values;

//TODO EntityTransmuter
public final class Scene extends ManagedObject {
	transient final SceneEventsDispatcher eventsDispatcher = new SceneEventsDispatcher(this);

	transient final OrderedValuesIntMap<SceneSystem2> _systems = new OrderedValuesIntMap<SceneSystem2>();
	@PropertyDescriptor(property = SceneSystemsProperty.class)
	public final ImmutableArray<SceneSystem2> systems = _systems.orderedValues();
	transient final OrderedIdentitySet<SceneSystem2> _activeSystems = new OrderedIdentitySet<SceneSystem2>();
	public transient final ImmutableArray<SceneSystem2> activeSystems = _activeSystems.orderedItems();

	transient final OrderedIdentitySet<SceneNode2> _nodes = new OrderedIdentitySet<SceneNode2>();
	@PropertyDescriptor(property = SceneNodesProperty.class)
	public final ImmutableArray<SceneNode2> nodes = _nodes.orderedItems();
	transient final OrderedIdentitySet<SceneNode2> _activeNodes = new OrderedIdentitySet<SceneNode2>();
	public transient final ImmutableArray<SceneNode2> activeNodes = _activeNodes.orderedItems();

	transient final OrderedIdentitySet<SceneNodeComponent2> _components = new OrderedIdentitySet<SceneNodeComponent2>();
	public transient final ImmutableArray<SceneNodeComponent2> components = _components.orderedItems();
	transient final OrderedIdentitySet<SceneNodeComponent2> _activeComponents = new OrderedIdentitySet<SceneNodeComponent2>();
	public transient final ImmutableArray<SceneNodeComponent2> activeComponents = _activeComponents.orderedItems();

	public final transient ComponentManager componentManager = new ComponentManager();
	public final transient NodeManager nodeManager = new NodeManager();
	public final transient TagManager tagManager = new TagManager();
	public final transient LayerManager layerManager = new LayerManager();

	public final transient SpatialPartitioningSystem<?> spatialPartitioningSystem = new BvhSpatialPartitioningSystem();
	public final transient InputSystem inputSystem = new InputSystem();
	public final transient RenderSystem renderSystem = new RenderSystem();
	public final transient AudioSystem audioSystem = new AudioSystem();
	// TODO public final BulletPhysicsSystem bulletPhysicsSystem = new BulletPhysicsSystem();

	public final void start() {
		if (isActive()) {
			throw new GdxRuntimeException("Scene is already active.");
		}
		activate();
	}

	@Override
	protected final void activated() {
		eventsDispatcher.activate();
	}

	public final void stop() {
		destroy();
		// TODO releaseResources();
	}

	@Override
	protected final void deactivated() {
		super.deactivated();
		eventsDispatcher.deactivate();
		// TODO reset managers and systems
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
			_nodes.add(node);
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
			_nodes.remove(node);
		}
	}

	public void addSystem(SceneSystem2 system) {
		system.setParent(this);
	}

	public void removeSystem(SceneSystem2 system) {
		SceneSystem2 value = _systems.get(system.baseSystemType);
		if (value != system) {
			return;
		}

		if (isDefaultSystem(system)) {
			throw new GdxRuntimeException("Can't remove default system.");
		}

		system.destroy();
	}

	public <T extends SceneSystem2> void removeSystem(Class<T> type) {
		int typeId = SystemType.findType(type);
		SceneSystem2 system = _systems.get(SystemType.findBaseType(typeId));
		if (system == null || !SystemType.isSubtype(typeId, system.systemType)) {
			return;
		}

		if (isDefaultSystem(system)) {
			throw new GdxRuntimeException("Can't remove default system.");
		}

		system.destroy();
	}

	public void removeSystem(int systemType) {
		SceneSystem2 system = _systems.get(SystemType.findBaseType(systemType));
		if (system == null || !SystemType.isSubtype(systemType, system.systemType)) {
			return;
		}

		if (isDefaultSystem(system)) {
			throw new GdxRuntimeException("Can't remove default system.");
		}

		system.destroy();
	}

	private boolean isDefaultSystem(SceneSystem2 system) {
		// TODO Auto-generated method stub
		return false;
	}

	public <T extends SceneSystem2> T getSystem(int typeId) {
		SceneSystem2 value = _systems.get(SystemType.findBaseType(typeId));
		return value != null && SystemType.isSubtype(typeId, value.systemType) ? Values.<T> cast(value) : null;
	}

	public <T extends SceneSystem2> T getSystem(Class<T> type) {
		int typeId = SystemType.findType(type);
		SceneSystem2 value = _systems.get(SystemType.findBaseType(typeId));
		return value != null && SystemType.isSubtype(typeId, value.systemType) ? Values.<T> cast(value) : null;
	}

	public <T extends SceneSystem2 & Poolable> T newSystem(Class<T> systemType) {
		T system = PoolService.obtain(systemType);
		system.setParent(this);
		return system;
	}

	public void addNode(SceneNode2 node) {
		node.setParent(this);
	}

	public void removeNode(SceneNode2 node) {
		if (_nodes.contains(node)) {
			node.destroy();
		}
	}

	public SceneNode2 newNode(String name) {
		SceneNode2 node = PoolService.obtain(SceneNode2.class);
		node.name = name;
		node.setParent(this);
		return node;
	}

	public SceneNode2 getNode(String name) {
		for (int i = 0; i < nodes.size(); i++) {
			SceneNode2 node = nodes.get(i);
			if (Values.isEqual(name, node.name)) {
				return node;
			}
		}
		return null;
	}

	public Array<SceneNode2> getNodes(String name, Array<SceneNode2> out) {
		for (int i = 0; i < nodes.size(); i++) {
			SceneNode2 node = nodes.get(i);
			if (Values.isEqual(name, node.name)) {
				out.add(node);
			}
		}
		return out;
	}

	public String getDiagnostics() {
		StringBuilder builder = new StringBuilder();
		builder.append("Systems [");
		ImmutableArray<SceneSystem2> orderedSystems = _systems.orderedValues();
		for (int i = 0; i < orderedSystems.size(); i++) {
			SceneSystem2 system = orderedSystems.get(i);
			builder.append("\n\t");
			if (!system.isActive()) {
				builder.append("*");
			}
			builder.append(system.getClass().getSimpleName());
		}
		builder.append("]\n");
		builder.append("Nodes [");
		for (int i = 0; i < nodes.size(); i++) {
			SceneNode2 node = nodes.get(i);
			builder.append("\n");
			builder.append(node.getDiagnostics());
		}
		builder.append("]");

		return builder.toString();
	}
}
