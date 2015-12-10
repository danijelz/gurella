package com.gurella.engine.graph.behaviour.trigger;

import static com.gurella.engine.graph.behaviour.BehaviourEvents.onResume;

import com.gurella.engine.event.Listener0;
import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.graph.event.EventTrigger;

public class ResumeTrigger extends EventTrigger implements Listener0 {
	@Override
	protected void activated() {
		sceneGraph.scene.resumeSignal.addListener(this);
	}

	@Override
	protected void deactivated() {
		sceneGraph.scene.resumeSignal.removeListener(this);
	}

	@Override
	public void handle() {
		for (BehaviourComponent behaviourComponent : eventManager.getListeners(onResume)) {
			behaviourComponent.onResume();
		}
	}
}
