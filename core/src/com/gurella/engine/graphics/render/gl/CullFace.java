package com.gurella.engine.graphics.render.gl;

import static com.badlogic.gdx.graphics.GL20.GL_BACK;
import static com.badlogic.gdx.graphics.GL20.GL_FRONT;
import static com.badlogic.gdx.graphics.GL20.GL_FRONT_AND_BACK;

import com.badlogic.gdx.utils.IntMap;

public enum CullFace {
	disabled(-1), front(GL_FRONT), back(GL_BACK), frontAndBack(GL_FRONT_AND_BACK);

	private static IntMap<CullFace> valuesByGlValue = new IntMap<CullFace>();

	static {
		CullFace[] values = values();
		for (int i = 0, n = values.length; i < n; i++) {
			CullFace value = values[i];
			valuesByGlValue.put(value.glValue, value);
		}
	}

	public final int glValue;

	private CullFace(int glValue) {
		this.glValue = glValue;
	}

	public static CullFace value(int glValue) {
		return valuesByGlValue.get(glValue);
	}

	public static boolean isEnabled(CullFace cullFace) {
		return cullFace != null && cullFace != disabled;
	}
}
