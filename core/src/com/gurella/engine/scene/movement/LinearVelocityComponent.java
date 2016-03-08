package com.gurella.engine.scene.movement;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.scene.SceneNodeComponent2;

public class LinearVelocityComponent extends SceneNodeComponent2 implements Poolable {
	@PropertyDescriptor
	public final Vector3 lastPosition = new Vector3(Float.NaN, Float.NaN, Float.NaN);
	@PropertyDescriptor
	public final Vector3 velocity = new Vector3();

	@Override
	public void reset() {
		lastPosition.set(Float.NaN, Float.NaN, Float.NaN);
		velocity.setZero();
	}
}
