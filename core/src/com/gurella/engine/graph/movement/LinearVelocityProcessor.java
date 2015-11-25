package com.gurella.engine.graph.movement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.application.UpdateOrder;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneProcessor;
import com.gurella.engine.graph.manager.SceneNodeManager;
import com.gurella.engine.graph.manager.SceneNodeManager.ComponentBitsNodeGroup;
import com.gurella.engine.graph.manager.SceneNodeManager.NodeGroup;
import com.gurella.engine.signal.Listener0;

public class LinearVelocityProcessor extends SceneProcessor {
	private SceneNodeManager nodeManager;
	@SuppressWarnings("unchecked")
	private NodeGroup nodeGroup = new ComponentBitsNodeGroup(TransformComponent.class, LinearVelocityComponent.class);
	private Vector3 tempTranslate = new Vector3();
	private Vector3 tempVelocity = new Vector3();
	
	public LinearVelocityProcessor() {
		activatedSignal.addListener(new ActivateListener());
		deactivatedSignal.addListener(new DeactivateListener());
	}

	@Override
	public void update() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		for(SceneNode node : nodeManager.getNodes(nodeGroup)) {
			TransformComponent transformComponent = node.getComponent(TransformComponent.class);
			LinearVelocityComponent linearVelocityComponent = node.getComponent(LinearVelocityComponent.class);
			transformComponent.getTranslation(tempTranslate);
			tempVelocity.set(tempTranslate).sub(linearVelocityComponent.lastPosition);
			linearVelocityComponent.velocity.set(tempVelocity.x / deltaTime, tempVelocity.y / deltaTime, tempVelocity.z / deltaTime);
			linearVelocityComponent.lastPosition.set(tempTranslate);
		}
	}

	@Override
	public int getOrdinal() {
		return UpdateOrder.THINK;
	}
	
	private class ActivateListener implements Listener0 {
		@Override
		public void handle() {
			nodeManager = getGraph().nodeManager;
			nodeManager.registerNodeGroup(nodeGroup);
		}
	}
	
	private class DeactivateListener implements Listener0 {
		@Override
		public void handle() {
			nodeManager = null;
		}
	}
}
