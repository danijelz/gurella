package com.gurella.engine.graphics.render.material;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.ObjectMap;

public class MaterialInstance {
	private Material material;
	private Technique technique;
	private ObjectMap<Pass, ShaderProgram> passPrograms = new ObjectMap<Pass, ShaderProgram>();
}
