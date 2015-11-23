package com.gurella.engine.graph.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectArray;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.gurella.engine.application.Application;
import com.gurella.engine.application.UpdateOrder;
import com.gurella.engine.graph.SceneGraph;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.SceneProcessorManager;
import com.gurella.engine.graph.script.ScriptComponent;
import com.gurella.engine.graph.script.DefaultScriptMethod;

public class BulletPhysicsProcessor extends SceneProcessorManager {
	private btCollisionConfiguration collisionConfig;
	private btDispatcher dispatcher;
	private btBroadphaseInterface broadphase;
	private btConstraintSolver constraintSolver;

	btDynamicsWorld dynamicsWorld;

	private CollisionTrackingInternalTickCallback tickCallback;

	private Vector3 gravity = new Vector3(0, -10f, 0);

	public BulletPhysicsProcessor() {
		collisionConfig = Application.DISPOSABLE_MANAGER.add(new btDefaultCollisionConfiguration());
		dispatcher = Application.DISPOSABLE_MANAGER.add(new btCollisionDispatcher(collisionConfig));
		broadphase = Application.DISPOSABLE_MANAGER.add(new btDbvtBroadphase());
		constraintSolver = Application.DISPOSABLE_MANAGER.add(new btSequentialImpulseConstraintSolver());

		dynamicsWorld = Application.DISPOSABLE_MANAGER
				.add(new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig));
		dynamicsWorld.setGravity(gravity);

		tickCallback = Application.DISPOSABLE_MANAGER.add(new CollisionTrackingInternalTickCallback());
		tickCallback.attach(dynamicsWorld, false);
	}

	static {
		Bullet.init();
	}

	@Override
	protected void attached() {
		tickCallback.graph = getGraph();
		SceneGraph graph = getGraph();
		for (SceneNodeComponent component : graph.activeComponents) {
			componentActivated(component);
		}
	}

	@Override
	protected void detached() {
		int numCollisionObjects = dynamicsWorld.getNumCollisionObjects();
		btCollisionObjectArray collisionObjectArray = dynamicsWorld.getCollisionObjectArray();

		for (int i = 0; i < numCollisionObjects; i++) {
			dynamicsWorld.removeCollisionObject(collisionObjectArray.at(i));
		}

		tickCallback.clear();
	}

	@Override
	public int getOrdinal() {
		return UpdateOrder.PHYSICS;
	}

	@Override
	public void update() {
		dispatchSimulationStartEvent();
		dynamicsWorld.stepSimulation(Gdx.graphics.getDeltaTime(), 5, 1f / 60f);
		dispatchSimulationEndEvent();
	}

	private void dispatchSimulationStartEvent() {
		SceneGraph graph = getGraph();
		for (ScriptComponent scriptComponent : graph.scriptManager
				.getScriptComponents(DefaultScriptMethod.onPhysicsSimulationStart)) {
			scriptComponent.onPhysicsSimulationStart(dynamicsWorld);
		}
	}

	private void dispatchSimulationEndEvent() {
		SceneGraph graph = getGraph();
		for (ScriptComponent scriptComponent : graph.scriptManager
				.getScriptComponents(DefaultScriptMethod.onPhysicsSimulationEnd)) {
			scriptComponent.onPhysicsSimulationEnd(dynamicsWorld);
		}
	}

	@Override
	protected void resetted() {
		super.resetted();
		dynamicsWorld.clearForces();
		tickCallback.clear();
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		if (component instanceof BulletPhysicsRigidBodyComponent) {
			BulletPhysicsRigidBodyComponent rigidBodyComponent = (BulletPhysicsRigidBodyComponent) component;
			dynamicsWorld.addRigidBody(rigidBodyComponent.rigidBody);
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		if (component instanceof BulletPhysicsRigidBodyComponent) {
			BulletPhysicsRigidBodyComponent rigidBodyComponent = (BulletPhysicsRigidBodyComponent) component;
			dynamicsWorld.removeRigidBody(rigidBodyComponent.rigidBody);
		}
	}
}
