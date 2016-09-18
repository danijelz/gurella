package com.gurella.engine.scene.bullet;

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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.SceneService;
import com.gurella.engine.subscriptions.application.ApplicationActivityListener;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.subscriptions.scene.bullet.BulletSimulationTickListener;
import com.gurella.engine.subscriptions.scene.update.PhysicsUpdateListener;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Values;

public class BulletPhysicsSystem extends SceneService
		implements ComponentActivityListener, PhysicsUpdateListener, ApplicationActivityListener, Poolable {
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

	private final Array<Object> tempListeners = new Array<Object>(64);

	public BulletPhysicsSystem() {
		collisionConfig = DisposablesService.add(new btDefaultCollisionConfiguration());
		dispatcher = DisposablesService.add(new btCollisionDispatcher(collisionConfig));
		broadphase = DisposablesService.add(new btDbvtBroadphase());
		constraintSolver = DisposablesService.add(new btSequentialImpulseConstraintSolver());

		dynamicsWorld = DisposablesService
				.add(new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig));
		dynamicsWorld.setGravity(gravity);

		tickCallback = DisposablesService.add(new CollisionTrackingCallback(tempListeners));
		tickCallback.attach(dynamicsWorld, false);
	}

	@Override
	protected void serviceActivated() {
		ImmutableArray<SceneNodeComponent2> components = getScene().activeComponents;
		for (int i = 0; i < components.size(); i++) {
			componentActivated(components.get(i));
		}
		// TODO paused = Application.isPaused();
	}

	@Override
	protected void serviceDeactivated() {
		int numCollisionObjects = dynamicsWorld.getNumCollisionObjects();
		btCollisionObjectArray collisionObjectArray = dynamicsWorld.getCollisionObjectArray();

		for (int i = 0; i < numCollisionObjects; i++) {
			dynamicsWorld.removeCollisionObject(collisionObjectArray.at(i));
		}

		tickCallback.clear();
	}

	@Override
	public void onPhysicsUpdate() {
		if (paused && stopSimulationOnPause) {
			return;
		}

		dispatchSimulationStartEvent();
		dynamicsWorld.stepSimulation(Gdx.graphics.getDeltaTime(), 5, 1f / 60f);
		dispatchSimulationEndEvent();
	}

	private void dispatchSimulationStartEvent() {
		Array<BulletSimulationTickListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(BulletSimulationTickListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).onPhysicsSimulationStart(dynamicsWorld);
		}
	}

	private void dispatchSimulationEndEvent() {
		Array<BulletSimulationTickListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(BulletSimulationTickListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).onPhysicsSimulationEnd(dynamicsWorld);
		}
	}

	@Override
	public void reset() {
		dynamicsWorld.clearForces();
		tickCallback.clear();
	}

	@Override
	public void componentActivated(SceneNodeComponent2 component) {
		if (component instanceof BulletRigidBodyComponent) {
			BulletRigidBodyComponent rigidBodyComponent = (BulletRigidBodyComponent) component;
			dynamicsWorld.addRigidBody(rigidBodyComponent.rigidBody);
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent2 component) {
		if (component instanceof BulletRigidBodyComponent) {
			BulletRigidBodyComponent rigidBodyComponent = (BulletRigidBodyComponent) component;
			dynamicsWorld.removeRigidBody(rigidBodyComponent.rigidBody);
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
}
