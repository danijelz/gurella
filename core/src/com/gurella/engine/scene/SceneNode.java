package com.gurella.engine.scene;

import static com.gurella.engine.scene.ComponentType.isSubtype;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.managedobject.ManagedObject;
import com.gurella.engine.metatype.PropertyDescriptor;
import com.gurella.engine.metatype.TransientProperty;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ImmutableBits;
import com.gurella.engine.utils.OrderedIdentitySet;
import com.gurella.engine.utils.OrderedValuesIntMap;
import com.gurella.engine.utils.Values;

public final class SceneNode extends SceneElement implements NodeContainer, Poolable {
	String name;

	transient final OrderedIdentitySet<SceneNode> _childNodes = new OrderedIdentitySet<SceneNode>();
	@PropertyDescriptor(property = NodeChildrenProperty.class)
	public final ImmutableArray<SceneNode> childNodes = _childNodes.orderedItems();

	transient final OrderedValuesIntMap<SceneNodeComponent> _components = new OrderedValuesIntMap<SceneNodeComponent>();
	@PropertyDescriptor(property = NodeComponentsProperty.class)
	public final ImmutableArray<SceneNodeComponent> components = _components.orderedValues();

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
	public ImmutableArray<SceneNode> getNodes() {
		return childNodes;
	}

	@Override
	protected final void validateReparent(ManagedObject newParent) {
		super.validateReparent(newParent);

		if (newParent == null) {
			return;
		}

		Class<?> parentType = newParent.getClass();
		if (parentType != SceneNode.class && parentType != Scene.class) {
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
		return parent instanceof SceneNode ? ((SceneNode) parent).isHierarchyEnabled() : true;
	}

	@Override
	public final void reset() {
		_childNodes.reset();
		_components.clear();
	}

	public SceneNode getParentNode() {
		ManagedObject parent = getParent();
		return parent instanceof SceneNode ? (SceneNode) parent : null;
	}

	final void setParent(SceneNode node) {
		super.setParent(node);
	}

	final void setParent(Scene scene) {
		super.setParent(scene);
	}

	final void unsetParent() {
		super.setParent(null);
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
		if (child instanceof SceneNodeComponent) {
			SceneNodeComponent component = (SceneNodeComponent) child;
			int baseType = component.baseComponentType;
			if (_components.containsKey(baseType)) {
				throw new IllegalArgumentException(
						"Node already contains component: " + component.getClass().getName());
			}
			component.scene = scene;
			_components.put(baseType, component);
			_componentBits.set(component.componentType);
		} else {
			SceneNode node = (SceneNode) child;
			node.scene = scene;
			_childNodes.add(node);
		}
	}

	@Override
	protected void childRemoved(ManagedObject child) {
		if (child instanceof SceneNodeComponent) {
			SceneNodeComponent component = (SceneNodeComponent) child;
			component.scene = null;
			_components.remove(component.baseComponentType);
			_componentBits.clear(component.componentType);
		} else {
			SceneNode node = (SceneNode) child;
			node.scene = null;
			_childNodes.remove(node);
		}
	}

	public void addChild(SceneNode child) {
		child.setParent(this);
	}

	public void removeChild(SceneNode child) {
		if (_childNodes.contains(child)) {
			child.destroy();
		}
	}

	public void removeChild(SceneNode child, boolean destroy) {
		if (!_childNodes.contains(child)) {
			return;
		}

		if (destroy) {
			child.destroy();
		} else {
			super.setParent(null);
		}
	}

	public SceneNode newChild(String name) {
		SceneNode node = PoolService.obtain(SceneNode.class);
		node.name = name;
		node.setParent(this);
		return node;
	}

	public SceneNode getChildNode(String name) {
		for (int i = 0; i < childNodes.size(); i++) {
			SceneNode node = childNodes.get(i);
			if (Values.isEqual(name, node.name)) {
				return node;
			}
		}
		return null;
	}

	public Array<SceneNode> getChildNodes(String name, Array<SceneNode> out) {
		for (int i = 0; i < childNodes.size(); i++) {
			SceneNode node = childNodes.get(i);
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
		} else if (parent instanceof SceneNode) {
			return ((SceneNode) parent)._childNodes.indexOf(this);
		} else {
			return -1;
		}
	}

	public void setIndex(int newIndex) {
		ManagedObject parent = getParent();
		if (parent instanceof Scene) {
			((Scene) parent)._nodes.setIndex(newIndex, this);
		} else if (parent instanceof SceneNode) {
			((SceneNode) parent)._childNodes.setIndex(newIndex, this);
		} else {
			throw new GdxRuntimeException("Node is not attached to graph.");
		}
	}

	public void addComponent(SceneNodeComponent component) {
		component.setParent(this);
	}

	public <T extends SceneNodeComponent & Poolable> T newComponent(Class<T> componentType) {
		T component = PoolService.obtain(componentType);
		component.setParent(this);
		return component;
	}

	public void removeComponent(SceneNodeComponent component) {
		SceneNodeComponent value = _components.get(component.baseComponentType);
		if (value == component) {
			component.destroy();
		}
	}

	public void removeComponent(SceneNodeComponent component, boolean destroy) {
		SceneNodeComponent value = _components.get(component.baseComponentType);
		if (value != component) {
			return;
		}

		if (destroy) {
			component.destroy();
		} else {
			component.unsetParent();
		}
	}

	public <T extends SceneNodeComponent> void removeComponent(Class<T> componentType) {
		int typeId = ComponentType.findType(componentType);
		SceneNodeComponent component = _components.get(ComponentType.findBaseType(typeId));
		if (component != null && isSubtype(typeId, component.componentType)) {
			component.destroy();
		}
	}

	public void removeComponent(int componentTypeId) {
		SceneNodeComponent component = _components.get(ComponentType.findBaseType(componentTypeId));
		if (component != null && isSubtype(componentTypeId, component.componentType)) {
			component.destroy();
		}
	}

	public <T extends SceneNodeComponent> T getComponent(int typeId, boolean includeInactive) {
		SceneNodeComponent value = _components.get(ComponentType.findBaseType(typeId));
		return value != null && (includeInactive || value.isActive()) && isSubtype(typeId, value.componentType)
				? Values.<T> cast(value) : null;
	}

	public boolean hasComponent(int typeId, boolean includeInactive) {
		SceneNodeComponent value = _components.get(ComponentType.findBaseType(typeId));
		return value != null && (includeInactive || value.isActive()) && isSubtype(typeId, value.componentType);
	}

	public <T extends SceneNodeComponent> T getComponent(Class<T> type, boolean includeInactive) {
		int typeId = ComponentType.findType(type);
		SceneNodeComponent value = _components.get(ComponentType.findBaseType(typeId));
		return value != null && (includeInactive || value.isActive()) && isSubtype(typeId, value.componentType)
				? Values.<T> cast(value) : null;
	}

	public <T extends SceneNodeComponent> boolean hasComponent(Class<T> type, boolean includeInactive) {
		int typeId = ComponentType.findType(type);
		SceneNodeComponent value = _components.get(ComponentType.findBaseType(typeId));
		return value != null && (includeInactive || value.isActive()) && isSubtype(typeId, value.componentType);
	}

	public <T extends SceneNodeComponent> T getComponent(int typeId) {
		return getComponent(typeId, false);
	}

	public boolean hasComponent(int typeId) {
		return hasComponent(typeId, false);
	}

	public <T extends SceneNodeComponent> T getComponent(Class<T> type) {
		return getComponent(type, false);
	}

	public <T extends SceneNodeComponent> boolean hasComponent(Class<T> type) {
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
		for (SceneNodeComponent component : components) {
			builder.append("\n\t\t");
			if (!component.isActive()) {
				builder.append("*");
			}
			builder.append(component.getClass().getSimpleName());
		}
		builder.append("]");

		builder.append("\n\tChildren [");
		for (SceneNode child : _childNodes) {
			builder.append("\n\t\t");
			builder.append(child.appendDiagnostics(builder));
		}
		builder.append("]");

		return builder.toString();
	}
}
