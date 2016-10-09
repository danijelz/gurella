package com.gurella.engine.subscriptions.scene.bullet;

import com.gurella.engine.scene.bullet.Collision;
import com.gurella.engine.scene.bullet.rigidbody.BulletRigidBodyComponent;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface BulletCollisionListener extends NodeEventSubscription {
	void onCollisionEnter(Collision collision);

	void onCollisionStay(Collision collision);

	void onCollisionExit(BulletRigidBodyComponent rigidBodyComponent);
}
