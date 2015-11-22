package com.gurella.engine.audio;

import com.gurella.engine.signal.Signal1.Signal1Impl;

public class AudioService {
	private static final Volume volume = new Volume();
	public static Signal1Impl<Float> volumeListeners = new Signal1Impl<Float>();

	public static float getVolume() {
		return volume.getVolume();
	}

	public static void setVolume(float newVolume) {
		volume.setVolume(newVolume);
		volumeListeners.dispatch(Float.valueOf(volume.getVolume()));
	}
}
