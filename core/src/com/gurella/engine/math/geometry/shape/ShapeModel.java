package com.gurella.engine.math.geometry.shape;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;

//TODO unused
public abstract class ShapeModel<T extends Shape> {
	T shape;
	private VertexAttributes attributes;
	private Material material;
}
