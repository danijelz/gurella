package com.gurella.engine.scene;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.event.EventService;

public abstract class SceneSystem2 extends SceneElement2 {
	public final int baseSystemType;
	public final int systemType;

	public SceneSystem2() {
		Class<? extends SceneSystem2> type = getClass();
		baseSystemType = SceneSystemType.getBaseSystemType(type);
		systemType = SceneSystemType.getSystemType(type);
	}

	@Override
	protected final void validateReparent(ManagedObject newParent) {
		super.validateReparent(newParent);
		if (!(newParent instanceof Scene)) {
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
		EventService.subscribe(scene.getInstanceId(), this);
	}

	@Override
	protected final void deactivated() {
		super.deactivated();
		scene._activeSystems.remove(this);
		EventService.unsubscribe(scene.getInstanceId(), this);
	}
}
