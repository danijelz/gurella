package com.gurella.engine.scene.behaviour.trigger;

import static com.gurella.engine.scene.behaviour.BehaviourEvents.onSceneStart;

import com.gurella.engine.event.Listener0;
import com.gurella.engine.scene.behaviour.BehaviourComponent;
import com.gurella.engine.scene.event.EventTrigger;

public class SceneStartTrigger extends EventTrigger implements Listener0 {
	@Override
	protected void start() {
		sceneGraph.scene.startSignal.addListener(this);
	}

	@Override
	protected void stop() {
		sceneGraph.scene.startSignal.removeListener(this);
	}

	@Override
	public void handle() {
		for (BehaviourComponent behaviourComponent : eventManager.getListeners(onSceneStart)) {
			behaviourComponent.onSceneStart();
		}
	}
}
