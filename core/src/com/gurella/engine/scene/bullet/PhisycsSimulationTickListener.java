package com.gurella.engine.scene.bullet;

import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

//TODO unused
public interface PhisycsSimulationTickListener extends SceneEventSubscription {
	void physicsSimulationStart();

	void physicsSimulationEnd();
}
