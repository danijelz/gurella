package com.gurella.engine.graphics.render.gl;

import static com.badlogic.gdx.graphics.GL20.GL_DECR;
import static com.badlogic.gdx.graphics.GL20.GL_DECR_WRAP;
import static com.badlogic.gdx.graphics.GL20.GL_INCR;
import static com.badlogic.gdx.graphics.GL20.GL_INCR_WRAP;
import static com.badlogic.gdx.graphics.GL20.GL_INVERT;
import static com.badlogic.gdx.graphics.GL20.GL_KEEP;
import static com.badlogic.gdx.graphics.GL20.GL_REPLACE;
import static com.badlogic.gdx.graphics.GL20.GL_ZERO;

import com.badlogic.gdx.utils.IntMap;

public enum StencilOp {
	keep(GL_KEEP),
	zero(GL_ZERO),
	replace(GL_REPLACE),
	incr(GL_INCR),
	incrWrap(GL_INCR_WRAP),
	decr(GL_DECR),
	decrWrap(GL_DECR_WRAP),
	invert(GL_INVERT);

	private static IntMap<StencilOp> valuesByGlValue = new IntMap<StencilOp>();

	static {
		StencilOp[] values = values();
		for (int i = 0, n = values.length; i < n; i++) {
			StencilOp value = values[i];
			valuesByGlValue.put(value.glValue, value);
		}
	}

	public final int glValue;

	private StencilOp(int glValue) {
		this.glValue = glValue;
	}

	public static StencilOp value(int glValue) {
		return valuesByGlValue.get(glValue);
	}
}
