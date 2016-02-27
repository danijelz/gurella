package com.gurella.engine.subscriptions.scene.bullet;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.bullet.BulletPhysicsRigidBodyComponent;
import com.gurella.engine.scene.bullet.Collision;

public interface CollisionListener extends EventSubscription {
	void onCollisionEnter(Collision collision);

	void onCollisionStay(Collision collision);

	void onCollisionExit(BulletPhysicsRigidBodyComponent rigidBodyComponent);
}
