package com.gurella.engine.scene.renderable.shape;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class EmptyShapeModel extends ShapeModel {
	@Override
	protected Model createModel(ModelBuilder builder) {
		return null;
	}
}
