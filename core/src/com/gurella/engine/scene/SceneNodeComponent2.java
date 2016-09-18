package com.gurella.engine.scene;

import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;
import com.gurella.engine.utils.SequenceGenerator;

public abstract class SceneNodeComponent2 extends SceneElement2 {
	public final int baseComponentType;
	public final int componentType;

	public SceneNodeComponent2() {
		Class<? extends SceneNodeComponent2> type = getClass();
		baseComponentType = ComponentType.getBaseType(type);
		componentType = ComponentType.findType(type);
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

	public final SceneNode2 getNode() {
		return (SceneNode2) getParent();
	}

	public final int getNodeId() {
		ManagedObject parent = getParent();
		return parent == null ? SequenceGenerator.invalidId : parent.getInstanceId();
	}

	@Override
	protected final void activated() {
		super.activated();
		scene._activeComponents.add(this);
		if (this instanceof SceneEventSubscription) {
			EventService.subscribe(scene.getInstanceId(), this);
		}
		if (this instanceof NodeEventSubscription) {
			EventService.subscribe(getNodeId(), this);
		}
		componentActivated();
		scene.eventsDispatcher.componentActivated(this);
	}

	protected void componentActivated() {
	}

	@Override
	protected final void deactivated() {
		super.deactivated();
		scene.eventsDispatcher.componentDeactivated(this);
		if (this instanceof SceneEventSubscription) {
			EventService.unsubscribe(scene.getInstanceId(), this);
		}
		if (this instanceof NodeEventSubscription) {
			EventService.unsubscribe(getNodeId(), this);
		}
		componentDeactivated();
		scene._activeComponents.remove(this);
	}

	protected void componentDeactivated() {
	}

	final void setParent(SceneNode2 node) {
		super.setParent(node);
	}
}
