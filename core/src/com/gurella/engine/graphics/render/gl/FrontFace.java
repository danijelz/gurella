package com.gurella.engine.graphics.render.gl;

import static com.badlogic.gdx.graphics.GL20.GL_CCW;
import static com.badlogic.gdx.graphics.GL20.GL_CW;

import com.badlogic.gdx.utils.IntMap;

public enum FrontFace {
	cw(GL_CW), ccw(GL_CCW);

	public static final FrontFace defaultValue = ccw;

	private static IntMap<FrontFace> valuesByGlValue = new IntMap<FrontFace>();

	static {
		FrontFace[] values = values();
		for (int i = 0, n = values.length; i < n; i++) {
			FrontFace value = values[i];
			valuesByGlValue.put(value.glValue, value);
		}
	}

	public final int glValue;

	private FrontFace(int glValue) {
		this.glValue = glValue;
	}

	public static FrontFace value(int glValue) {
		return valuesByGlValue.get(glValue);
	}
}
