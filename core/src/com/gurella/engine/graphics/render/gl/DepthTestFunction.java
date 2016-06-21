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

public enum DepthTestFunction {
	never(GL_NEVER),
	less(GL_LESS),
	equal(GL_EQUAL),
	lequal(GL_LEQUAL),
	greater(GL_GREATER),
	notequal(GL_NOTEQUAL),
	gequal(GL_GEQUAL),
	always(GL_ALWAYS);

	private static IntMap<DepthTestFunction> valuesByGlValue = new IntMap<DepthTestFunction>();

	static {
		DepthTestFunction[] values = values();
		for (int i = 0, n = values.length; i < n; i++) {
			DepthTestFunction value = values[i];
			valuesByGlValue.put(value.glValue, value);
		}
	}

	public final int glValue;

	private DepthTestFunction(int glValue) {
		this.glValue = glValue;
	}

	public static DepthTestFunction value(int glValue) {
		return valuesByGlValue.get(glValue);
	}
}
