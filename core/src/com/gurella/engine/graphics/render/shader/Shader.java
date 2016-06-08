package com.gurella.engine.graphics.render.shader;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class Shader {
	private ObjectMap<String, ShaderUnifrom> uniforms;
	private Array<Technique> techniques;
}
