package com.gurella.engine.graph.behaviour.trigger;

import static com.gurella.engine.graph.behaviour.BehaviourEvents.nodeComponentActivated;

import com.gurella.engine.event.Listener1;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.graph.event.EventTrigger;

public class NodeComponentActivatedTrigger extends EventTrigger implements Listener1<SceneNodeComponent> {
	@Override
	protected void activated() {
		sceneGraph.componentActivatedSignal.addListener(this);
	}

	@Override
	protected void deactivated() {
		sceneGraph.componentActivatedSignal.removeListener(this);
	}

	@Override
	public void handle(SceneNodeComponent component) {
		SceneNode node = component.getNode();
		for (BehaviourComponent behaviourComponent : eventManager.getListeners(node, nodeComponentActivated)) {
			behaviourComponent.nodeComponentActivated(component);
		}
	}
}
