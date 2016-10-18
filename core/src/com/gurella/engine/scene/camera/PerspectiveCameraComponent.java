package com.gurella.engine.scene.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.gurella.engine.base.model.ModelDescriptor;

@ModelDescriptor(descriptiveName = "Perspective Camera")
public class PerspectiveCameraComponent extends CameraComponent<PerspectiveCamera> {
	public boolean depthTest = true;
	public Color ambientLight;
	public Color fog;

	public PerspectiveCameraComponent() {
		near = 0.1f;
	}

	@Override
	PerspectiveCamera createCamera() {
		return new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public float getFieldOfView() {
		return camera.fieldOfView;
	}

	public void setFieldOfView(float fieldOfView) {
		camera.fieldOfView = fieldOfView;
	}
}
