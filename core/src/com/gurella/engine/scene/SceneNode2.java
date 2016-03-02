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
	// TODO remove
	@ResourceProperty(model = SceneNodeChildrenModelProperty.class)
	final IdentityOrderedSet<SceneNode2> _childNodes = new IdentityOrderedSet<SceneNode2>();
	public transient final ImmutableArray<SceneNode2> childNodes = _childNodes.orderedItems();

	// TODO remove
	@ResourceProperty(model = SceneNodeComponentsModelProperty.class)
	final IntMap<SceneNodeComponent2> _components = new IntMap<SceneNodeComponent2>();
	public transient final ImmutableIntMapValues<SceneNodeComponent2> components = ImmutableIntMapValues
			.with(_components);

	@Override
	protected final void validateReparent(ManagedObject newParent) {
		super.validateReparent(newParent);
		if (!(newParent instanceof SceneNode2) && !(newParent instanceof Scene)) {
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
	}

	@Override
	public final void reset() {
		super.reset();
	}

	public void addChild(SceneNode2 child) {

	}

	public void removeChild(SceneNode2 child) {

	}

	public void addComponent(SceneNodeComponent2 component) {

	}

	public void removeComponent(SceneNodeComponent2 component) {

	}

	public <T extends SceneNodeComponent2> T getComponent(int typeId) {
		return Values.cast(_components.get(SceneNodeComponentType.findBaseType(typeId)));
	}

	public <T extends SceneNodeComponent2> T getComponent(Class<T> type) {
		return Values.cast(_components.get(SceneNodeComponentType.findBaseType(type)));
	}
}
