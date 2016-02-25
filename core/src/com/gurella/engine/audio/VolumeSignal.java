package com.gurella.engine.audio;

import com.gurella.engine.event.Signal;

public class VolumeSignal extends Signal<VolumeListener> {
	public void volumeChanged(float volume) {
		VolumeListener[] items = listeners.begin();
		for (int i = 0, n = items.length; i < n; i++) {
			VolumeListener item = items[i];
			item.volumeChanged(volume);
		}
		listeners.end();
	}
}
