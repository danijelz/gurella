package com.gurella.engine.audio;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.metatype.ValueRange;
import com.gurella.engine.metatype.ValueRange.FloatRange;

public class Volume implements Poolable {
	@ValueRange(floatRange = @FloatRange(min = 0, max = 1))
	private float volume = 1;

	public Volume() {
	}

	public Volume(float volume) {
		setVolume(volume);
	}

	public static Volume getInstance(float volume) {
		return new Volume(volume);
	}

	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = MathUtils.clamp(volume, 0, 1);
	}

	public void set(Volume volume) {
		this.volume = volume.getVolume();
	}

	@Override
	public void reset() {
		volume = 1;
	}
}
