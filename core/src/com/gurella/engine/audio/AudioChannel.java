package com.gurella.engine.audio;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.utils.SynchronizedPools;

public class AudioChannel implements Poolable, VolumeListener {
	static final AudioChannel DEFAULT = newInstance();

	private final Volume volume = new Volume();
	private final Volume commonVolume = new Volume();

	private VolumeSignal volumeSignal = new VolumeSignal();

	private float masterVolume;

	private AudioChannel() {
	}

	public static AudioChannel newInstance() {
		AudioChannel audioChannel = SynchronizedPools.obtain(AudioChannel.class);
		AudioService.addVolumeListener(audioChannel);
		audioChannel.masterVolume = AudioService.getVolume();
		audioChannel.updateVolume();
		return audioChannel;
	}

	public float getVolume() {
		return volume.getVolume();
	}

	public void setVolume(float newVolume) {
		volume.setVolume(newVolume);
		updateVolume();
	}

	private void updateVolume() {
		commonVolume.setVolume(getVolume() * masterVolume);
		volumeSignal.volumeChanged(commonVolume.getVolume());
	}

	public float getCommonVolume() {
		return commonVolume.getVolume();
	}

	@Override
	public void volumeChanged(float newMasterVolume) {
		this.masterVolume = newMasterVolume;
		updateVolume();
	}

	public boolean addVolumeListener(VolumeListener listener) {
		return volumeSignal.addListener(listener);
	}

	public boolean removeVolumeListener(VolumeListener listener) {
		return volumeSignal.removeListener(listener);
	}

	public void free() {
		SynchronizedPools.free(this);
	}

	@Override
	public void reset() {
		volume.setVolume(1);
		volumeSignal.clear();
		AudioService.removeVolumeListener(this);
	}
}
