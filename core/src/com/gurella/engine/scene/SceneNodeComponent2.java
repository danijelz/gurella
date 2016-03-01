package com.gurella.engine.scene;

public abstract class SceneNodeComponent2 extends SceneElement2 {
	public final int baseComponentType;
	public final int componentType;

	transient SceneNode2 node;

	public SceneNodeComponent2() {
		Class<? extends SceneNodeComponent2> type = getClass();
		baseComponentType = SceneNodeComponentType.getBaseType(type);
		componentType = SceneNodeComponentType.getType(type);
	}

	@Override
	protected final boolean isActivationAllowed() {
		return super.isActivationAllowed() && isParentHierarchyEnabled();
	}

	public final boolean isHierarchyEnabled() {
		return this.enabled && isParentHierarchyEnabled();
	}

	public final boolean isParentHierarchyEnabled() {
		return node == null ? false : node.isHierarchyEnabled();
	}

	public SceneNode2 getNode() {
		return node;
	}

	public int getNodeId() {
		return node.getInstanceId();
	}
}
