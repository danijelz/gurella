package com.gurella.engine.scene.bullet;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.utils.SynchronizedPools;

public class Collision implements Poolable {
	private btPersistentManifold contactManifold;
	private btCollisionObject collidedWithCollisionObject;
	private BulletPhysicsRigidBodyComponent collidedWithComponent;
	private SceneNode collidedWithNode;
	private float timeStep = -1;

	private Collision() {
	}

	static Collision obtain(btPersistentManifold contactManifold, btCollisionObject collidedWithCollisionObject,
			float timeStep) {
		Collision collision = SynchronizedPools.obtain(Collision.class);
		collision.contactManifold = contactManifold;
		collision.collidedWithCollisionObject = collidedWithCollisionObject;
		collision.collidedWithComponent = (BulletPhysicsRigidBodyComponent) collidedWithCollisionObject.userData;
		collision.collidedWithNode = collision.collidedWithComponent.getNode();
		collision.timeStep = timeStep;
		return collision;
	}

	void free() {
		SynchronizedPools.free(this);
	}

	@Override
	public void reset() {
		contactManifold = null;
		collidedWithCollisionObject = null;
		collidedWithComponent = null;
		collidedWithNode = null;
		timeStep = -1;
	}

	public btPersistentManifold getContactManifold() {
		return contactManifold;
	}

	public btCollisionObject getCollidedWithCollisionObject() {
		return collidedWithCollisionObject;
	}

	public BulletPhysicsRigidBodyComponent getCollidedWithComponent() {
		return collidedWithComponent;
	}

	public SceneNode getCollidedWithNode() {
		return collidedWithNode;
	}

	public float getTimeStep() {
		return timeStep;
	}
}
