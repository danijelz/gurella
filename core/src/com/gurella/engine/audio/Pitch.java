package com.gurella.engine.audio;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Pitch implements Poolable {
	private float pitch = 1;

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = MathUtils.clamp(pitch, 0.5f, 2);
	}

	public void set(Pitch pitch) {
		this.pitch = pitch.pitch;
	}

	@Override
	public void reset() {
		pitch = 1;
	}
}
