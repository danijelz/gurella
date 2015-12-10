package com.gurella.engine.graph.behaviour.trigger;

import static com.gurella.engine.graph.behaviour.BehaviourEvents.onSceneStart;

import com.gurella.engine.event.Listener0;
import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.graph.event.EventTrigger;

public class SceneStartTrigger extends EventTrigger implements Listener0 {
	@Override
	protected void activated() {
		eventManager.getScene().startSignal.addListener(this);
	}

	@Override
	protected void deactivated() {
		eventManager.getScene().startSignal.removeListener(this);
	}

	@Override
	public void handle() {
		for (BehaviourComponent behaviourComponent : eventManager.getListeners(onSceneStart)) {
			behaviourComponent.onSceneStart();
		}
	}
}
