package com.gurella.engine.scene.bullet;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.physics.bullet.dynamics.InternalTickCallback;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.behaviour.BehaviourComponent;
import com.gurella.engine.scene.behaviour.BehaviourEvents;
import com.gurella.engine.scene.event.EventCallbackIdentifier;
import com.gurella.engine.utils.ImmutableArray;

class CollisionTrackingInternalTickCallback extends InternalTickCallback {
	private ObjectSet<CachedCollisionPair> previousTickCollisionPairs = new ObjectSet<CachedCollisionPair>();
	private ObjectSet<CachedCollisionPair> currentTickCollisionPairs = new ObjectSet<CachedCollisionPair>();

	Scene scene;

	@Override
	public void onInternalTick(btDynamicsWorld dynamicsWorld, float timeStep) {
		updateCurrentCollisions(dynamicsWorld, timeStep);
		clearPreviousCollisions();
		swapCollisionPairs();
		fireSimulationStepEvent(dynamicsWorld, timeStep);
	}

	private void updateCurrentCollisions(btDynamicsWorld dynamicsWorld, float timeStep) {
		int numManifolds = dynamicsWorld.getDispatcher().getNumManifolds();
		for (int i = 0; i < numManifolds; i++) {
			btPersistentManifold contactManifold = dynamicsWorld.getDispatcher().getManifoldByIndexInternal(i);
			if (hasCollisions(contactManifold)) {
				handleContactManifold(contactManifold, timeStep);
			}
		}
	}

	private static boolean hasCollisions(btPersistentManifold contactManifold) {
		int numContacts = contactManifold.getNumContacts();
		for (int j = 0; j < numContacts; j++) {
			btManifoldPoint pt = contactManifold.getContactPoint(j);
			if (pt.getDistance() <= 0) {
				return true;
			}
		}

		return false;
	}

	private void handleContactManifold(btPersistentManifold contactManifold, float timeStep) {
		btCollisionObject collisionObject0 = contactManifold.getBody0();
		BulletPhysicsRigidBodyComponent rigidBodyComponent0 = (BulletPhysicsRigidBodyComponent) collisionObject0.userData;
		Collision collision1 = Collision.obtain(contactManifold, collisionObject0, timeStep);

		btCollisionObject collisionObject1 = contactManifold.getBody1();
		BulletPhysicsRigidBodyComponent rigidBodyComponent1 = (BulletPhysicsRigidBodyComponent) collisionObject1.userData;
		Collision collision0 = Collision.obtain(contactManifold, collisionObject1, timeStep);

		CollisionPair collisionPair = CollisionPair.obtain(contactManifold, collisionObject0, collisionObject1,
				timeStep);

		CachedCollisionPair cachedCollisionPair = CachedCollisionPair.obtain(rigidBodyComponent0, rigidBodyComponent1);
		currentTickCollisionPairs.add(cachedCollisionPair);

		if (previousTickCollisionPairs.contains(cachedCollisionPair)) {
			for (BehaviourComponent behaviourComponent : getNodeScripts(rigidBodyComponent0,
					BehaviourEvents.onCollisionStay)) {
				behaviourComponent.onCollisionStay(collision0);
			}

			for (BehaviourComponent behaviourComponent : getNodeScripts(rigidBodyComponent1,
					BehaviourEvents.onCollisionStay)) {
				behaviourComponent.onCollisionStay(collision1);
			}

			for (BehaviourComponent behaviourComponent : getScripts(BehaviourEvents.onCollisionStayGlobal)) {
				behaviourComponent.onCollisionStay(collisionPair);
			}
		} else {
			for (BehaviourComponent behaviourComponent : getNodeScripts(rigidBodyComponent0,
					BehaviourEvents.onCollisionEnter)) {
				behaviourComponent.onCollisionEnter(collision0);
			}

			for (BehaviourComponent behaviourComponent : getNodeScripts(rigidBodyComponent1,
					BehaviourEvents.onCollisionEnter)) {
				behaviourComponent.onCollisionEnter(collision1);
			}

			for (BehaviourComponent behaviourComponent : getScripts(BehaviourEvents.onCollisionEnterGlobal)) {
				behaviourComponent.onCollisionEnter(collisionPair);
			}
		}

		collisionPair.free();
		collision0.free();
		collision1.free();
	}

	private void clearPreviousCollisions() {
		for (CachedCollisionPair cachedCollisionPair : previousTickCollisionPairs) {
			if (!currentTickCollisionPairs.contains(cachedCollisionPair)) {
				fireCollisionExitEvent(cachedCollisionPair);
			}
			cachedCollisionPair.free();
		}
		previousTickCollisionPairs.clear();
	}

	private void fireCollisionExitEvent(CachedCollisionPair cachedCollisionPair) {
		BulletPhysicsRigidBodyComponent rigidBodyComponent0 = cachedCollisionPair.rigidBodyComponent0;
		BulletPhysicsRigidBodyComponent rigidBodyComponent1 = cachedCollisionPair.rigidBodyComponent1;

		for (BehaviourComponent behaviourComponent : getNodeScripts(rigidBodyComponent0,
				BehaviourEvents.onCollisionExit)) {
			behaviourComponent.onCollisionExit(rigidBodyComponent1);
		}

		for (BehaviourComponent behaviourComponent : getNodeScripts(rigidBodyComponent1,
				BehaviourEvents.onCollisionExit)) {
			behaviourComponent.onCollisionExit(rigidBodyComponent0);
		}

		for (BehaviourComponent behaviourComponent : getScripts(BehaviourEvents.onCollisionExitGlobal)) {
			behaviourComponent.onCollisionExit(rigidBodyComponent0, rigidBodyComponent1);
		}
	}

	private ImmutableArray<BehaviourComponent> getNodeScripts(BulletPhysicsRigidBodyComponent rigidBodyComponent,
			EventCallbackIdentifier<BehaviourComponent> scriptMethod) {
		return scene.eventManager.getListeners(rigidBodyComponent.getNode(), scriptMethod);
	}

	private void swapCollisionPairs() {
		ObjectSet<CachedCollisionPair> temp = previousTickCollisionPairs;
		previousTickCollisionPairs = currentTickCollisionPairs;
		currentTickCollisionPairs = temp;
	}

	private void fireSimulationStepEvent(btDynamicsWorld dynamicsWorld, float timeStep) {
		for (BehaviourComponent behaviourComponent : getScripts(BehaviourEvents.onPhysicsSimulationStep)) {
			behaviourComponent.onPhysicsSimulationStep(dynamicsWorld, timeStep);
		}
	}

	private ImmutableArray<BehaviourComponent> getScripts(EventCallbackIdentifier<BehaviourComponent> method) {
		return scene.eventManager.getListeners(method);
	}

	void clear() {
		for (CachedCollisionPair cachedCollisionPair : previousTickCollisionPairs) {
			cachedCollisionPair.free();
		}
		previousTickCollisionPairs.clear();

		for (CachedCollisionPair cachedCollisionPair : currentTickCollisionPairs) {
			cachedCollisionPair.free();
		}
		currentTickCollisionPairs.clear();
		scene = null;
	}
}
