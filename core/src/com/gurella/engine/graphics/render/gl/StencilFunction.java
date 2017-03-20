package com.gurella.engine.graphics.render.gl;

import static com.badlogic.gdx.graphics.GL20.GL_ALWAYS;
import static com.badlogic.gdx.graphics.GL20.GL_EQUAL;
import static com.badlogic.gdx.graphics.GL20.GL_GEQUAL;
import static com.badlogic.gdx.graphics.GL20.GL_GREATER;
import static com.badlogic.gdx.graphics.GL20.GL_LEQUAL;
import static com.badlogic.gdx.graphics.GL20.GL_LESS;
import static com.badlogic.gdx.graphics.GL20.GL_NEVER;
import static com.badlogic.gdx.graphics.GL20.GL_NOTEQUAL;

import com.badlogic.gdx.utils.IntMap;

public enum StencilFunction {
	never(GL_NEVER),
	less(GL_LESS),
	equal(GL_EQUAL),
	lequal(GL_LEQUAL),
	greater(GL_GREATER),
	notequal(GL_NOTEQUAL),
	gequal(GL_GEQUAL),
	always(GL_ALWAYS);
	
	public static final StencilFunction defaultValue = always;

	private static IntMap<StencilFunction> valuesByGlValue = new IntMap<StencilFunction>();

	static {
		StencilFunction[] values = values();
		for (int i = 0, n = values.length; i < n; i++) {
			StencilFunction value = values[i];
			valuesByGlValue.put(value.glValue, value);
		}
	}

	public final int glValue;

	private StencilFunction(int glValue) {
		this.glValue = glValue;
	}

	public static StencilFunction value(int glValue) {
		return valuesByGlValue.get(glValue);
	}
}
