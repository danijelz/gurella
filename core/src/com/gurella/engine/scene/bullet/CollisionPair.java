package com.gurella.engine.scene.bullet;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.bullet.rigidbody.BulletRigidBodyComponent;

public class CollisionPair {
	private btPersistentManifold contactManifold;

	private btCollisionObject collisionObject0;
	private BulletRigidBodyComponent component0;
	private SceneNode node0;

	private btCollisionObject collisionObject1;
	private BulletRigidBodyComponent component1;
	private SceneNode node1;

	private float timeStep = -1;

	CollisionPair() {
	}

	void init(btPersistentManifold contactManifold, btCollisionObject collisionObject0,
			btCollisionObject collisionObject1, float timeStep) {
		this.contactManifold = contactManifold;
		this.collisionObject0 = collisionObject0;
		this.component0 = (BulletRigidBodyComponent) collisionObject0.userData;
		this.node0 = this.component0.getNode();
		this.collisionObject1 = collisionObject1;
		this.component1 = (BulletRigidBodyComponent) collisionObject1.userData;
		this.node1 = this.component1.getNode();
		this.timeStep = timeStep;
	}

	void reset() {
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

	public BulletRigidBodyComponent getComponent0() {
		return component0;
	}

	public SceneNode getNode0() {
		return node0;
	}

	public btCollisionObject getCollisionObject1() {
		return collisionObject1;
	}

	public BulletRigidBodyComponent getComponent1() {
		return component1;
	}

	public SceneNode getNode1() {
		return node1;
	}

	public float getTimeStep() {
		return timeStep;
	}
}
