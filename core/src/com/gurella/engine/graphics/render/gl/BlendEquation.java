package com.gurella.engine.graphics.render.gl;

import static com.badlogic.gdx.graphics.GL20.GL_FUNC_ADD;
import static com.badlogic.gdx.graphics.GL20.GL_FUNC_REVERSE_SUBTRACT;
import static com.badlogic.gdx.graphics.GL20.GL_FUNC_SUBTRACT;

import com.badlogic.gdx.utils.IntMap;

public enum BlendEquation {
	add(GL_FUNC_ADD), substract(GL_FUNC_SUBTRACT), reverseSubstract(GL_FUNC_REVERSE_SUBTRACT);

	private static IntMap<BlendEquation> functionsByGlValue = new IntMap<BlendEquation>();

	static {
		BlendEquation[] values = values();
		for (int i = 0, n = values.length; i < n; i++) {
			BlendEquation value = values[i];
			functionsByGlValue.put(value.glValue, value);
		}
	}

	public final int glValue;

	private BlendEquation(int glValue) {
		this.glValue = glValue;
	}

	public static BlendEquation value(int glValue) {
		return functionsByGlValue.get(glValue);
	}
}
