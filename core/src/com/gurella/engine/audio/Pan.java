package com.gurella.engine.audio;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Pan implements Poolable {
	private float pan;

	public float getPan() {
		return pan;
	}

	public void setPan(float pan) {
		this.pan = MathUtils.clamp(pan, -1, 1);
	}

	public void set(Pan pan) {
		this.pan = pan.pan;
	}

	@Override
	public void reset() {
		pan = 0;
	}
}
