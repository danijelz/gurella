package com.gurella.engine.subscriptions.scene.bullet;

import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface BulletSimulationStepListener extends SceneEventSubscription {
	//TODO wrap world in object that only exposes safe methods
	void onPhysicsSimulationStep(btDynamicsWorld dynamicsWorld, float timeStep);
}
