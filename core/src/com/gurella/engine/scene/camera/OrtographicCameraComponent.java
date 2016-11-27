package com.gurella.engine.scene.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.gurella.engine.metatype.ModelDescriptor;

@ModelDescriptor(descriptiveName = "Ortographic Camera")
public class OrtographicCameraComponent extends CameraComponent<OrthographicCamera> {
	@Override
	OrthographicCamera createCamera() {
		Graphics graphics = Gdx.graphics;
		OrthographicCamera camera = new OrthographicCamera(graphics.getWidth(), graphics.getHeight());
		camera.near = 0;
		return camera;
	}

	public float getZoom() {
		return camera.zoom;
	}

	public void setZoom(float zoom) {
		camera.zoom = zoom;
	}

	@Override
	public void reset() {
		super.reset();
		camera.zoom = 1;
		camera.near = 0;
		camera.far = 100;
	}
}
