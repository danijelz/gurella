package com.gurella.engine.audio;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.resource.model.ValueRange;
import com.gurella.engine.resource.model.ValueRange.FloatRange;

public class Volume implements Poolable {
	@ValueRange(floatRange = @FloatRange(min = 0, max = 1) )
	private float volume = 1;

	public static Volume getInstance(float volume) {
		Volume volumeObj = new Volume();
		volumeObj.setVolume(volume);
		return volumeObj;
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
