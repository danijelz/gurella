package com.gurella.engine.scene.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.gurella.engine.base.model.ModelDescriptor;
import com.gurella.engine.editor.property.PropertyEditorDescriptor;

@ModelDescriptor(descriptiveName = "Perspective Camera")
public class PerspectiveCameraComponent extends CameraComponent<PerspectiveCamera> {
	public boolean depthTest = true;
	@PropertyEditorDescriptor(group = "Environment")
	public Color ambientLight;
	@PropertyEditorDescriptor(group = "Environment")
	public Color fog;

	@Override
	PerspectiveCamera createCamera() {
		Graphics graphics = Gdx.graphics;
		PerspectiveCamera camera = new PerspectiveCamera(67, graphics.getWidth(), graphics.getHeight());
		camera.near = 0.1f;
		camera.far = 1000;
		return camera;
	}

	public float getFieldOfView() {
		return camera.fieldOfView;
	}

	public void setFieldOfView(float fieldOfView) {
		camera.fieldOfView = fieldOfView;
	}

	@Override
	public void reset() {
		super.reset();
		camera.fieldOfView = 67;
		camera.near = 0.1f;
		camera.far = 1000;
	}
}
