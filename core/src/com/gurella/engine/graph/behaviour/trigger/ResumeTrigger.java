package com.gurella.engine.graph.behaviour.trigger;

import static com.gurella.engine.graph.behaviour.BehaviourEvents.onResume;

import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.graph.event.EventTrigger;
import com.gurella.engine.signal.Listener0;

public class ResumeTrigger extends EventTrigger implements Listener0 {
	@Override
	protected void activated() {
		eventManager.getScene().resumeSignal.addListener(this);
	}

	@Override
	protected void deactivated() {
		eventManager.getScene().resumeSignal.removeListener(this);
	}

	@Override
	public void handle() {
		for (BehaviourComponent behaviourComponent : eventManager.getListeners(onResume)) {
			behaviourComponent.onResume();
		}
	}
}
