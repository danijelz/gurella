package com.gurella.engine.scene;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.object.ManagedObject;

public final class SceneNode2 extends SceneElement2 implements Poolable {
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
