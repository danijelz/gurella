package com.gurella.engine.scene.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.gurella.engine.base.model.ModelDescriptor;

@ModelDescriptor(descriptiveName = "Ortographic Camera")
public class OrtographicCameraComponent extends CameraComponent<OrthographicCamera> {
	private float zoom = 1;

	@Override
	OrthographicCamera createCamera() {
		return new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	void initCamera() {
		super.initCamera();
		camera.zoom = zoom;
		camera.near = 0;
	}

	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
		camera.zoom = zoom;
	}

	@Override
	public void reset() {
		super.reset();
		zoom = 1;
	}
}
