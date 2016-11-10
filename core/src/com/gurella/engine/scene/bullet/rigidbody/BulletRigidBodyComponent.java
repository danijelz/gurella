package com.gurella.engine.scene.bullet.rigidbody;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.CollisionConstants;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btEmptyShape;
import com.badlogic.gdx.physics.bullet.collision.btGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.ModelDescriptor;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.editor.property.PropertyEditorDescriptor;
import com.gurella.engine.scene.RequiresComponent;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.bullet.shape.CollisionShape;
import com.gurella.engine.scene.debug.DebugRenderable;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;

@ModelDescriptor(descriptiveName = "Collision Object 3D")
@RequiresComponent(TransformComponent.class)
public class BulletRigidBodyComponent extends SceneNodeComponent2
		implements NodeComponentActivityListener, Poolable, DebugRenderable {

	static {
		Bullet.init();
	}

	public boolean ghost;
	public boolean allwaysActive;
	public boolean unresponsive;

	public int group;
	public int mask;

	@PropertyDescriptor(nullable = false)
	public BulletRigidBodyType type = BulletRigidBodyType.DYNAMIC;

	@PropertyDescriptor(nullable = false)
	//@PropertyEditorDescriptor(factory = CollisionShapePropertyEditorFactory.class)
	public CollisionShape shape;

	@PropertyEditorDescriptor(descriptiveName = "Calc. inertia")
	public boolean calculateInertia = true;
	public final Vector3 inertia = new Vector3(0, 0, 0);

	public final Vector3 gravity = new Vector3(0f, -9.8f, 0f);

	public float mass = 1;
	public float margin = 0.04f;
	public float friction;
	public float rollingFriction;
	public float restitution;

	@PropertyEditorDescriptor(group = "Factor", descriptiveName = "linear")
	public final Vector3 linearFactor = new Vector3(1, 1, 1);
	@PropertyEditorDescriptor(group = "Factor", descriptiveName = "angular")
	public final Vector3 angularFactor = new Vector3(1, 1, 1);

	@PropertyEditorDescriptor(group = "Damping", descriptiveName = "linear")
	public float linearDamping;
	@PropertyEditorDescriptor(group = "Damping", descriptiveName = "angular")
	public float angularDamping;

	@PropertyEditorDescriptor(group = "Sleeping Threshold", descriptiveName = "linear")
	public float linearSleepingThreshold;
	@PropertyEditorDescriptor(group = "Sleeping Threshold", descriptiveName = "angular")
	public float angularSleepingThreshold;

	@PropertyEditorDescriptor(group = "Additional damping factor", descriptiveName = "enabled")
	public boolean additionalDampingEnabled;
	@PropertyEditorDescriptor(group = "Additional damping factor", descriptiveName = "linear")
	public float additionalLinearDampingFactor;
	@PropertyEditorDescriptor(group = "Additional damping factor", descriptiveName = "angular")
	public float additionalAngularDampingFactor;

	@PropertyEditorDescriptor(group = "Initial state", descriptiveName = "sleeping")
	public boolean initialySleeping;
	@PropertyEditorDescriptor(group = "Initial state", descriptiveName = "linear")
	public Vector3 initialLinearVelocity;
	@PropertyEditorDescriptor(group = "Initial state", descriptiveName = "angular")
	public Vector3 initialAngularVelocity;

	private final transient MotionState motionState = new MotionState();
	public transient btCollisionObject collisionObject;

	private transient TransformComponent transformComponent;

	@Override
	protected void componentActivated() {
		transformComponent = getNode().getComponent(TransformComponent.class);
		createCollisionObject();
	}

	@Override
	protected void componentDeactivated() {
		transformComponent = null;
		if (collisionObject.isActive()) {
			collisionObject.activate(false);
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
			if (collisionObject != null && collisionObject.isActive()) {
				collisionObject.activate(false);
			}
		}
	}

	private void createCollisionObject() {
		if (collisionObject == null) {
			btRigidBodyConstructionInfo info = createConstructionInfo();
			collisionObject = ghost ? new btGhostObject() : new btRigidBody(info);
			collisionObject.userData = this;
			collisionObject.setCollisionFlags(collisionObject.getCollisionFlags() | getCollisionFlags());

			if (!ghost) {
				btRigidBody rigidBody = (btRigidBody) collisionObject;
				rigidBody.setLinearFactor(linearFactor);
				rigidBody.setAngularFactor(angularFactor);
				rigidBody.setGravity(gravity);

				if (initialLinearVelocity != null) {
					rigidBody.setLinearVelocity(initialLinearVelocity);
				}

				if (initialAngularVelocity != null) {
					rigidBody.setAngularVelocity(initialAngularVelocity);
				}
			}

			if (allwaysActive) {
				collisionObject.setActivationState(CollisionConstants.DISABLE_DEACTIVATION);
			} else if (initialySleeping) {
				collisionObject.setActivationState(0);
			}

			info.dispose();// TODO remove when pooled
		}

		if (transformComponent != null) {
			transformComponent.getWorldTransform(collisionObject.getWorldTransform());
			collisionObject.activate(true);
		}
	}

	// TODO other CollisionFlags
	private int getCollisionFlags() {
		int collisionFlags = unresponsive ? CollisionFlags.CF_NO_CONTACT_RESPONSE
				: CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK;
		switch (type) {
		case KINEMATIC:
			collisionFlags |= CollisionFlags.CF_KINEMATIC_OBJECT;
			break;
		case STATIC:
			collisionFlags |= CollisionFlags.CF_STATIC_OBJECT;
			break;
		default:
			break;
		}
		return collisionFlags;
	}

	private btRigidBodyConstructionInfo createConstructionInfo() {
		btCollisionShape nativeShape = shape == null ? new btEmptyShape() : shape.createNativeShape();
		nativeShape.setMargin(margin);

		float mass = this.mass;
		mass = mass < 0 ? 0 : mass;
		Vector3 inertia = this.inertia;
		if (mass > 0 && calculateInertia) {
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
		info.setAdditionalDamping(additionalDampingEnabled);
		info.setAdditionalDampingFactor(additionalLinearDampingFactor);
		info.setAdditionalAngularDampingFactor(additionalAngularDampingFactor);

		return info;
	}

	@Override
	public void debugRender(DebugRenderContext context) {
		if (shape != null && isActive()) {
			shape.debugRender(context.batch, transformComponent);
		}
	}

	@Override
	public void reset() {
		transformComponent = null;
		if (collisionObject != null) {
			btCollisionShape collisionShape = collisionObject.getCollisionShape();
			if (collisionShape != null) {
				collisionShape.dispose();
			}
			collisionObject.dispose();
			collisionObject = null;
		}
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
