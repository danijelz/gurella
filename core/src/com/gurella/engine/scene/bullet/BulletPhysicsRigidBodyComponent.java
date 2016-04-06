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
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.movement.TransformComponent;

public class BulletPhysicsRigidBodyComponent extends SceneNodeComponent2 implements Poolable {
	static {
		Bullet.init();
	}
	
	private TransformComponent transformComponent;
	private btRigidBodyConstructionInfo constructionInfo;

	public boolean ghost;
	public boolean trigger;// CF_NO_CONTACT_RESPONSE
	public float mass;
	public final Vector3 localInertia = new Vector3();
	public int collisionGroup;
	public Bits collisionMasks = new Bits(32);

	public BulletPhysicsRigidBodyType rigidBodyType = BulletPhysicsRigidBodyType.DYNAMIC;
	public BulletPhysicsCollisionShape bulletPhysicsCollisionShape;
	public btRigidBody rigidBody;

	public BulletPhysicsRigidBodyComponent() {
	}

	public BulletPhysicsRigidBodyComponent(float mass, btCollisionShape collisionShape) {
		Vector3 inertia = new Vector3(0f, 0f, 0.001f);
		collisionShape.calculateLocalInertia(mass, inertia);
		constructionInfo = new btRigidBodyConstructionInfo(mass, null, collisionShape, inertia);
	}
	
	@Override
	protected void onActivate() {
		transformComponent = getNode().getComponent(TransformComponent.class);
		if (rigidBody == null) {
			constructionInfo.setMotionState(new MotionState());
			rigidBody = DisposablesService.add(new btRigidBody(constructionInfo));
			rigidBody.userData = this;
			rigidBody.setFriction(0.4f);
			rigidBody.setRestitution(0.2f);
			rigidBody.setAngularFactor(new Vector3(0, 0, 1));
		}

		rigidBody.activate(true);
	}
	
	@Override
	public void reset() {
		transformComponent.reset();
		rigidBody.setWorldTransform(transformComponent.getWorldTransform(rigidBody.getWorldTransform()));
		rigidBody.clearForces();
		rigidBody.setLinearVelocity(rigidBody.getLinearVelocity().setZero());
		rigidBody.setAngularVelocity(rigidBody.getAngularVelocity().setZero());
		
		/*
		TODO
		private TransformComponent transformComponent;
		private btRigidBodyConstructionInfo constructionInfo;

		public boolean ghost;
		public boolean trigger;// CF_NO_CONTACT_RESPONSE
		public float mass;
		public final Vector3 localInertia = new Vector3();
		public int collisionGroup;
		public Bits collisionMasks = new Bits(32);

		public BulletPhysicsRigidBodyType rigidBodyType = BulletPhysicsRigidBodyType.DYNAMIC;
		public BulletPhysicsCollisionShape bulletPhysicsCollisionShape;
		public btRigidBody rigidBody;*/
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
