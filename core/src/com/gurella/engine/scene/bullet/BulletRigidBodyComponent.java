package com.gurella.engine.scene.bullet;

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
import com.gurella.engine.scene.bullet.shapes.BulletCollisionShape;
import com.gurella.engine.scene.bullet.shapes.EmptyCollisionShape;
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

	@PropertyEditorDescriptor(factory = CollisionShapePropertyEditorFactory.class)
	@PropertyDescriptor(nullable = false)
	public BulletCollisionShape collisionShape;
	public final BulletMaterial material = new BulletMaterial();

	private final transient MotionState motionState = new MotionState();
	transient btRigidBody rigidBody;

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
			rigidBody.setAngularFactor(material.angularFactor);
			rigidBody.setGravity(material.gravity);
			info.dispose();// TODO remove when pooled
		}

		if (transformComponent != null) {
			transformComponent.getWorldTransform(rigidBody.getWorldTransform());
			rigidBody.activate(true);
		}
	}

	private btRigidBodyConstructionInfo createConstructionInfo() {
		btCollisionShape nativeShape = collisionShape == null ? new btEmptyShape() : collisionShape.createNativeShape();
		nativeShape.setMargin(material.margin);

		float mass = material.mass;
		mass = mass < 0 ? 0 : mass;
		Vector3 inertia = material.localInertia;
		if (mass > 0 && material.inertiaFromShape) {
			nativeShape.calculateLocalInertia(mass, inertia);
		}

		// TODO create info from pool
		btRigidBodyConstructionInfo info = new btRigidBodyConstructionInfo(mass, motionState, nativeShape, inertia);
		info.setFriction(material.friction);
		info.setRollingFriction(material.rollingFriction);
		info.setRestitution(material.restitution);
		info.setLinearDamping(material.linearDamping);
		info.setAngularDamping(material.angularDamping);
		info.setLinearSleepingThreshold(material.linearSleepingThreshold);
		info.setAngularSleepingThreshold(material.angularSleepingThreshold);
		info.setAdditionalDamping(material.additionalDamping);
		info.setAdditionalDampingFactor(material.additionalDampingFactor);
		info.setAdditionalAngularDampingFactor(material.additionalAngularDampingFactor);

		return info;
	}

	@Override
	public void debugRender(GenericBatch batch) {
		if (collisionShape != null && !(collisionShape instanceof EmptyCollisionShape)) {
			collisionShape.debugRender(batch, transformComponent);
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
