package com.gurella.engine.graph.behaviour;

import static com.gurella.engine.graph.behaviour.BehaviourEvents.nodeComponentDeactivated;

import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.event.EventTrigger;
import com.gurella.engine.signal.Listener1;

public class NodeComponentDeactivatedTrigger extends EventTrigger implements Listener1<SceneNodeComponent> {
	@Override
	protected void activated() {
		eventSystem.getGraph().componentDeactivatedSignal.addListener(this);
	}

	@Override
	protected void deactivated() {
		eventSystem.getGraph().componentDeactivatedSignal.removeListener(this);
	}

	@Override
	public void handle(SceneNodeComponent component) {
		SceneNode node = component.getNode();
		for (BehaviourComponent behaviourComponent : eventSystem.getListeners(node, nodeComponentDeactivated)) {
			behaviourComponent.nodeComponentDeactivated(component);
		}
	}
}
