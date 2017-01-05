package com.gurella.engine.scene;

import com.gurella.engine.event.EventService;
import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public class BuiltinSceneSystem {
	public final Scene scene;

	public BuiltinSceneSystem(Scene scene) {
		this.scene = scene;
	}

	protected void activate() {
		if (this instanceof ApplicationEventSubscription) {
			EventService.subscribe((EventSubscription) this);
		}
		if (this instanceof SceneEventSubscription) {
			EventService.subscribe(scene.getInstanceId(), (EventSubscription) this);
		}
		serviceActivated();
	}

	protected void serviceActivated() {
	}

	protected void deactivate() {
		if (this instanceof ApplicationEventSubscription) {
			EventService.unsubscribe((EventSubscription) this);
		}
		if (this instanceof SceneEventSubscription) {
			EventService.unsubscribe(scene.getInstanceId(), (EventSubscription) this);
		}
		serviceDeactivated();
	}

	protected void serviceDeactivated() {
	}
}
