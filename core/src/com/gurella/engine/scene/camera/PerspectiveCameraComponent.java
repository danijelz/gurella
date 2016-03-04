package com.gurella.engine.scene.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.Pool.Poolable;

public class PerspectiveCameraComponent extends CameraComponent<PerspectiveCamera> implements Poolable {
	/** the field of view in degrees **/
	private float fieldOfView = 67;

	@Override
	PerspectiveCamera createCamera() {
		return new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	void initCamera() {
		super.initCamera();
		camera.fieldOfView = fieldOfView;
	}

	public float getFieldOfView() {
		return fieldOfView;
	}

	public void setFieldOfView(float fieldOfView) {
		this.fieldOfView = fieldOfView;
		camera.fieldOfView = fieldOfView;
	}
}
