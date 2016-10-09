package com.gurella.engine.scene.bullet;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.physics.bullet.dynamics.InternalTickCallback;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.bullet.rigidbody.BulletRigidBodyComponent;
import com.gurella.engine.subscriptions.scene.bullet.BulletCollisionListener;
import com.gurella.engine.subscriptions.scene.bullet.BulletCollisionPairListener;
import com.gurella.engine.subscriptions.scene.bullet.BulletSimulationStepListener;

class CollisionTrackingCallback extends InternalTickCallback {
	private final Scene scene;

	private final Collision collision0 = new Collision();
	private final Collision collision1 = new Collision();
	private final CollisionPair collisionPair = new CollisionPair();

	private ObjectSet<CachedCollisionPair> previousTickCollisionPairs = new ObjectSet<CachedCollisionPair>();
	private ObjectSet<CachedCollisionPair> currentTickCollisionPairs = new ObjectSet<CachedCollisionPair>();

	private final SimulationStepEvent simulationStepEvent = new SimulationStepEvent();

	private final CollisionEnterEvent collisionEnterEvent = new CollisionEnterEvent();
	private final CollisionPairEnterEvent collisionPairEnterEvent = new CollisionPairEnterEvent();
	private final CollisionStayEvent collisionStayEvent = new CollisionStayEvent();
	private final CollisionPairStayEvent collisionPairStayEvent = new CollisionPairStayEvent();
	private final CollisionExitEvent collisionExitEvent = new CollisionExitEvent();
	private final CollisionPairExitEvent collisionPairExitEvent = new CollisionPairExitEvent();

	CollisionTrackingCallback(Scene scene) {
		this.scene = scene;
	}

	@Override
	public void onInternalTick(btDynamicsWorld dynamicsWorld, float timeStep) {
		simulationStepEvent.dynamicsWorld = dynamicsWorld;
		simulationStepEvent.timeStep = timeStep;
		EventService.post(scene.getInstanceId(), simulationStepEvent);
		simulationStepEvent.dynamicsWorld = null;

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
			collisionEnterEvent.collision = collision0;
			EventService.post(rigidBodyComponent0.getNodeId(), collisionEnterEvent);
			collisionEnterEvent.collision = collision1;
			EventService.post(rigidBodyComponent1.getNodeId(), collisionEnterEvent);
			collisionEnterEvent.collision = null;
			EventService.post(scene.getInstanceId(), collisionPairEnterEvent);
		} else {
			collisionStayEvent.collision = collision0;
			EventService.post(rigidBodyComponent0.getNodeId(), collisionStayEvent);
			collisionStayEvent.collision = collision1;
			EventService.post(rigidBodyComponent1.getNodeId(), collisionStayEvent);
			collisionStayEvent.collision = null;
			EventService.post(scene.getInstanceId(), collisionPairStayEvent);
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

		collisionExitEvent.rigidBodyComponent = rigidBodyComponent1;
		EventService.post(rigidBodyComponent0.getNodeId(), collisionExitEvent);
		collisionExitEvent.rigidBodyComponent = rigidBodyComponent0;
		EventService.post(rigidBodyComponent1.getNodeId(), collisionExitEvent);
		collisionExitEvent.rigidBodyComponent = null;

		collisionPairExitEvent.rigidBodyComponent0 = rigidBodyComponent0;
		collisionPairExitEvent.rigidBodyComponent1 = rigidBodyComponent1;
		EventService.post(scene.getInstanceId(), collisionPairExitEvent);
		collisionPairExitEvent.rigidBodyComponent0 = null;
		collisionPairExitEvent.rigidBodyComponent1 = null;
	}

	private void swapCollisionPairs() {
		ObjectSet<CachedCollisionPair> temp = previousTickCollisionPairs;
		previousTickCollisionPairs = currentTickCollisionPairs;
		currentTickCollisionPairs = temp;
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

	private static class SimulationStepEvent implements Event<BulletSimulationStepListener> {
		btDynamicsWorld dynamicsWorld;
		float timeStep;

		@Override
		public Class<BulletSimulationStepListener> getSubscriptionType() {
			return BulletSimulationStepListener.class;
		}

		@Override
		public void dispatch(BulletSimulationStepListener subscriber) {
			subscriber.onPhysicsSimulationStep(dynamicsWorld, timeStep);
		}
	}

	private static class CollisionEnterEvent implements Event<BulletCollisionListener> {
		Collision collision;

		@Override
		public Class<BulletCollisionListener> getSubscriptionType() {
			return BulletCollisionListener.class;
		}

		@Override
		public void dispatch(BulletCollisionListener subscriber) {
			subscriber.onCollisionEnter(collision);
		}
	}

	private class CollisionPairEnterEvent implements Event<BulletCollisionPairListener> {
		@Override
		public Class<BulletCollisionPairListener> getSubscriptionType() {
			return BulletCollisionPairListener.class;
		}

		@Override
		public void dispatch(BulletCollisionPairListener subscriber) {
			subscriber.onCollisionEnter(collisionPair);
		}
	}

	private static class CollisionStayEvent implements Event<BulletCollisionListener> {
		Collision collision;

		@Override
		public Class<BulletCollisionListener> getSubscriptionType() {
			return BulletCollisionListener.class;
		}

		@Override
		public void dispatch(BulletCollisionListener subscriber) {
			subscriber.onCollisionStay(collision);
		}
	}

	private class CollisionPairStayEvent implements Event<BulletCollisionPairListener> {
		@Override
		public Class<BulletCollisionPairListener> getSubscriptionType() {
			return BulletCollisionPairListener.class;
		}

		@Override
		public void dispatch(BulletCollisionPairListener subscriber) {
			subscriber.onCollisionStay(collisionPair);
		}
	}

	private static class CollisionExitEvent implements Event<BulletCollisionListener> {
		BulletRigidBodyComponent rigidBodyComponent;

		@Override
		public Class<BulletCollisionListener> getSubscriptionType() {
			return BulletCollisionListener.class;
		}

		@Override
		public void dispatch(BulletCollisionListener subscriber) {
			subscriber.onCollisionExit(rigidBodyComponent);
		}
	}

	private static class CollisionPairExitEvent implements Event<BulletCollisionPairListener> {
		BulletRigidBodyComponent rigidBodyComponent0;
		BulletRigidBodyComponent rigidBodyComponent1;

		@Override
		public Class<BulletCollisionPairListener> getSubscriptionType() {
			return BulletCollisionPairListener.class;
		}

		@Override
		public void dispatch(BulletCollisionPairListener subscriber) {
			subscriber.onCollisionExit(rigidBodyComponent0, rigidBodyComponent1);
		}
	}
}
