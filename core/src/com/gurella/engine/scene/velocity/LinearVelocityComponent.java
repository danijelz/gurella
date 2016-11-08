package com.gurella.engine.scene.velocity;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.ModelDescriptor;
import com.gurella.engine.scene.RequiresComponent;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.transform.TransformComponent;

@ModelDescriptor(descriptiveName = "Linear Velocity")
@RequiresComponent(TransformComponent.class)
public class LinearVelocityComponent extends SceneNodeComponent2 implements Poolable {
	public final Vector3 lastPosition = new Vector3(Float.NaN, Float.NaN, Float.NaN);
	public final Vector3 velocity = new Vector3();

	@Override
	public void reset() {
		lastPosition.set(Float.NaN, Float.NaN, Float.NaN);
		velocity.setZero();
	}
}
