package com.gurella.engine.scene.bullet;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.utils.SynchronizedPools;

//TODO unused
public class CollisionPair implements Poolable {
	private btPersistentManifold contactManifold;

	private btCollisionObject collisionObject0;
	private BulletPhysicsRigidBodyComponent component0;
	private SceneNode node0;

	private btCollisionObject collisionObject1;
	private BulletPhysicsRigidBodyComponent component1;
	private SceneNode node1;

	private float timeStep = -1;

	private CollisionPair() {
	}

	static CollisionPair obtain(btPersistentManifold contactManifold, btCollisionObject collisionObject0,
			btCollisionObject collisionObject1, float timeStep) {
		CollisionPair collision = SynchronizedPools.obtain(CollisionPair.class);

		collision.contactManifold = contactManifold;

		collision.collisionObject0 = collisionObject0;
		collision.component0 = (BulletPhysicsRigidBodyComponent) collisionObject0.userData;
		collision.node0 = collision.component0.getNode();

		collision.collisionObject1 = collisionObject1;
		collision.component1 = (BulletPhysicsRigidBodyComponent) collisionObject1.userData;
		collision.node1 = collision.component1.getNode();

		collision.timeStep = timeStep;
		return collision;
	}

	void free() {
		SynchronizedPools.free(this);
	}

	@Override
	public void reset() {
		contactManifold = null;
		collisionObject0 = null;
		component0 = null;
		node0 = null;
		collisionObject1 = null;
		component1 = null;
		node1 = null;
		timeStep = -1;
	}

	public btPersistentManifold getContactManifold() {
		return contactManifold;
	}

	public btCollisionObject getCollisionObject0() {
		return collisionObject0;
	}

	public BulletPhysicsRigidBodyComponent getComponent0() {
		return component0;
	}

	public SceneNode getNode0() {
		return node0;
	}

	public btCollisionObject getCollisionObject1() {
		return collisionObject1;
	}

	public BulletPhysicsRigidBodyComponent getComponent1() {
		return component1;
	}

	public SceneNode getNode1() {
		return node1;
	}

	public float getTimeStep() {
		return timeStep;
	}
}
