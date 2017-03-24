package com.gurella.engine.graphics.render.renderable;

import com.badlogic.gdx.math.Matrix4;
import com.gurella.engine.graphics.render.material.MaterialInstance;

public interface Renderable {
	Matrix4 getLocalTransform();

	Matrix4 getWorldTransform();

	RenederableGeometry getGeometry();

	MaterialInstance getMaterialInstance();
}
