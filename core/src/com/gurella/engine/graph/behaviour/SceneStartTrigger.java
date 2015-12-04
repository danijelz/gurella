package com.gurella.engine.graph.behaviour;

import static com.gurella.engine.graph.behaviour.BehaviourEvents.onSceneStart;

import com.gurella.engine.graph.event.EventTrigger;
import com.gurella.engine.signal.Listener0;

public class SceneStartTrigger extends EventTrigger implements Listener0 {
	@Override
	protected void activated() {
		eventSystem.getScene().startSignal.addListener(this);
	}

	@Override
	protected void deactivated() {
		eventSystem.getScene().startSignal.removeListener(this);
	}

	@Override
	public void handle() {
		for (BehaviourComponent behaviourComponent : eventSystem.getListeners(onSceneStart)) {
			behaviourComponent.onSceneStart();
		}
	}
}
