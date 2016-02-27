package com.gurella.engine.subscriptions.scene.bullet;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.bullet.BulletPhysicsRigidBodyComponent;
import com.gurella.engine.scene.bullet.CollisionPair;

public interface BulletCollisionPairListener extends EventSubscription {
	void onCollisionEnter(CollisionPair collision);

	public void onCollisionStay(CollisionPair collision);

	public void onCollisionExit(BulletPhysicsRigidBodyComponent rigidBody1, BulletPhysicsRigidBodyComponent rigidBody2);
}
