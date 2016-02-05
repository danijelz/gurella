package com.gurella.engine.audio;

public class AudioService {
	private static final Volume volume = new Volume();
	private static VolumeSignal volumeSignal = new VolumeSignal();

	public static float getVolume() {
		return volume.getVolume();
	}

	public static void setVolume(float newVolume) {
		volume.setVolume(newVolume);
		volumeSignal.volumeChanged(volume.getVolume());
	}

	public static boolean addVolumeListener(VolumeListener listener) {
		return volumeSignal.addListener(listener);
	}

	public static boolean removeVolumeListener(VolumeListener listener) {
		return volumeSignal.removeListener(listener);
	}
}
