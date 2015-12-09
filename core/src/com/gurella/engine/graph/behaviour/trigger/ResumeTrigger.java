package com.gurella.engine.graph.behaviour.trigger;

import static com.gurella.engine.graph.behaviour.BehaviourEvents.onResume;

import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.graph.event.EventTrigger;
import com.gurella.engine.signal.Listener0;

public class ResumeTrigger extends EventTrigger implements Listener0 {
	@Override
	protected void activated() {
		eventSystem.getScene().resumeSignal.addListener(this);
	}

	@Override
	protected void deactivated() {
		eventSystem.getScene().resumeSignal.removeListener(this);
	}

	@Override
	public void handle() {
		for (BehaviourComponent behaviourComponent : eventSystem.getListeners(onResume)) {
			behaviourComponent.onResume();
		}
	}
}
