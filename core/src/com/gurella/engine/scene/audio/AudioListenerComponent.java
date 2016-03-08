package com.gurella.engine.scene.audio;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.scene.SceneNodeComponent2;

public class AudioListenerComponent extends SceneNodeComponent2 implements Poolable {
	@PropertyDescriptor
	public final Vector3 up = new Vector3();
	@PropertyDescriptor
	public final Vector3 lookAt = new Vector3();

	@Override
	public void reset() {
		up.setZero();
		lookAt.setZero();
	}
}
