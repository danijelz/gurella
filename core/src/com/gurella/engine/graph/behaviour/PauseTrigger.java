package com.gurella.engine.graph.behaviour;

import static com.gurella.engine.graph.behaviour.BehaviourEvents.onPause;

import com.gurella.engine.graph.event.EventTrigger;
import com.gurella.engine.signal.Listener0;

public class PauseTrigger extends EventTrigger implements Listener0 {
	@Override
	protected void activated() {
		eventSystem.getScene().pauseSignal.addListener(this);
	}

	@Override
	protected void deactivated() {
		eventSystem.getScene().pauseSignal.removeListener(this);
	}

	@Override
	public void handle() {
		for (BehaviourComponent behaviourComponent : eventSystem.getListeners(onPause)) {
			behaviourComponent.onPause();
		}
	}
}
