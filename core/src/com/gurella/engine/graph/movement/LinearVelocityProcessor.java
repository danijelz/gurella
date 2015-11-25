package com.gurella.engine.graph.movement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.application.UpdateOrder;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneProcessor;
import com.gurella.engine.graph.manager.ComponentsPredicate;
import com.gurella.engine.graph.manager.SceneNodeManager;
import com.gurella.engine.graph.manager.SceneNodeManager.SceneNodeFamily;
import com.gurella.engine.signal.Listener0;

public class LinearVelocityProcessor extends SceneProcessor {
	private static final SceneNodeFamily family = new SceneNodeFamily(
			ComponentsPredicate.all(true, TransformComponent.class, LinearVelocityComponent.class).build());

	private SceneNodeManager nodeManager;
	private Vector3 tempTranslate = new Vector3();
	private Vector3 tempVelocity = new Vector3();

	public LinearVelocityProcessor() {
		activatedSignal.addListener(new ActivateListener());
		deactivatedSignal.addListener(new DeactivateListener());
	}

	@Override
	protected void activated() {
		nodeManager = getGraph().nodeManager;
		nodeManager.registerFamily(family);
	}

	@Override
	protected void deactivated() {
		nodeManager.unregisterFamily(family);
		nodeManager = null;
	}

	@Override
	public void update() {
		float deltaTime = Gdx.graphics.getDeltaTime();

		for (SceneNode node : nodeManager.getNodes(family)) {
			LinearVelocityComponent linearVelocityComponent = node.getComponent(LinearVelocityComponent.class);
			TransformComponent transformComponent = node.getComponent(TransformComponent.class);
			transformComponent.getTranslation(tempTranslate);

			if (linearVelocityComponent.lastPosition.x == Float.NaN) {
				tempVelocity.setZero();
			} else {

				tempVelocity.set(tempTranslate).sub(linearVelocityComponent.lastPosition);
			}

			linearVelocityComponent.velocity.set(tempVelocity.x / deltaTime, tempVelocity.y / deltaTime,
					tempVelocity.z / deltaTime);
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

		}
	}

	private class DeactivateListener implements Listener0 {
		@Override
		public void handle() {
			nodeManager = null;
		}
	}
}
