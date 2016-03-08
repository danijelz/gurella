package com.gurella.engine.scene;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.event.EventService;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.audio.AudioSystem;
import com.gurella.engine.scene.input.InputSystem;
import com.gurella.engine.scene.layer.LayerManager;
import com.gurella.engine.scene.manager.ComponentManager;
import com.gurella.engine.scene.manager.NodeManager;
import com.gurella.engine.scene.movement.TransformComponent;
import com.gurella.engine.scene.renderable.RenderSystem;
import com.gurella.engine.scene.spatial.SpatialPartitioningSystem;
import com.gurella.engine.scene.spatial.bvh.BvhSpatialPartitioningSystem;
import com.gurella.engine.scene.tag.TagManager;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.utils.IdentityOrderedSet;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.OrderedValuesIntMap;
import com.gurella.engine.utils.Values;

public final class Scene extends ManagedObject {
	transient final SceneEventsDispatcher eventsDispatcher = new SceneEventsDispatcher(this);

	transient final OrderedValuesIntMap<SceneSystem2> _systems = new OrderedValuesIntMap<SceneSystem2>();
	@PropertyDescriptor(property = SceneSystemsProperty.class)
	public final ImmutableArray<SceneSystem2> systems = _systems.orderedValues();
	transient final IdentityOrderedSet<SceneSystem2> _activeSystems = new IdentityOrderedSet<SceneSystem2>();
	public transient final ImmutableArray<SceneSystem2> activeSystems = _activeSystems.orderedItems();

	final IdentityOrderedSet<SceneNode2> _nodes = new IdentityOrderedSet<SceneNode2>();
	public transient final ImmutableArray<SceneNode2> nodes = _nodes.orderedItems();
	transient final IdentityOrderedSet<SceneNode2> _activeNodes = new IdentityOrderedSet<SceneNode2>();
	public transient final ImmutableArray<SceneNode2> activeNodes = _activeNodes.orderedItems();

	transient final IdentityOrderedSet<SceneNodeComponent2> _components = new IdentityOrderedSet<SceneNodeComponent2>();
	public transient final ImmutableArray<SceneNodeComponent2> components = _components.orderedItems();
	transient final IdentityOrderedSet<SceneNodeComponent2> _activeComponents = new IdentityOrderedSet<SceneNodeComponent2>();
	public transient final ImmutableArray<SceneNodeComponent2> activeComponents = _activeComponents.orderedItems();

	public final ComponentManager componentManager = new ComponentManager();
	public final NodeManager nodeManager = new NodeManager();

	public final TagManager tagManager = new TagManager();
	public final LayerManager layerManager = new LayerManager();

	public final SpatialPartitioningSystem<?> spatialPartitioningSystem = new BvhSpatialPartitioningSystem();
	public final InputSystem inputSystem = new InputSystem();
	public final RenderSystem renderSystem = new RenderSystem();
	public final AudioSystem audioSystem = new AudioSystem();
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

	public String getDiagnostics() {
		StringBuilder builder = new StringBuilder();
		builder.append("Systems [");
		for (SceneSystem2 system : _systems.values()) {
			builder.append("\n\t");
			if (!system.isActive()) {
				builder.append("*");
			}
			builder.append(system.getClass().getSimpleName());
		}
		builder.append("]\n");
		builder.append("Nodes [");
		for (SceneNode2 node : nodes) {
			builder.append("\n");
			builder.append(node.getDiagnostics());
		}
		builder.append("]");

		return builder.toString();
	}

	public static void main(String[] args) {
		Scene scene = new Scene();
		scene.newSystem(TestSystem.class);
		SceneNode2 node1 = scene.newNode("node 1");
		node1.newComponent(TestComponent.class);
		System.out.println(scene.getDiagnostics());

		scene.start();
		update();
		System.out.println("\n\n\n");
		System.out.println(scene.getDiagnostics());

		node1.removeComponent(TestComponent.class);
		update();
		System.out.println("\n\n\n");
		System.out.println(scene.getDiagnostics());

		TransformComponent transform1 = node1.newComponent(TransformComponent.class);
		update();

		SceneNode2 node2 = node1.newChild("node 2");
		TransformComponent transform2 = node2.newComponent(TransformComponent.class);
		update();
		System.out.println("\n\n\n");
		System.out.println(scene.getDiagnostics());

		node2.activate();
		update();
		System.out.println("\n\n\n");
		System.out.println(scene.getDiagnostics());

		Vector3 out = new Vector3();
		transform1.translate(1, 1, 1);
		transform2.getWorldTranslation(out);
		System.out.println("\n\n\n");
		System.out.println(out);

		transform1.translate(1, 1, 1);
		transform2.getWorldTranslation(out);
		System.out.println("\n\n\n");
		System.out.println(out);

		scene.addNode(node2);
		update();
		System.out.println("\n\n\n");
		System.out.println(scene.getDiagnostics());

		transform2.getWorldTranslation(out);
		System.out.println("\n\n\n");
		System.out.println(out);

		node1.addChild(node2);
		update();
		System.out.println("\n\n\n");
		System.out.println(scene.getDiagnostics());

		transform2.getWorldTranslation(out);
		System.out.println("\n\n\n");
		System.out.println(out);

		transform1.disable();
		update();
		System.out.println("\n\n\n");
		System.out.println(scene.getDiagnostics());
		transform2.getWorldTranslation(out);
		System.out.println(out);

		transform1.enable();
		update();
		System.out.println("\n\n\n");
		System.out.println(scene.getDiagnostics());
		transform2.getWorldTranslation(out);
		System.out.println(out);
	}

	private static void update() {
		Array<ApplicationUpdateListener> listeners = new Array<ApplicationUpdateListener>();
		EventService.getSubscribers(ApplicationUpdateListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).update();
		}
	}

	private static class TestSystem extends SceneSystem2 implements Poolable {
		@Override
		public void reset() {
		}
	}

	private static class TestComponent extends SceneNodeComponent2 implements Poolable {
		@Override
		public void reset() {
		}
	}
}
