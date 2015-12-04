package com.gurella.engine.graph.bullet;

import static com.gurella.engine.graph.behaviour.BehaviourEventCallbacks.onPhysicsSimulationEnd;
import static com.gurella.engine.graph.behaviour.BehaviourEventCallbacks.onPhysicsSimulationStart;

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
import com.gurella.engine.application.CommonUpdateOrder;
import com.gurella.engine.graph.SceneGraph;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.SceneProcessorManager;
import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.graph.event.EventSystem;
import com.gurella.engine.utils.ImmutableArray;

public class BulletPhysicsProcessor extends SceneProcessorManager {
	private btCollisionConfiguration collisionConfig;
	private btDispatcher dispatcher;
	private btBroadphaseInterface broadphase;
	private btConstraintSolver constraintSolver;

	btDynamicsWorld dynamicsWorld;

	private CollisionTrackingInternalTickCallback tickCallback;

	private Vector3 gravity = new Vector3(0, -10f, 0);
	private EventSystem eventSystem;

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
	protected void activated() {
		SceneGraph graph = getGraph();
		tickCallback.graph = graph;
		eventSystem = graph.eventSystem;
		ImmutableArray<SceneNodeComponent> components = graph.activeComponents;
		for (int i = 0; i < components.size(); i++) {
			componentActivated(components.get(i));
		}
	}

	@Override
	protected void deactivated() {
		int numCollisionObjects = dynamicsWorld.getNumCollisionObjects();
		btCollisionObjectArray collisionObjectArray = dynamicsWorld.getCollisionObjectArray();

		for (int i = 0; i < numCollisionObjects; i++) {
			dynamicsWorld.removeCollisionObject(collisionObjectArray.at(i));
		}

		tickCallback.clear();
		eventSystem = null;
	}

	@Override
	public int getOrdinal() {
		return CommonUpdateOrder.PHYSICS;
	}

	@Override
	public void update() {
		dispatchSimulationStartEvent();
		dynamicsWorld.stepSimulation(Gdx.graphics.getDeltaTime(), 5, 1f / 60f);
		dispatchSimulationEndEvent();
	}

	private void dispatchSimulationStartEvent() {
		for (BehaviourComponent behaviourComponent : eventSystem.getListeners(onPhysicsSimulationStart)) {
			behaviourComponent.onPhysicsSimulationStart(dynamicsWorld);
		}
	}

	private void dispatchSimulationEndEvent() {
		for (BehaviourComponent behaviourComponent : eventSystem.getListeners(onPhysicsSimulationEnd)) {
			behaviourComponent.onPhysicsSimulationEnd(dynamicsWorld);
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
