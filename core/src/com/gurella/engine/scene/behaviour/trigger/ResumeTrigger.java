package com.gurella.engine.scene.behaviour.trigger;

import static com.gurella.engine.scene.behaviour.BehaviourEvents.onResume;

import com.gurella.engine.event.Listener0;
import com.gurella.engine.scene.behaviour.BehaviourComponent;
import com.gurella.engine.scene.event.EventTrigger;

public class ResumeTrigger extends EventTrigger implements Listener0 {
	@Override
	protected void start() {
		sceneGraph.scene.resumeSignal.addListener(this);
	}

	@Override
	protected void stop() {
		sceneGraph.scene.resumeSignal.removeListener(this);
	}

	@Override
	public void handle() {
		for (BehaviourComponent behaviourComponent : eventManager.getListeners(onResume)) {
			behaviourComponent.onResume();
		}
	}
}
