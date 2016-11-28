package com.gurella.engine.scene.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectArray;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.gurella.engine.application.Application;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.SceneService;
import com.gurella.engine.scene.bullet.rigidbody.BulletRigidBodyComponent;
import com.gurella.engine.subscriptions.application.ApplicationActivityListener;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.subscriptions.scene.bullet.BulletSimulationTickListener;
import com.gurella.engine.subscriptions.scene.update.PhysicsUpdateListener;

//TODO add joints, force fields
public class BulletPhysicsSystem extends SceneService
		implements ComponentActivityListener, PhysicsUpdateListener, ApplicationActivityListener {
	static {
		Bullet.init();
	}

	private btCollisionConfiguration collisionConfig;
	private btDispatcher dispatcher;
	private btBroadphaseInterface broadphase;
	private btConstraintSolver constraintSolver;
	private btDynamicsWorld dynamicsWorld;
	private CollisionTrackingCallback tickCallback;

	public boolean stopSimulationOnPause;
	private boolean paused;
	private final Vector3 gravity = new Vector3(0f, -9.8f, 0f);

	private PhysicsSimulationStartEvent physicsSimulationStartEvent = new PhysicsSimulationStartEvent();
	private PhysicsSimulationEndEvent physicsSimulationEndEvent = new PhysicsSimulationEndEvent();

	public BulletPhysicsSystem(Scene scene) {
		super(scene);
		collisionConfig = DisposablesService.add(new btDefaultCollisionConfiguration());
		dispatcher = DisposablesService.add(new btCollisionDispatcher(collisionConfig));
		broadphase = DisposablesService.add(new btDbvtBroadphase());
		constraintSolver = DisposablesService.add(new btSequentialImpulseConstraintSolver());

		dynamicsWorld = DisposablesService
				.add(new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig));
		dynamicsWorld.setGravity(gravity);

		tickCallback = DisposablesService.add(new CollisionTrackingCallback(scene));
		tickCallback.attach(dynamicsWorld, false);
	}

	@Override
	protected void serviceActivated() {
		//paused = ((Application) Gdx.app.getApplicationListener()).isPaused();
		// TODO paused = Application.isPaused(); 
	}

	@Override
	protected void serviceDeactivated() {
		int numCollisionObjects = dynamicsWorld.getNumCollisionObjects();
		btCollisionObjectArray collisionObjectArray = dynamicsWorld.getCollisionObjectArray();

		for (int i = 0; i < numCollisionObjects; i++) {
			dynamicsWorld.removeCollisionObject(collisionObjectArray.at(i));
		}

		dynamicsWorld.clearForces();
		tickCallback.clear();
	}

	@Override
	public void onPhysicsUpdate() {
		if (paused && stopSimulationOnPause) {
			return;
		}

		EventService.post(scene.getInstanceId(), physicsSimulationStartEvent);
		dynamicsWorld.stepSimulation(Gdx.graphics.getDeltaTime(), 5, 1f / 60f);
		EventService.post(scene.getInstanceId(), physicsSimulationEndEvent);
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		if (component instanceof BulletRigidBodyComponent) {
			BulletRigidBodyComponent rigidBodyComponent = (BulletRigidBodyComponent) component;
			btCollisionObject collisionObject = rigidBodyComponent.collisionObject;
			if (collisionObject instanceof btRigidBody) {
				dynamicsWorld.addRigidBody((btRigidBody) collisionObject);
			} else {
				dynamicsWorld.addCollisionObject(collisionObject);
			}
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		if (component instanceof BulletRigidBodyComponent) {
			BulletRigidBodyComponent rigidBodyComponent = (BulletRigidBodyComponent) component;
			btCollisionObject collisionObject = rigidBodyComponent.collisionObject;
			if (collisionObject instanceof btRigidBody) {
				dynamicsWorld.removeRigidBody((btRigidBody) collisionObject);
			} else {
				dynamicsWorld.removeCollisionObject(collisionObject);
			}
		}
	}

	@Override
	public void pause() {
		paused = true;
	}

	@Override
	public void resume() {
		paused = false;
	}

	private class PhysicsSimulationStartEvent implements Event<BulletSimulationTickListener> {
		@Override
		public Class<BulletSimulationTickListener> getSubscriptionType() {
			return BulletSimulationTickListener.class;
		}

		@Override
		public void dispatch(BulletSimulationTickListener subscriber) {
			subscriber.onPhysicsSimulationStart(dynamicsWorld);
		}
	}

	private class PhysicsSimulationEndEvent implements Event<BulletSimulationTickListener> {
		@Override
		public Class<BulletSimulationTickListener> getSubscriptionType() {
			return BulletSimulationTickListener.class;
		}

		@Override
		public void dispatch(BulletSimulationTickListener subscriber) {
			subscriber.onPhysicsSimulationStart(dynamicsWorld);
		}
	}
}
