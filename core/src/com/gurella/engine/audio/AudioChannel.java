package com.gurella.engine.audio;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pools.SynchronizedPools;
import com.gurella.engine.signal.Listener1;
import com.gurella.engine.signal.Signal1.Signal1Impl;

public class AudioChannel implements Poolable, Listener1<Float> {
	static final AudioChannel DEFAULT = newInstance();

	private final Volume volume = new Volume();
	private final Volume commonVolume = new Volume();

	final Signal1Impl<Float> volumeListeners = new Signal1Impl<Float>();

	private float masterVolume;

	private AudioChannel() {
	}

	public static AudioChannel newInstance() {
		AudioChannel audioChannel = SynchronizedPools.obtain(AudioChannel.class);
		AudioService.volumeListeners.addListener(audioChannel);
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
		volumeListeners.dispatch(Float.valueOf(commonVolume.getVolume()));
	}

	public float getCommonVolume() {
		return commonVolume.getVolume();
	}

	@Override
	public void handle(Float newMasterVolume) {
		this.masterVolume = newMasterVolume.floatValue();
		updateVolume();
	}

	public void free() {
		SynchronizedPools.free(this);
	}

	@Override
	public void reset() {
		volume.setVolume(1);
		volumeListeners.clear();
		AudioService.volumeListeners.removeListener(this);
	}
}
