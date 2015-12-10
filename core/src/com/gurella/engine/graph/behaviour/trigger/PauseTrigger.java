package com.gurella.engine.graph.behaviour.trigger;

import static com.gurella.engine.graph.behaviour.BehaviourEvents.onPause;

import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.graph.event.EventTrigger;
import com.gurella.engine.signal.Listener0;

public class PauseTrigger extends EventTrigger implements Listener0 {
	@Override
	protected void activated() {
		eventManager.getScene().pauseSignal.addListener(this);
	}

	@Override
	protected void deactivated() {
		eventManager.getScene().pauseSignal.removeListener(this);
	}

	@Override
	public void handle() {
		for (BehaviourComponent behaviourComponent : eventManager.getListeners(onPause)) {
			behaviourComponent.onPause();
		}
	}
}
