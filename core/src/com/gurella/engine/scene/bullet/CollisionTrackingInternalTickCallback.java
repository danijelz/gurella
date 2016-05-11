package com.gurella.engine.scene.bullet;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.physics.bullet.dynamics.InternalTickCallback;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.scene.bullet.BulletCollisionListener;
import com.gurella.engine.subscriptions.scene.bullet.BulletCollisionPairListener;
import com.gurella.engine.subscriptions.scene.bullet.BulletSimulationStepListener;
import com.gurella.engine.utils.Values;

class CollisionTrackingInternalTickCallback extends InternalTickCallback {
	private Array<Object> tempListeners;

	private Collision collision0 = new Collision();
	private Collision collision1 = new Collision();
	private CollisionPair collisionPair = new CollisionPair();

	private ObjectSet<CachedCollisionPair> previousTickCollisionPairs = new ObjectSet<CachedCollisionPair>();
	private ObjectSet<CachedCollisionPair> currentTickCollisionPairs = new ObjectSet<CachedCollisionPair>();

	CollisionTrackingInternalTickCallback(Array<Object> tempListeners) {
		this.tempListeners = tempListeners;
	}

	@Override
	public void onInternalTick(btDynamicsWorld dynamicsWorld, float timeStep) {
		fireSimulationStepEvent(dynamicsWorld, timeStep);
		updateCurrentCollisions(dynamicsWorld, timeStep);
		clearPreviousCollisions();
		swapCollisionPairs();
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
		BulletRigidBodyComponent rigidBodyComponent0 = (BulletRigidBodyComponent) collisionObject0.userData;
		collision1.init(contactManifold, collisionObject0, timeStep);

		btCollisionObject collisionObject1 = contactManifold.getBody1();
		BulletRigidBodyComponent rigidBodyComponent1 = (BulletRigidBodyComponent) collisionObject1.userData;
		collision0.init(contactManifold, collisionObject1, timeStep);

		collisionPair.init(contactManifold, collisionObject0, collisionObject1, timeStep);

		CachedCollisionPair cachedCollisionPair = CachedCollisionPair.obtain(rigidBodyComponent0, rigidBodyComponent1);
		currentTickCollisionPairs.add(cachedCollisionPair);

		if (previousTickCollisionPairs.contains(cachedCollisionPair)) {
			Array<BulletCollisionListener> listeners = Values.cast(tempListeners);
			EventService.getSubscribers(rigidBodyComponent0.getNodeId(), BulletCollisionListener.class, listeners);
			for (int i = 0; i < listeners.size; i++) {
				listeners.get(i).onCollisionStay(collision0);
			}

			listeners = Values.cast(tempListeners);
			EventService.getSubscribers(rigidBodyComponent1.getNodeId(), BulletCollisionListener.class, listeners);
			for (int i = 0; i < listeners.size; i++) {
				listeners.get(i).onCollisionStay(collision1);
			}

			Array<BulletCollisionPairListener> pairListeners = Values.cast(tempListeners);
			EventService.getSubscribers(BulletCollisionPairListener.class, pairListeners);
			for (int i = 0; i < pairListeners.size; i++) {
				pairListeners.get(i).onCollisionStay(collisionPair);
			}
		} else {
			Array<BulletCollisionListener> listeners = Values.cast(tempListeners);
			EventService.getSubscribers(rigidBodyComponent0.getNodeId(), BulletCollisionListener.class, listeners);
			for (int i = 0; i < listeners.size; i++) {
				listeners.get(i).onCollisionEnter(collision0);
			}

			listeners = Values.cast(tempListeners);
			EventService.getSubscribers(rigidBodyComponent1.getNodeId(), BulletCollisionListener.class, listeners);
			for (int i = 0; i < listeners.size; i++) {
				listeners.get(i).onCollisionEnter(collision1);
			}

			Array<BulletCollisionPairListener> pairListeners = Values.cast(tempListeners);
			EventService.getSubscribers(BulletCollisionPairListener.class, pairListeners);
			for (int i = 0; i < pairListeners.size; i++) {
				pairListeners.get(i).onCollisionEnter(collisionPair);
			}
		}

		collisionPair.reset();
		collision0.reset();
		collision1.reset();
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
		BulletRigidBodyComponent rigidBodyComponent0 = cachedCollisionPair.rigidBodyComponent0;
		BulletRigidBodyComponent rigidBodyComponent1 = cachedCollisionPair.rigidBodyComponent1;

		Array<BulletCollisionListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(rigidBodyComponent0.getNodeId(), BulletCollisionListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).onCollisionExit(rigidBodyComponent1);
		}

		listeners = Values.cast(tempListeners);
		EventService.getSubscribers(rigidBodyComponent1.getNodeId(), BulletCollisionListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).onCollisionExit(rigidBodyComponent0);
		}

		Array<BulletCollisionPairListener> pairListeners = Values.cast(tempListeners);
		EventService.getSubscribers(BulletCollisionPairListener.class, pairListeners);
		for (int i = 0; i < pairListeners.size; i++) {
			pairListeners.get(i).onCollisionExit(rigidBodyComponent0, rigidBodyComponent1);
		}
	}

	private void swapCollisionPairs() {
		ObjectSet<CachedCollisionPair> temp = previousTickCollisionPairs;
		previousTickCollisionPairs = currentTickCollisionPairs;
		currentTickCollisionPairs = temp;
	}

	private void fireSimulationStepEvent(btDynamicsWorld dynamicsWorld, float timeStep) {
		Array<BulletSimulationStepListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(BulletSimulationStepListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).onPhysicsSimulationStep(dynamicsWorld, timeStep);
		}
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
	}
}
