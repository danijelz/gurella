package com.gurella.engine.scene;

import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

//TODO unused
public class SceneService2 {
	public final Scene scene;

	public SceneService2(Scene scene) {
		this.scene = scene;
	}
	
	protected void activate() {
		if (this instanceof ApplicationEventSubscription) {
			EventService.subscribe(this);
		}
		if (this instanceof SceneEventSubscription) {
			EventService.subscribe(scene.getInstanceId(), this);
		}
		serviceActivated();
	}

	protected void serviceActivated() {
	}
	
	protected void deactivate() {
		if (this instanceof ApplicationEventSubscription) {
			EventService.unsubscribe(this);
		}
		if (this instanceof SceneEventSubscription) {
			EventService.unsubscribe(scene.getInstanceId(), this);
		}
		serviceDeactivated();
	}

	protected void serviceDeactivated() {
	}
}
