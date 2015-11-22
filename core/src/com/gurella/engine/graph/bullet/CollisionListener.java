package com.gurella.engine.graph.bullet;

public interface CollisionListener {
	void onCollisionEnter(Collision collision);

	void onCollisionStay(Collision collision);

	void onCollisionExit(BulletPhysicsRigidBodyComponent colidedWith);
}
