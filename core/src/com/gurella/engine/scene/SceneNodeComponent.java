package com.gurella.engine.scene;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.event.EventService;
import com.gurella.engine.managedobject.ManagedObject;
import com.gurella.engine.metatype.TransientProperty;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;
import com.gurella.engine.utils.Sequence;

public abstract class SceneNodeComponent extends SceneElement {
	public final int baseComponentType;
	public final int componentType;

	public SceneNodeComponent() {
		Class<? extends SceneNodeComponent> type = getClass();
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

	public final SceneNode getNode() {
		return (SceneNode) getParent();
	}

	public final int getNodeId() {
		ManagedObject parent = getParent();
		return parent == null ? Sequence.invalidId : parent.getInstanceId();
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

	final void setParent(SceneNode node) {
		super.setParent(node);
	}

	@TransientProperty
	public int getIndex() {
		SceneNode node = getNode();
		return node == null ? -1 : node._components.indexOf(this, true);
	}

	public void setIndex(int newIndex) {
		SceneNode node = getNode();
		if (node == null) {
			throw new GdxRuntimeException("Component is not attached to graph.");
		}
		node._components.setIndex(newIndex, this, true);
	}
}
