package com.gurella.engine.scene;

import static com.gurella.engine.scene.ComponentType.isSubtype;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.base.model.TransientProperty;
import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ImmutableBits;
import com.gurella.engine.utils.OrderedIdentitySet;
import com.gurella.engine.utils.OrderedValuesIntMap;
import com.gurella.engine.utils.Values;

public final class SceneNode2 extends SceneElement2 implements NodeContainer, Poolable {
	String name;

	transient final OrderedIdentitySet<SceneNode2> _childNodes = new OrderedIdentitySet<SceneNode2>();
	@PropertyDescriptor(property = NodeChildrenProperty.class)
	public final ImmutableArray<SceneNode2> childNodes = _childNodes.orderedItems();

	transient final OrderedValuesIntMap<SceneNodeComponent2> _components = new OrderedValuesIntMap<SceneNodeComponent2>();
	@PropertyDescriptor(property = NodeComponentsProperty.class)
	public final ImmutableArray<SceneNodeComponent2> components = _components.orderedValues();

	transient final Bits _componentBits = new Bits(1);
	public transient final ImmutableBits componentBits = new ImmutableBits(_componentBits);

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (Values.isEqual(this.name, name, true)) {
			return;
		}

		String oldName = this.name;
		this.name = name;
		if (isActive()) {
			scene.eventsDispatcher.nodeRenamed(this, oldName, name);
		}
	}

	@Override
	public ImmutableArray<SceneNode2> getNodes() {
		return childNodes;
	}

	@Override
	protected final void validateReparent(ManagedObject newParent) {
		super.validateReparent(newParent);
		Class<?> parentType = newParent.getClass();
		if (parentType != SceneNode2.class && parentType != Scene.class) {
			throw new GdxRuntimeException(
					"Node can only be added to Scene or other Node. Parent type: " + parentType.getSimpleName());
		}
	}

	@Override
	protected final boolean isActivationAllowed() {
		return super.isActivationAllowed() && isParentHierarchyEnabled();
	}

	public final boolean isHierarchyEnabled() {
		return this.enabled && isParentHierarchyEnabled();
	}

	public final boolean isParentHierarchyEnabled() {
		ManagedObject parent = getParent();
		return parent instanceof SceneNode2 ? ((SceneNode2) parent).isHierarchyEnabled() : true;
	}

	@Override
	public final void reset() {
		_childNodes.reset();
		_components.clear();
	}

	public SceneNode2 getParentNode() {
		ManagedObject parent = getParent();
		return parent instanceof SceneNode2 ? (SceneNode2) parent : null;
	}

	final void setParent(SceneNode2 node) {
		super.setParent(node);
	}

	final void setParent(Scene scene) {
		super.setParent(scene);
	}

	@Override
	protected final void activated() {
		scene._activeNodes.add(this);
	}

	@Override
	protected final void deactivated() {
		scene._activeNodes.remove(this);
	}

	@Override
	protected final void childAdded(ManagedObject child) {
		if (child instanceof SceneNodeComponent2) {
			SceneNodeComponent2 component = (SceneNodeComponent2) child;
			int baseType = component.baseComponentType;
			if (_components.containsKey(baseType)) {
				throw new IllegalArgumentException(
						"Node already contains component: " + component.getClass().getName());
			}
			component.scene = scene;
			_components.put(baseType, component);
			_componentBits.set(component.componentType);
		} else {
			SceneNode2 node = (SceneNode2) child;
			node.scene = scene;
			_childNodes.add(node);
		}
	}

	@Override
	protected void childRemoved(ManagedObject child) {
		if (child instanceof SceneNodeComponent2) {
			SceneNodeComponent2 component = (SceneNodeComponent2) child;
			component.scene = null;
			_components.remove(component.baseComponentType);
			_componentBits.clear(component.componentType);
		} else {
			SceneNode2 node = (SceneNode2) child;
			node.scene = null;
			_childNodes.remove(node);
		}
	}

	public void addChild(SceneNode2 child) {
		child.setParent(this);
	}

	//TODO public void removeChild(SceneNode2 child, boolean destroy)
	public void removeChild(SceneNode2 child) {
		if (_childNodes.contains(child)) {
			child.destroy();
		}
	}

	public SceneNode2 newChild(String name) {
		SceneNode2 node = PoolService.obtain(SceneNode2.class);
		node.name = name;
		node.setParent(this);
		return node;
	}

	public SceneNode2 getChildNode(String name) {
		for (int i = 0; i < childNodes.size(); i++) {
			SceneNode2 node = childNodes.get(i);
			if (Values.isEqual(name, node.name)) {
				return node;
			}
		}
		return null;
	}

	public Array<SceneNode2> getChildNodes(String name, Array<SceneNode2> out) {
		for (int i = 0; i < childNodes.size(); i++) {
			SceneNode2 node = childNodes.get(i);
			if (Values.isEqual(name, node.name)) {
				out.add(node);
			}
		}
		return out;
	}

	@TransientProperty
	public int getIndex() {
		ManagedObject parent = getParent();
		if (parent instanceof Scene) {
			return ((Scene) parent)._nodes.indexOf(this);
		} else if (parent instanceof SceneNode2) {
			return ((SceneNode2) parent)._childNodes.indexOf(this);
		} else {
			return -1;
		}
	}

	public void setIndex(int newIndex) {
		ManagedObject parent = getParent();
		if (parent instanceof Scene) {
			((Scene) parent)._nodes.setIndex(newIndex, this);
		} else if (parent instanceof SceneNode2) {
			((SceneNode2) parent)._childNodes.setIndex(newIndex, this);
		} else {
			throw new GdxRuntimeException("Node is not attached to graph.");
		}
	}

	public void addComponent(SceneNodeComponent2 component) {
		component.setParent(this);
	}

	public <T extends SceneNodeComponent2 & Poolable> T newComponent(Class<T> componentType) {
		T component = PoolService.obtain(componentType);
		component.setParent(this);
		return component;
	}

	//TODO public void removeComponent(SceneNodeComponent2 component, boolean destroy)
	public void removeComponent(SceneNodeComponent2 component) {
		SceneNodeComponent2 value = _components.get(component.baseComponentType);
		if (value == component) {
			component.destroy();
		}
	}

	public <T extends SceneNodeComponent2> void removeComponent(Class<T> componentType) {
		int typeId = ComponentType.findType(componentType);
		SceneNodeComponent2 component = _components.get(ComponentType.findBaseType(typeId));
		if (component != null && isSubtype(typeId, component.componentType)) {
			component.destroy();
		}
	}

	public void removeComponent(int componentTypeId) {
		SceneNodeComponent2 component = _components.get(ComponentType.findBaseType(componentTypeId));
		if (component != null && isSubtype(componentTypeId, component.componentType)) {
			component.destroy();
		}
	}

	public <T extends SceneNodeComponent2> T getComponent(int typeId, boolean includeInactive) {
		SceneNodeComponent2 value = _components.get(ComponentType.findBaseType(typeId));
		return value != null && (includeInactive || value.isActive()) && isSubtype(typeId, value.componentType)
				? Values.<T> cast(value) : null;
	}

	public boolean hasComponent(int typeId, boolean includeInactive) {
		SceneNodeComponent2 value = _components.get(ComponentType.findBaseType(typeId));
		return value != null && (includeInactive || value.isActive()) && isSubtype(typeId, value.componentType);
	}

	public <T extends SceneNodeComponent2> T getComponent(Class<T> type, boolean includeInactive) {
		int typeId = ComponentType.findType(type);
		SceneNodeComponent2 value = _components.get(ComponentType.findBaseType(typeId));
		return value != null && (includeInactive || value.isActive()) && isSubtype(typeId, value.componentType)
				? Values.<T> cast(value) : null;
	}

	public <T extends SceneNodeComponent2> boolean hasComponent(Class<T> type, boolean includeInactive) {
		int typeId = ComponentType.findType(type);
		SceneNodeComponent2 value = _components.get(ComponentType.findBaseType(typeId));
		return value != null && (includeInactive || value.isActive()) && isSubtype(typeId, value.componentType);
	}

	public <T extends SceneNodeComponent2> T getComponent(int typeId) {
		return getComponent(typeId, false);
	}

	public boolean hasComponent(int typeId) {
		return hasComponent(typeId, false);
	}

	public <T extends SceneNodeComponent2> T getComponent(Class<T> type) {
		return getComponent(type, false);
	}

	public <T extends SceneNodeComponent2> boolean hasComponent(Class<T> type) {
		return hasComponent(type, false);
	}

	public String getDiagnostics() {
		return appendDiagnostics(new StringBuilder()).toString();
	}

	public String appendDiagnostics(StringBuilder builder) {
		builder.append("\t");
		if (!isActive()) {
			builder.append("*");
		}
		builder.append(name == null ? "-" : name);

		builder.append("\n\tComponents [");
		for (SceneNodeComponent2 component : components) {
			builder.append("\n\t\t");
			if (!component.isActive()) {
				builder.append("*");
			}
			builder.append(component.getClass().getSimpleName());
		}
		builder.append("]");

		builder.append("\n\tChildren [");
		for (SceneNode2 child : _childNodes) {
			builder.append("\n\t\t");
			builder.append(child.appendDiagnostics(builder));
		}
		builder.append("]");

		return builder.toString();
	}
}
