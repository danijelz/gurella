package com.gurella.engine.audio;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

public class AudioService {
	private static final ObjectMap<Application, AudioService> instances = new ObjectMap<Application, AudioService>();

	private final Volume volume = new Volume();
	private VolumeSignal volumeSignal = new VolumeSignal();

	private AudioService() {
	}

	private static AudioService getInstance() {
		synchronized (instances) {
			AudioService instance = instances.get(Gdx.app);
			if (instance == null) {
				instance = new AudioService();
				instances.put(Gdx.app, instance);
			}
			return instance;
		}
	}

	public static float getVolume() {
		return getInstance().volume.getVolume();
	}

	public static void setVolume(float newVolume) {
		AudioService instance = getInstance();
		instance.volume.setVolume(newVolume);
		instance.volumeSignal.volumeChanged(instance.volume.getVolume());
	}

	public static boolean addVolumeListener(VolumeListener listener) {
		return getInstance().volumeSignal.addListener(listener);
	}

	public static boolean removeVolumeListener(VolumeListener listener) {
		return getInstance().volumeSignal.removeListener(listener);
	}
}
