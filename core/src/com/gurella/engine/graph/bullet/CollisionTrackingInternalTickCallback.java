package com.gurella.engine.graph.bullet;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.physics.bullet.dynamics.InternalTickCallback;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.OrderedSet;
import com.gurella.engine.graph.SceneGraph;
import com.gurella.engine.graph.script.DefaultScriptMethod;
import com.gurella.engine.graph.script.ScriptComponent;
import com.gurella.engine.graph.script.ScriptMethodDescriptor;

class CollisionTrackingInternalTickCallback extends InternalTickCallback {
	private ObjectSet<CachedCollisionPair> previousTickCollisionPairs = new ObjectSet<CachedCollisionPair>();
	private ObjectSet<CachedCollisionPair> currentTickCollisionPairs = new ObjectSet<CachedCollisionPair>();

	SceneGraph graph;

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
			rigidBodyComponent0.collisionSignal.onCollisionStay(collision0);
			for (ScriptComponent scriptComponent : getNodeScripts(rigidBodyComponent0,
					DefaultScriptMethod.onCollisionStay)) {
				scriptComponent.onCollisionStay(collision0);
			}

			rigidBodyComponent1.collisionSignal.onCollisionStay(collision1);
			for (ScriptComponent scriptComponent : getNodeScripts(rigidBodyComponent1,
					DefaultScriptMethod.onCollisionStay)) {
				scriptComponent.onCollisionStay(collision1);
			}

			for (ScriptComponent scriptComponent : getScripts(DefaultScriptMethod.onCollisionStayGlobal)) {
				scriptComponent.onCollisionStay(collisionPair);
			}
		} else {
			rigidBodyComponent0.collisionSignal.onCollisionEnter(collision0);
			for (ScriptComponent scriptComponent : getNodeScripts(rigidBodyComponent0,
					DefaultScriptMethod.onCollisionEnter)) {
				scriptComponent.onCollisionEnter(collision0);
			}

			rigidBodyComponent1.collisionSignal.onCollisionEnter(collision1);
			for (ScriptComponent scriptComponent : getNodeScripts(rigidBodyComponent1,
					DefaultScriptMethod.onCollisionEnter)) {
				scriptComponent.onCollisionEnter(collision1);
			}

			for (ScriptComponent scriptComponent : getScripts(DefaultScriptMethod.onCollisionEnterGlobal)) {
				scriptComponent.onCollisionEnter(collisionPair);
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

		rigidBodyComponent0.collisionSignal.onCollisionExit(rigidBodyComponent1);
		for (ScriptComponent scriptComponent : getNodeScripts(rigidBodyComponent0,
				DefaultScriptMethod.onCollisionExit)) {
			scriptComponent.onCollisionExit(rigidBodyComponent1);
		}

		rigidBodyComponent1.collisionSignal.onCollisionExit(rigidBodyComponent0);
		for (ScriptComponent scriptComponent : getNodeScripts(rigidBodyComponent1,
				DefaultScriptMethod.onCollisionExit)) {
			scriptComponent.onCollisionExit(rigidBodyComponent0);
		}

		for (ScriptComponent scriptComponent : getScripts(DefaultScriptMethod.onCollisionExitGlobal)) {
			scriptComponent.onCollisionExit(rigidBodyComponent0, rigidBodyComponent1);
		}
	}

	private OrderedSet<ScriptComponent> getNodeScripts(BulletPhysicsRigidBodyComponent rigidBodyComponent,
			ScriptMethodDescriptor scriptMethod) {
		return graph.scriptManager.getNodeScriptsByMethod(rigidBodyComponent.getNode(), scriptMethod);
	}

	private void swapCollisionPairs() {
		ObjectSet<CachedCollisionPair> temp = previousTickCollisionPairs;
		previousTickCollisionPairs = currentTickCollisionPairs;
		currentTickCollisionPairs = temp;
	}

	private void fireSimulationStepEvent(btDynamicsWorld dynamicsWorld, float timeStep) {
		for (ScriptComponent scriptComponent : getScripts(DefaultScriptMethod.onPhysicsSimulationStep)) {
			scriptComponent.onPhysicsSimulationStep(dynamicsWorld, timeStep);
		}
	}

	private OrderedSet<ScriptComponent> getScripts(ScriptMethodDescriptor method) {
		return graph.scriptManager.getScriptComponents(method);
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
		graph = null;
	}
}
