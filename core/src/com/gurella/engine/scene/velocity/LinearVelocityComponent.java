package com.gurella.engine.scene.velocity;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.metatype.MetaTypeDescriptor;
import com.gurella.engine.metatype.TransientProperty;
import com.gurella.engine.scene.RequiresComponent;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.transform.TransformComponent;

@MetaTypeDescriptor(descriptiveName = "Linear Velocity")
@RequiresComponent(TransformComponent.class)
public class LinearVelocityComponent extends SceneNodeComponent implements Poolable {
	@TransientProperty
	public final Vector3 lastPosition = new Vector3(Float.NaN, Float.NaN, Float.NaN);
	@TransientProperty
	public final Vector3 velocity = new Vector3();

	@Override
	public void reset() {
		lastPosition.set(Float.NaN, Float.NaN, Float.NaN);
		velocity.setZero();
	}
}
