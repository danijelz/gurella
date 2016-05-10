package com.gurella.engine.scene.bullet;

import com.badlogic.gdx.math.Vector3;

public class BulletMaterial {
	public float mass;
	public final Vector3 gravity = new Vector3(0f, -9.8f, 0f);
	public final Vector3 localInertia = new Vector3(0f, 0f, 0.001f);
	public final Vector3 angularFactor = new Vector3(1, 1, 1);
	public float linearDamping;
	public float angularDamping;
	public float friction;
	public float rollingFriction;
	public float restitution;
	public float linearSleepingThreshold;
	public float angularSleepingThreshold;
	public float additionalDamping;
	public float additionalDampingFactor;
	public float additionalLinearDampingThresholdSqr;
	public float additionalAngularDampingThresholdSqr;
	public float additionalAngularDampingFactor;
}
