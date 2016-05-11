package com.gurella.engine.scene.bullet;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.gurella.engine.scene.SceneNode2;

public class Collision {
	private btPersistentManifold contactManifold;
	private btCollisionObject collidedWithCollisionObject;
	private BulletRigidBodyComponent collidedWithComponent;
	private SceneNode2 collidedWithNode;
	private float timeStep = -1;

	Collision() {
	}

	void init(btPersistentManifold manifold, btCollisionObject collidedWith, float timeStep) {
		this.contactManifold = manifold;
		this.collidedWithCollisionObject = collidedWith;
		this.collidedWithComponent = (BulletRigidBodyComponent) collidedWith.userData;
		this.collidedWithNode = this.collidedWithComponent.getNode();
		this.timeStep = timeStep;
	}

	void reset() {
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

	public BulletRigidBodyComponent getCollidedWithComponent() {
		return collidedWithComponent;
	}

	public SceneNode2 getCollidedWithNode() {
		return collidedWithNode;
	}

	public float getTimeStep() {
		return timeStep;
	}
}
