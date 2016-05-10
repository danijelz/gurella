package com.gurella.engine.scene.bullet;

import com.badlogic.gdx.math.Vector3;

public class BulletMaterial {
	public float mass;
	public final Vector3 gravity = new Vector3();
	public final Vector3 localInertia = new Vector3();
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
