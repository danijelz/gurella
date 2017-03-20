package com.gurella.engine.graphics.render.gl;

import static com.badlogic.gdx.graphics.GL20.GL_CONSTANT_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_CONSTANT_COLOR;
import static com.badlogic.gdx.graphics.GL20.GL_DST_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_DST_COLOR;
import static com.badlogic.gdx.graphics.GL20.GL_ONE;
import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_CONSTANT_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_CONSTANT_COLOR;
import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_DST_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_DST_COLOR;
import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_COLOR;
import static com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_SRC_COLOR;
import static com.badlogic.gdx.graphics.GL20.GL_ZERO;

import com.badlogic.gdx.utils.IntMap;

public enum BlendFunction {
	zero(GL_ZERO),
	one(GL_ONE),
	srcColor(GL_SRC_COLOR),
	oneMinusSrcColor(GL_ONE_MINUS_SRC_COLOR),
	dstColor(GL_DST_COLOR),
	oneMinusDstColor(GL_ONE_MINUS_DST_COLOR),
	srcAlpha(GL_SRC_ALPHA),
	oneMinusSrcAlpha(GL_ONE_MINUS_SRC_ALPHA),
	dstAlpha(GL_DST_ALPHA),
	oneMinusDstAlpha(GL_ONE_MINUS_DST_ALPHA),
	constantColor(GL_CONSTANT_COLOR),
	oneMinusConstantColor(GL_ONE_MINUS_CONSTANT_COLOR),
	constantAlpha(GL_CONSTANT_ALPHA),
	oneMinusConstantAlpha(GL_ONE_MINUS_CONSTANT_ALPHA);

	private static IntMap<BlendFunction> functionsByGlValue = new IntMap<BlendFunction>();
	
	public static final BlendFunction defaultSource = one;
	public static final BlendFunction defaultDestination = zero;

	static {
		BlendFunction[] values = values();
		for (int i = 0, n = values.length; i < n; i++) {
			BlendFunction value = values[i];
			functionsByGlValue.put(value.glValue, value);
		}
	}

	public final int glValue;

	private BlendFunction(int glValue) {
		this.glValue = glValue;
	}

	public static BlendFunction value(int glValue) {
		return functionsByGlValue.get(glValue);
	}
}
