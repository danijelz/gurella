package com.gurella.engine.subscriptions.scene.bullet;

import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.gurella.engine.event.EventSubscription;

public interface BulletSimulationStepListener extends EventSubscription {
	//TODO wrap world in object that only exposes safe methods
	void onPhysicsSimulationStep(btDynamicsWorld dynamicsWorld, float timeStep);
}
