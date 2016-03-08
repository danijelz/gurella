package com.gurella.engine.scene.audio;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.scene.SceneNodeComponent2;

public class AudioListenerComponent extends SceneNodeComponent2 implements Poolable {
	public final Vector3 up = new Vector3();
	public final Vector3 lookAt = new Vector3();

	@Override
	public void reset() {
		up.setZero();
		lookAt.setZero();
	}
}
