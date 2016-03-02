package com.gurella.engine.scene;

import com.gurella.engine.event.EventService;

public abstract class SceneNodeComponent2 extends SceneElement2 {
	public final int baseComponentType;
	public final int componentType;

	public SceneNodeComponent2() {
		Class<? extends SceneNodeComponent2> type = getClass();
		baseComponentType = ComponentType.getBaseType(type);
		componentType = ComponentType.getType(type);
	}

	@Override
	protected final boolean isActivationAllowed() {
		return super.isActivationAllowed() && isParentHierarchyEnabled();
	}

	public final boolean isHierarchyEnabled() {
		return this.enabled && isParentHierarchyEnabled();
	}

	public final boolean isParentHierarchyEnabled() {
		return getNode() == null ? false : getNode().isHierarchyEnabled();
	}

	public SceneNode2 getNode() {
		return (SceneNode2) getParent();
	}

	public int getNodeId() {
		return getNode().getInstanceId();
	}

	@Override
	protected final void activated() {
		super.activated();
		EventService.subscribe(scene.getInstanceId(), this);
		EventService.subscribe(getNodeId(), this);
	}

	@Override
	protected final void deactivated() {
		super.deactivated();
		EventService.unsubscribe(scene.getInstanceId(), this);
		EventService.unsubscribe(getNodeId(), this);
	}

	final void setParent(SceneNode2 node) {
		super.setParent(node);
	}
}
