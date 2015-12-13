package com.gurella.engine.scene.behaviour.trigger;

import static com.gurella.engine.scene.behaviour.BehaviourEvents.onSceneStop;

import com.gurella.engine.event.Listener0;
import com.gurella.engine.scene.behaviour.BehaviourComponent;
import com.gurella.engine.scene.event.EventTrigger;

public class SceneStopTrigger extends EventTrigger implements Listener0 {
	@Override
	protected void start() {
		sceneGraph.scene.stopSignal.addListener(this);
	}

	@Override
	protected void stop() {
		sceneGraph.scene.stopSignal.removeListener(this);
	}

	@Override
	public void handle() {
		for (BehaviourComponent behaviourComponent : eventManager.getListeners(onSceneStop)) {
			behaviourComponent.onSceneStop();
		}
	}
}
