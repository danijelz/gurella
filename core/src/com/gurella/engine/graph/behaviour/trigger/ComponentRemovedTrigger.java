package com.gurella.engine.graph.behaviour.trigger;

import static com.gurella.engine.graph.behaviour.BehaviourEvents.componentRemoved;

import com.gurella.engine.event.Listener1;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.graph.event.EventTrigger;

public class ComponentRemovedTrigger extends EventTrigger implements Listener1<SceneNodeComponent> {
	@Override
	protected void activated() {
		eventManager.getGraph().componentRemovedSignal.addListener(this);
	}

	@Override
	protected void deactivated() {
		eventManager.getGraph().componentRemovedSignal.removeListener(this);
	}

	@Override
	public void handle(SceneNodeComponent component) {
		SceneNode node = component.getNode();
		for (BehaviourComponent behaviourComponent : eventManager.getListeners(componentRemoved)) {
			behaviourComponent.componentRemoved(node, component);
		}
	}
}
