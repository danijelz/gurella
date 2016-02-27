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
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.TypePriorities;
import com.gurella.engine.event.TypePriority;
import com.gurella.engine.scene.SceneListener;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.SceneSystem;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.subscriptions.application.CommonUpdatePriority;
import com.gurella.engine.subscriptions.scene.bullet.BulletSimulationTickListener;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Values;

//TODO attach listeners on activate -> SceneListener
@TypePriorities({ @TypePriority(priority = CommonUpdatePriority.PHYSICS, type = ApplicationUpdateListener.class) })
public class BulletPhysicsSystem extends SceneSystem implements SceneListener, ApplicationUpdateListener {
	static {
		Bullet.init();
	}

	private btCollisionConfiguration collisionConfig;
	private btDispatcher dispatcher;
	private btBroadphaseInterface broadphase;
	private btConstraintSolver constraintSolver;

	private btDynamicsWorld dynamicsWorld;

	private CollisionTrackingInternalTickCallback tickCallback;

	private Vector3 gravity = new Vector3(0, -10f, 0);

	private final Array<Object> tempListeners = new Array<Object>(64);

	public BulletPhysicsSystem() {
		collisionConfig = DisposablesService.add(new btDefaultCollisionConfiguration());
		dispatcher = DisposablesService.add(new btCollisionDispatcher(collisionConfig));
		broadphase = DisposablesService.add(new btDbvtBroadphase());
		constraintSolver = DisposablesService.add(new btSequentialImpulseConstraintSolver());

		dynamicsWorld = DisposablesService.add(new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig));
		dynamicsWorld.setGravity(gravity);

		tickCallback = DisposablesService.add(new CollisionTrackingInternalTickCallback(tempListeners));
		tickCallback.attach(dynamicsWorld, false);
	}

	@Override
	protected void activated() {
		ImmutableArray<SceneNodeComponent> components = getScene().activeComponents;
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
	}

	@Override
	public void update() {
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
	protected void resetted() {
		super.resetted();
		dynamicsWorld.clearForces();
		tickCallback.clear();
	}

	@Override
	public void componentAdded(SceneNodeComponent component) {
	}

	@Override
	public void componentRemoved(SceneNodeComponent component) {
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
