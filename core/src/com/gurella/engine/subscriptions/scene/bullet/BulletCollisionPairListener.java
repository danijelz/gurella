package com.gurella.engine.subscriptions.scene.bullet;

import com.gurella.engine.scene.bullet.BulletPhysicsRigidBodyComponent;
import com.gurella.engine.scene.bullet.CollisionPair;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface BulletCollisionPairListener extends SceneEventSubscription {
	void onCollisionEnter(CollisionPair collision);

	public void onCollisionStay(CollisionPair collision);

	public void onCollisionExit(BulletPhysicsRigidBodyComponent rigidBody1, BulletPhysicsRigidBodyComponent rigidBody2);
}
