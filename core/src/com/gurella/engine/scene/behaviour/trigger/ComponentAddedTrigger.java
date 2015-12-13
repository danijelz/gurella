package com.gurella.engine.scene.behaviour.trigger;

import static com.gurella.engine.scene.behaviour.BehaviourEvents.componentAdded;

import com.gurella.engine.event.Listener1;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.behaviour.BehaviourComponent;
import com.gurella.engine.scene.event.EventTrigger;

public class ComponentAddedTrigger extends EventTrigger implements Listener1<SceneNodeComponent> {
	@Override
	protected void start() {
		sceneGraph.componentAddedSignal.addListener(this);
	}

	@Override
	protected void stop() {
		sceneGraph.componentAddedSignal.removeListener(this);
	}

	@Override
	public void handle(SceneNodeComponent component) {
		SceneNode node = component.getNode();
		for (BehaviourComponent behaviourComponent : eventManager.getListeners(componentAdded)) {
			behaviourComponent.componentAdded(node, component);
		}
	}
}
