package com.gurella.engine.scene;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.resource.model.ResourceProperty;
import com.gurella.engine.resource.model.common.SceneNodeChildrenModelProperty;
import com.gurella.engine.resource.model.common.SceneNodeComponentsModelProperty;
import com.gurella.engine.utils.IdentityOrderedSet;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ImmutableIntMapValues;
import com.gurella.engine.utils.Values;

public final class SceneNode2 extends SceneElement2 implements Poolable {
	String name;
	
	// TODO remove
	@ResourceProperty(model = SceneNodeChildrenModelProperty.class)
	final IdentityOrderedSet<SceneNode2> _childNodes = new IdentityOrderedSet<SceneNode2>();
	public transient final ImmutableArray<SceneNode2> childNodes = _childNodes.orderedItems();

	// TODO remove
	@ResourceProperty(model = SceneNodeComponentsModelProperty.class)
	final IntMap<SceneNodeComponent2> _components = new IntMap<SceneNodeComponent2>();
	public transient final ImmutableIntMapValues<SceneNodeComponent2> components = ImmutableIntMapValues
			.with(_components);
	
	public String getName() {
		return name;
	}

	@Override
	protected final void validateReparent(ManagedObject newParent) {
		super.validateReparent(newParent);
		Class<?> parentType = newParent.getClass();
		if (parentType != SceneNode2.class || parentType != Scene.class) {
			throw new GdxRuntimeException("Node can only be added to Scene or other Node.");
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
	protected final void clear() {
		super.clear();
		_childNodes.clear();
		_components.clear();
	}

	@Override
	public final void reset() {
		super.reset();
	}
	
	final void setParent(SceneNode2 node) {
		super.setParent(node);
	}
	
	final void setParent(Scene scene) {
		super.setParent(scene);
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
		} else {
			SceneNode2 node = (SceneNode2) child;
			node.scene = null;
			_childNodes.remove(node);
		}
	}

	public void addChild(SceneNode2 child) {
		child.setParent(this);
	}

	public void removeChild(SceneNode2 child) {
		child.destroy();
	}

	public void addComponent(SceneNodeComponent2 component) {
		component.setParent(this);
	}

	public void removeComponent(SceneNodeComponent2 component) {
		component.destroy();
	}

	public <T extends SceneNodeComponent2> T getComponent(int typeId) {
		SceneNodeComponent2 value = _components.get(ComponentType.findBaseType(typeId));
		return value != null && ComponentType.isSubtype(typeId, value.componentType) ? Values.cast(value) : null;
	}

	public <T extends SceneNodeComponent2> T getComponent(Class<T> type) {
		int typeId = ComponentType.findType(type);
		SceneNodeComponent2 value = _components.get(ComponentType.findBaseType(typeId));
		return value != null && ComponentType.isSubtype(typeId, value.componentType) ? Values.cast(value) : null;
	}
}
