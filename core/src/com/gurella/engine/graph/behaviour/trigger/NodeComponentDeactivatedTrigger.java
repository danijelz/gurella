package com.gurella.engine.graph.behaviour.trigger;

import static com.gurella.engine.graph.behaviour.BehaviourEvents.nodeComponentDeactivated;

import com.gurella.engine.event.Listener1;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.graph.event.EventTrigger;

public class NodeComponentDeactivatedTrigger extends EventTrigger implements Listener1<SceneNodeComponent> {
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
		for (BehaviourComponent behaviourComponent : eventManager.getListeners(node, nodeComponentDeactivated)) {
			behaviourComponent.nodeComponentDeactivated(component);
		}
	}
}
