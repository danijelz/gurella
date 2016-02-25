package com.gurella.engine.scene.bullet;

import com.gurella.engine.event.EventSubscription;

//TODO unused
public interface PhisycsSimulationTickListener extends EventSubscription {
	void physicsSimulationStart();

	void physicsSimulationEnd();
}
