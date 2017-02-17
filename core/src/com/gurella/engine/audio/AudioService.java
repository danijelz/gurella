package com.gurella.engine.audio;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.async.AsyncService;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;

public class AudioService {
	private static final ObjectMap<Application, AudioService> instances = new ObjectMap<Application, AudioService>();

	private static AudioService lastSelected;
	private static Application lastApp;

	private final Volume volume = new Volume();
	private final VolumeSignal volumeSignal = new VolumeSignal();

	private AudioService() {
	}

	private static AudioService getInstance() {
		AudioService instance;
		boolean subscribe = false;

		synchronized (instances) {
			Application app = AsyncService.getCurrentApplication();
			if (lastApp == app) {
				return lastSelected;
			}

			instance = instances.get(app);
			if (instance == null) {
				instance = new AudioService();
				instances.put(app, instance);
				subscribe = true;
			}

			lastApp = app;
			lastSelected = instance;

		}

		if (subscribe) {
			EventService.subscribe(new Cleaner());
		}

		return instance;
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

	private static class Cleaner implements ApplicationShutdownListener {
		@Override
		public void shutdown() {
			EventService.unsubscribe(this);

			synchronized (instances) {
				if (instances.remove(AsyncService.getCurrentApplication()) == lastSelected) {
					lastSelected = null;
					lastApp = null;
				}
			}
		}
	}
}
