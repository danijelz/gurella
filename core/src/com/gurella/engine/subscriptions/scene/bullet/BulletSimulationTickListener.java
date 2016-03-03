package com.gurella.engine.subscriptions.scene.bullet;

import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface BulletSimulationTickListener extends SceneEventSubscription {
	//TODO wrap world in object that only exposes safe methods
	void onPhysicsSimulationStart(btDynamicsWorld world);

	void onPhysicsSimulationEnd(btDynamicsWorld world);
}
