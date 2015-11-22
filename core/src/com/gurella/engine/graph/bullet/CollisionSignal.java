package com.gurella.engine.graph.bullet;

import com.gurella.engine.signal.AbstractSignal;

public class CollisionSignal extends AbstractSignal<CollisionListener> {
	BulletPhysicsRigidBodyComponent bulletRigidBodyComponent;

	CollisionSignal(BulletPhysicsRigidBodyComponent bulletRigidBodyComponent) {
		this.bulletRigidBodyComponent = bulletRigidBodyComponent;
	}

	void onCollisionEnter(Collision collision) {
		bulletRigidBodyComponent.collisionEnterSignal.dispatch(collision);
		for (CollisionListener listener : listeners) {
			listener.onCollisionEnter(collision);
		}
	}

	void onCollisionStay(Collision collision) {
		bulletRigidBodyComponent.collisionStaySignal.dispatch(collision);
		for (CollisionListener listener : listeners) {
			listener.onCollisionStay(collision);
		}
	}

	void onCollisionExit(BulletPhysicsRigidBodyComponent colidedWith) {
		bulletRigidBodyComponent.collisionExitSignal.dispatch(colidedWith);
		for (CollisionListener listener : listeners) {
			listener.onCollisionExit(colidedWith);
		}
	}
}
