package com.gurella.engine.graph.behaviour.trigger;

import static com.gurella.engine.graph.behaviour.BehaviourEvents.onSceneStop;

import com.gurella.engine.event.Listener0;
import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.graph.event.EventTrigger;

public class SceneStopTrigger extends EventTrigger implements Listener0 {
	@Override
	protected void activated() {
		eventManager.getScene().stopSignal.addListener(this);
	}

	@Override
	protected void deactivated() {
		eventManager.getScene().stopSignal.removeListener(this);
	}

	@Override
	public void handle() {
		for (BehaviourComponent behaviourComponent : eventManager.getListeners(onSceneStop)) {
			behaviourComponent.onSceneStop();
		}
	}
}
