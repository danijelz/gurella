package com.gurella.engine.scene;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.managedobject.ManagedObject;
import com.gurella.engine.metatype.PropertyDescriptor;
import com.gurella.engine.metatype.TransientProperty;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.audio.AudioSystem;
import com.gurella.engine.scene.bullet.BulletPhysicsSystem;
import com.gurella.engine.scene.input.InputSystem;
import com.gurella.engine.scene.manager.ComponentManager;
import com.gurella.engine.scene.manager.NodeManager;
import com.gurella.engine.scene.renderable.RenderSystem;
import com.gurella.engine.scene.spatial.SpatialSystem;
import com.gurella.engine.scene.spatial.bvh.BvhSpatialSystem;
import com.gurella.engine.scene.tag.TagManager;
import com.gurella.engine.scene.ui.UiSystem;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.OrderedIdentitySet;
import com.gurella.engine.utils.OrderedValuesIntMap;
import com.gurella.engine.utils.Values;

//TODO EntityTransmuter
public final class Scene extends ManagedObject implements NodeContainer, Poolable {
	private static Scene current;

	public final transient ComponentManager componentManager = new ComponentManager(this);
	public final transient NodeManager nodeManager = new NodeManager(this);
	public final transient TagManager tagManager = new TagManager(this);

	public final transient SpatialSystem<?> spatialSystem = new BvhSpatialSystem(this);
	public final transient InputSystem inputSystem = new InputSystem(this);
	public final transient RenderSystem renderSystem = new RenderSystem(this);
	public final transient AudioSystem audioSystem = new AudioSystem(this);
	public final transient BulletPhysicsSystem bulletPhysicsSystem = new BulletPhysicsSystem(this);
	public final transient UiSystem uiSystem = new UiSystem(this);

	transient final SceneEventsDispatcher eventsDispatcher = new SceneEventsDispatcher(this);

	transient final OrderedValuesIntMap<SceneSystem> _systems = new OrderedValuesIntMap<SceneSystem>();
	@PropertyDescriptor(property = SceneSystemsProperty.class)
	public final ImmutableArray<SceneSystem> systems = _systems.orderedValues();
	transient final OrderedIdentitySet<SceneSystem> _activeSystems = new OrderedIdentitySet<SceneSystem>();
	public transient final ImmutableArray<SceneSystem> activeSystems = _activeSystems.orderedItems();

	transient final OrderedIdentitySet<SceneNode> _nodes = new OrderedIdentitySet<SceneNode>();
	@PropertyDescriptor(property = SceneNodesProperty.class)
	public final ImmutableArray<SceneNode> nodes = _nodes.orderedItems();
	transient final OrderedIdentitySet<SceneNode> _activeNodes = new OrderedIdentitySet<SceneNode>();
	public transient final ImmutableArray<SceneNode> activeNodes = _activeNodes.orderedItems();

	transient final OrderedIdentitySet<SceneNodeComponent> _components = new OrderedIdentitySet<SceneNodeComponent>();
	public transient final ImmutableArray<SceneNodeComponent> components = _components.orderedItems();
	transient final OrderedIdentitySet<SceneNodeComponent> _activeComponents = new OrderedIdentitySet<SceneNodeComponent>();
	public transient final ImmutableArray<SceneNodeComponent> activeComponents = _activeComponents.orderedItems();

	public static Scene getCurrent() {
		return current;
	}

	public final void start() {
		if (isActive()) {
			throw new GdxRuntimeException("Scene is already active.");
		}

		activate();
	}

	@Override
	protected void preActivation() {
		current = this;

		componentManager.activate();
		nodeManager.activate();
		tagManager.activate();

		spatialSystem.activate();
		inputSystem.activate();
		renderSystem.activate();
		uiSystem.activate();
		audioSystem.activate();
		bulletPhysicsSystem.activate();
	}

	@Override
	protected void postActivation() {
		eventsDispatcher.activate();
	}

	public final void stop() {
		destroy();
	}

	@Override
	protected final void preDeactivation() {
		eventsDispatcher.deactivate();
	}

	@Override
	protected void postDeactivation() {
		componentManager.deactivate();
		nodeManager.deactivate();
		tagManager.deactivate();

		spatialSystem.deactivate();
		inputSystem.deactivate();
		renderSystem.deactivate();
		uiSystem.deactivate();
		audioSystem.deactivate();
		bulletPhysicsSystem.deactivate();

		current = null;
	}

	@Override
	protected final void childAdded(ManagedObject child) {
		if (child instanceof SceneNode) {
			SceneNode node = (SceneNode) child;
			node.scene = this;
			updateNodeChildren(node);
			_nodes.add(node);
		} else {
			SceneSystem system = (SceneSystem) child;
			int baseSystemType = system.baseSystemType;
			if (_systems.containsKey(baseSystemType)) {
				throw new IllegalArgumentException("Scene already contains system: " + system.getClass().getName());
			}
			system.scene = this;
			_systems.put(baseSystemType, system);
		}
	}

	private void updateNodeChildren(SceneNode node) {
		ImmutableArray<ManagedObject> nodeChildren = node.children;
		for (int i = 0, n = nodeChildren.size(); i < n; i++) {
			SceneElement sceneElement = (SceneElement) nodeChildren.get(i);
			sceneElement.scene = this;
			if (sceneElement instanceof SceneNode) {
				updateNodeChildren((SceneNode) sceneElement);
			}
		}
	}

	@Override
	protected void childRemoved(ManagedObject child) {
		if (child instanceof SceneNode) {
			SceneNode node = (SceneNode) child;
			node.scene = null;
			_nodes.remove(node);
		} else {
			SceneSystem system = (SceneSystem) child;
			system.scene = null;
			_systems.remove(system.baseSystemType);
		}
	}

	public void addSystem(SceneSystem system) {
		system.setParent(this);
	}

	public void removeSystem(SceneSystem system) {
		SceneSystem value = _systems.get(system.baseSystemType);
		if (value != system) {
			return;
		}

		system.destroy();
	}

	public <T extends SceneSystem> void removeSystem(Class<T> type) {
		int typeId = SystemType.findType(type);
		SceneSystem system = _systems.get(SystemType.findBaseType(typeId));
		if (system == null || !SystemType.isSubtype(typeId, system.systemType)) {
			return;
		}

		system.destroy();
	}

	public void removeSystem(int systemType) {
		SceneSystem system = _systems.get(SystemType.findBaseType(systemType));
		if (system == null || !SystemType.isSubtype(systemType, system.systemType)) {
			return;
		}

		system.destroy();
	}

	public <T extends SceneSystem> T getSystem(int typeId) {
		SceneSystem value = _systems.get(SystemType.findBaseType(typeId));
		return value != null && SystemType.isSubtype(typeId, value.systemType) ? Values.<T> cast(value) : null;
	}

	public <T extends SceneSystem> T getSystem(Class<T> type) {
		int typeId = SystemType.findType(type);
		SceneSystem value = _systems.get(SystemType.findBaseType(typeId));
		return value != null && SystemType.isSubtype(typeId, value.systemType) ? Values.<T> cast(value) : null;
	}

	public <T extends SceneSystem & Poolable> T newSystem(Class<T> systemType) {
		T system = PoolService.obtain(systemType);
		system.setParent(this);
		return system;
	}

	@Override
	public ImmutableArray<SceneNode> getNodes() {
		return nodes;
	}

	public void addNode(SceneNode node) {
		node.setParent(this);
	}

	public void removeNode(SceneNode node) {
		if (_nodes.contains(node)) {
			node.destroy();
		}
	}

	public SceneNode newNode(String name) {
		SceneNode node = PoolService.obtain(SceneNode.class);
		node.name = name;
		node.setParent(this);
		return node;
	}

	public SceneNode getNode(String name) {
		for (int i = 0; i < nodes.size(); i++) {
			SceneNode node = nodes.get(i);
			if (Values.isEqual(name, node.name)) {
				return node;
			}
		}
		return null;
	}

	public Array<SceneNode> getNodes(String name, Array<SceneNode> out) {
		for (int i = 0; i < nodes.size(); i++) {
			SceneNode node = nodes.get(i);
			if (Values.isEqual(name, node.name)) {
				out.add(node);
			}
		}
		return out;
	}

	@TransientProperty
	public int getNodeIndex(SceneNode child) {
		return _nodes.indexOf(child);
	}

	public void setNodeIndex(int newIndex, SceneNode child) {
		_nodes.setIndex(newIndex, child);
	}

	public String getDiagnostics() {
		StringBuilder builder = new StringBuilder();
		builder.append("Systems [");
		ImmutableArray<SceneSystem> orderedSystems = _systems.orderedValues();
		for (int i = 0; i < orderedSystems.size(); i++) {
			SceneSystem system = orderedSystems.get(i);
			builder.append("\n\t");
			if (!system.isActive()) {
				builder.append("*");
			}
			builder.append(system.getClass().getSimpleName());
		}
		builder.append("]\n");
		builder.append("Nodes [");
		for (int i = 0; i < nodes.size(); i++) {
			SceneNode node = nodes.get(i);
			builder.append("\n");
			builder.append(node.getDiagnostics());
		}
		builder.append("]");

		return builder.toString();
	}

	@Override
	public void reset() {
		eventsDispatcher.reset();
	}
}
