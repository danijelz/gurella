package com.gurella.engine.scene;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

//TODO detach from SceneElement hierarchy
public class SceneService extends SceneElement2 {
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
		if (this instanceof SceneEventSubscription) {
			EventService.subscribe(scene.getInstanceId(), this);
		}
		onActivate();
	}

	protected void onActivate() {
	}

	@Override
	protected final void deactivated() {
		super.deactivated();
		if (this instanceof SceneEventSubscription) {
			EventService.unsubscribe(scene.getInstanceId(), this);
		}
		onDeactivate();
	}

	protected void onDeactivate() {
	}
}
