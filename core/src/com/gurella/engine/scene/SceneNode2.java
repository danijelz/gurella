package com.gurella.engine.scene;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.resource.model.ResourceProperty;
import com.gurella.engine.resource.model.common.SceneNodeChildrenModelProperty;
import com.gurella.engine.resource.model.common.SceneNodeComponentsModelProperty;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ImmutableIntMapValues;

public final class SceneNode2 extends SceneElement2 implements Poolable {
	// TODO remove
	@ResourceProperty(model = SceneNodeChildrenModelProperty.class)
	final Array<SceneNode2> childrenPrivate = new Array<SceneNode2>();
	public transient final ImmutableArray<SceneNode2> childNodes = ImmutableArray.with(childrenPrivate);

	// TODO remove
	@ResourceProperty(model = SceneNodeComponentsModelProperty.class)
	final IntMap<SceneNodeComponent2> componentsPrivate = new IntMap<SceneNodeComponent2>();
	public transient final ImmutableIntMapValues<SceneNodeComponent2> components = ImmutableIntMapValues
			.with(componentsPrivate);

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
	public void reset() {
		super.reset();
		// TODO
	}

	public void addChild(SceneNode2 child) {

	}

	public void removeChild(SceneNode2 child) {

	}

	public void addComponent(SceneNodeComponent2 component) {

	}

	public void removeComponent(SceneNodeComponent2 component) {

	}
}
