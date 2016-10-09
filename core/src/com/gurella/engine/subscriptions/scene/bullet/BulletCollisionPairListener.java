package com.gurella.engine.subscriptions.scene.bullet;

import com.gurella.engine.scene.bullet.CollisionPair;
import com.gurella.engine.scene.bullet.rigidbody.BulletRigidBodyComponent;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface BulletCollisionPairListener extends SceneEventSubscription {
	void onCollisionEnter(CollisionPair collision);

	public void onCollisionStay(CollisionPair collision);

	public void onCollisionExit(BulletRigidBodyComponent rigidBody1, BulletRigidBodyComponent rigidBody2);
}
