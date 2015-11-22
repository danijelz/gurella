package com.gurella.engine.audio;

import com.badlogic.gdx.math.MathUtils;

public class Pan {
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
}
