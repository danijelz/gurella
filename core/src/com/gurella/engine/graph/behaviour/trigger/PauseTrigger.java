package com.gurella.engine.graph.behaviour.trigger;

import static com.gurella.engine.graph.behaviour.BehaviourEvents.onPause;

import com.gurella.engine.event.Listener0;
import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.graph.event.EventTrigger;

public class PauseTrigger extends EventTrigger implements Listener0 {
	@Override
	protected void activated() {
		sceneGraph.scene.pauseSignal.addListener(this);
	}

	@Override
	protected void deactivated() {
		sceneGraph.scene.pauseSignal.removeListener(this);
	}

	@Override
	public void handle() {
		for (BehaviourComponent behaviourComponent : eventManager.getListeners(onPause)) {
			behaviourComponent.onPause();
		}
	}
}
