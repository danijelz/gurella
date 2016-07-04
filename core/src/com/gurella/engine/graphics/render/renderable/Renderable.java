package com.gurella.engine.graphics.render.renderable;

import com.badlogic.gdx.math.Matrix4;
import com.gurella.engine.graphics.render.material.Material;
import com.gurella.engine.scene.camera.CameraComponent;
import com.gurella.engine.scene.renderable.RenderableComponent;

public interface Renderable {
	Matrix4 getWorldTransform();

	RenederableGeometry getGeometry();

	RenderableComponent getComponent();
	
	CameraComponent<?> getCamera();
	
	Material getMaterial();
}
