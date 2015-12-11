package com.gurella.engine.scene.behaviour.trigger;

import static com.gurella.engine.scene.behaviour.BehaviourEvents.componentDeactivated;

import com.gurella.engine.event.Listener1;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.behaviour.BehaviourComponent;
import com.gurella.engine.scene.event.EventTrigger;

public class ComponentDeactivatedTrigger extends EventTrigger implements Listener1<SceneNodeComponent> {
	@Override
	protected void activated() {
		sceneGraph.componentDeactivatedSignal.addListener(this);
	}

	@Override
	protected void deactivated() {
		sceneGraph.componentDeactivatedSignal.removeListener(this);
	}

	@Override
	public void handle(SceneNodeComponent component) {
		SceneNode node = component.getNode();
		for (BehaviourComponent behaviourComponent : eventManager.getListeners(componentDeactivated)) {
			behaviourComponent.componentDeactivated(node, component);
		}
	}
}
