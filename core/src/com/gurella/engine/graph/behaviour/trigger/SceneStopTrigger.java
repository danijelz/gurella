package com.gurella.engine.graph.behaviour.trigger;

import static com.gurella.engine.graph.behaviour.BehaviourEvents.onSceneStop;

import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.graph.event.EventTrigger;
import com.gurella.engine.signal.Listener0;

public class SceneStopTrigger extends EventTrigger implements Listener0 {
	@Override
	protected void activated() {
		eventSystem.getScene().stopSignal.addListener(this);
	}

	@Override
	protected void deactivated() {
		eventSystem.getScene().stopSignal.removeListener(this);
	}

	@Override
	public void handle() {
		for (BehaviourComponent behaviourComponent : eventSystem.getListeners(onSceneStop)) {
			behaviourComponent.onSceneStop();
		}
	}
}
