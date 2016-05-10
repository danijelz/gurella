package com.gurella.engine.scene.bullet;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.bullet.shapes.BulletPhysicsCollisionShape;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;

public class BulletPhysicsRigidBodyComponent extends SceneNodeComponent2
		implements NodeComponentActivityListener, Poolable {
	static {
		Bullet.init();
	}

	private transient TransformComponent transformComponent;
	private btRigidBodyConstructionInfo constructionInfo;

	public boolean ghost;
	public boolean disableContactResponse;// CF_NO_CONTACT_RESPONSE

	public final BulletMaterial bulletMaterial = new BulletMaterial();
	private final transient MotionState motionState = new MotionState();
	public BulletPhysicsCollisionShape bulletPhysicsCollisionShape;

	public int collisionGroup;
	public Bits collisionMasks = new Bits(32);

	public BulletPhysicsRigidBodyType rigidBodyType = BulletPhysicsRigidBodyType.DYNAMIC;
	public transient btRigidBody rigidBody;

	public BulletPhysicsRigidBodyComponent() {
	}

	public BulletPhysicsRigidBodyComponent(float mass, btCollisionShape collisionShape) {
		Vector3 inertia = new Vector3(0f, 0f, 0.001f);
		collisionShape.calculateLocalInertia(mass, inertia);
		constructionInfo = new btRigidBodyConstructionInfo(mass, null, collisionShape, inertia);
	}

	@Override
	protected void onActivate() {
		createCollisionObject();
		transformComponent = getNode().getComponent(TransformComponent.class);
		if (rigidBody != null && transformComponent != null) {
			transformComponent.getWorldTransform(rigidBody.getWorldTransform());
			rigidBody.activate(true);
		}
	}

	private void createCollisionObject() {
		if (rigidBody == null) {
			constructionInfo.setMotionState(motionState);
			constructionInfo.setFriction(0.4f);
			constructionInfo.setRestitution(0.2f);
			rigidBody = new btRigidBody(constructionInfo);
			rigidBody.userData = this;
			rigidBody.setAngularFactor(new Vector3(0, 0, 1));
		}
	}

	@Override
	protected void onDeactivate() {
		transformComponent = null;
		if (rigidBody.isActive()) {
			rigidBody.activate(false);
		}
	}

	@Override
	public void nodeComponentActivated(SceneNodeComponent2 component) {
		if (component instanceof TransformComponent) {
			transformComponent = (TransformComponent) component;
			rigidBody.activate(true);
		}
	}

	@Override
	public void nodeComponentDeactivated(SceneNodeComponent2 component) {
		if (component instanceof TransformComponent) {
			transformComponent = null;
			if (rigidBody.isActive()) {
				rigidBody.activate(false);
			}
		}
	}

	@Override
	public void reset() {
		transformComponent = null;
		rigidBody.dispose();
		rigidBody = null;
	}

	private class MotionState extends btMotionState {
		@Override
		public void getWorldTransform(Matrix4 worldTransform) {
			transformComponent.getWorldTransform(worldTransform);
		}

		@Override
		public void setWorldTransform(Matrix4 worldTransform) {
			transformComponent.setWorldTransform(worldTransform);
		}
	}
}
