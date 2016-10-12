package com.gurella.engine.scene.bullet.rigidbody;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btEmptyShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.ModelDescriptor;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.editor.property.PropertyEditorDescriptor;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.bullet.rigidbody.shape.BulletCollisionShape;
import com.gurella.engine.scene.debug.DebugRenderable;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;

@ModelDescriptor(descriptiveName = "Collision Object 3D")
public class BulletRigidBodyComponent extends SceneNodeComponent2
		implements NodeComponentActivityListener, Poolable, DebugRenderable {

	static {
		Bullet.init();
	}

	public boolean ghost;
	public boolean unresponsive;// CF_NO_CONTACT_RESPONSE

	public int collisionGroup;
	public int collisionMask;
	public BulletRigidBodyType rigidBodyType = BulletRigidBodyType.DYNAMIC;

	@PropertyDescriptor(nullable = false)
	@PropertyEditorDescriptor(factory = CollisionShapePropertyEditorFactory.class)
	public BulletCollisionShape shape;

	@PropertyDescriptor(descriptiveName = "Calc. inertia")
	public boolean inertiaFromShape = true;
	public final Vector3 inertia = new Vector3(0, 0, 0);

	public final Vector3 angularFactor = new Vector3(1, 1, 1);
	public final Vector3 linearFactor = new Vector3(1, 1, 1);

	public final Vector3 gravity = new Vector3(0f, -9.8f, 0f);

	public float mass;

	public float margin = 0.04f;

	public float linearDamping;
	public float angularDamping;

	public float friction;
	public float rollingFriction;

	public float restitution;

	public float linearSleepingThreshold;
	public float angularSleepingThreshold;

	@PropertyEditorDescriptor(group = "Additional damping")
	public boolean additionalDamping;
	@PropertyEditorDescriptor(group = "Additional damping")
	public float additionalDampingFactor;
	@PropertyEditorDescriptor(group = "Additional damping")
	public float additionalAngularDampingFactor;

	private final transient MotionState motionState = new MotionState();
	public transient btRigidBody rigidBody;

	private transient TransformComponent transformComponent;

	@Override
	protected void componentActivated() {
		transformComponent = getNode().getComponent(TransformComponent.class);
		createCollisionObject();
	}

	@Override
	protected void componentDeactivated() {
		transformComponent = null;
		if (rigidBody.isActive()) {
			rigidBody.activate(false);
		}
	}

	@Override
	public void nodeComponentActivated(SceneNodeComponent2 component) {
		if (component instanceof TransformComponent) {
			transformComponent = (TransformComponent) component;
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

	private void createCollisionObject() {
		if (rigidBody == null) {
			btRigidBodyConstructionInfo info = createConstructionInfo();
			rigidBody = new btRigidBody(info);
			rigidBody.userData = this;
			rigidBody.setLinearFactor(linearFactor);
			rigidBody.setAngularFactor(angularFactor);
			rigidBody.setGravity(gravity);
			info.dispose();// TODO remove when pooled
		}

		if (transformComponent != null) {
			transformComponent.getWorldTransform(rigidBody.getWorldTransform());
			rigidBody.activate(true);
		}
	}

	private btRigidBodyConstructionInfo createConstructionInfo() {
		btCollisionShape nativeShape = shape == null ? new btEmptyShape() : shape.createNativeShape();
		nativeShape.setMargin(margin);

		float mass = this.mass;
		mass = mass < 0 ? 0 : mass;
		Vector3 inertia = this.inertia;
		if (mass > 0 && inertiaFromShape) {
			nativeShape.calculateLocalInertia(mass, inertia);
		}

		// TODO create info from pool
		btRigidBodyConstructionInfo info = new btRigidBodyConstructionInfo(mass, motionState, nativeShape, inertia);
		info.setFriction(friction);
		info.setRollingFriction(rollingFriction);
		info.setRestitution(restitution);
		info.setLinearDamping(linearDamping);
		info.setAngularDamping(angularDamping);
		info.setLinearSleepingThreshold(linearSleepingThreshold);
		info.setAngularSleepingThreshold(angularSleepingThreshold);
		info.setAdditionalDamping(additionalDamping);
		info.setAdditionalDampingFactor(additionalDampingFactor);
		info.setAdditionalAngularDampingFactor(additionalAngularDampingFactor);

		return info;
	}

	@Override
	public void debugRender(GenericBatch batch) {
		if (shape != null) {
			shape.debugRender(batch, transformComponent);
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
			if (transformComponent == null) {
				worldTransform.idt();
			} else {
				transformComponent.getWorldTransform(worldTransform);
			}
		}

		@Override
		public void setWorldTransform(Matrix4 worldTransform) {
			if (transformComponent != null) {
				transformComponent.setWorldTransform(worldTransform);
			}
		}
	}
}
