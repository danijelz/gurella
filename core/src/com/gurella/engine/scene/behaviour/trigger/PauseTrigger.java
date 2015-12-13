package com.gurella.engine.scene.behaviour.trigger;

import static com.gurella.engine.scene.behaviour.BehaviourEvents.onPause;

import com.gurella.engine.event.Listener0;
import com.gurella.engine.scene.behaviour.BehaviourComponent;
import com.gurella.engine.scene.event.EventTrigger;

public class PauseTrigger extends EventTrigger implements Listener0 {
	@Override
	protected void start() {
		scene.pauseSignal.addListener(this);
	}

	@Override
	protected void stop() {
		scene.pauseSignal.removeListener(this);
	}

	@Override
	public void handle() {
		for (BehaviourComponent behaviourComponent : eventManager.getListeners(onPause)) {
			behaviourComponent.onPause();
		}
	}
}
