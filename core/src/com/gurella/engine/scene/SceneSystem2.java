package com.gurella.engine.scene;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.base.model.TransientProperty;
import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public abstract class SceneSystem2 extends SceneElement2 {
	public final int baseSystemType;
	public final int systemType;

	public SceneSystem2() {
		Class<? extends SceneSystem2> type = getClass();
		baseSystemType = SystemType.getBaseType(type);
		systemType = SystemType.findType(type);
	}

	@Override
	protected final void validateReparent(ManagedObject newParent) {
		super.validateReparent(newParent);
		if (newParent.getClass() != Scene.class) {
			throw new GdxRuntimeException("System can only be added to Scene.");
		}
	}

	@Override
	protected final boolean isActivationAllowed() {
		return super.isActivationAllowed();
	}

	final void setParent(Scene scene) {
		super.setParent(scene);
	}

	@Override
	protected final void activated() {
		super.activated();
		scene._activeSystems.add(this);
		if (this instanceof SceneEventSubscription) {
			EventService.subscribe(scene.getInstanceId(), this);
		}
		systemActivated();
	}

	protected void systemActivated() {
	}

	@Override
	protected final void deactivated() {
		super.deactivated();
		scene._activeSystems.remove(this);
		if (this instanceof SceneEventSubscription) {
			EventService.unsubscribe(scene.getInstanceId(), this);
		}
		systemDeactivated();
	}

	protected void systemDeactivated() {
	}

	@TransientProperty
	public int getIndex() {
		Scene scene = getScene();
		return scene == null ? -1 : scene._systems.indexOf(this, true);
	}

	public void setIndex(int newIndex) {
		Scene scene = getScene();
		if (scene == null) {
			throw new GdxRuntimeException("System is not attached to graph.");
		}
		scene._systems.setIndex(newIndex, this, true);
	}
}
