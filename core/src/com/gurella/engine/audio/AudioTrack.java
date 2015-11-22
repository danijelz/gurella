package com.gurella.engine.audio;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.gurella.engine.pools.SynchronizedPools;
import com.gurella.engine.signal.Listener1;
import com.gurella.engine.signal.Signal1.Signal1Impl;

public class AudioTrack implements Poolable, Listener1<Float> {
	private AudioChannel audioChannel;
	private AudioClip audioClip;
	public final Signal1Impl<AudioTrack> completitionCallbacks = new Signal1Impl<AudioTrack>();

	private float channelVolume = 1;

	private long handle = -1;
	private long startTimestamp;
	private long trackedActivity;

	private int priority = Integer.MAX_VALUE;
	private final Volume volume = new Volume();
	private final Volume commonVolume = new Volume();
	private final Pan pan = new Pan();
	private final Pitch pitch = new Pitch();
	private boolean looping;
	private boolean paused;

	private StopTrackTask task = new StopTrackTask();

	private AudioTrack() {
	}

	public static AudioTrack newInstance(AudioClip audioClip) {
		return newInstance(AudioChannel.DEFAULT, audioClip);
	}

	public static AudioTrack newInstance(AudioChannel audioChannel, AudioClip audioClip) {
		AudioTrack track = SynchronizedPools.obtain(AudioTrack.class);
		track.audioClip = audioClip;
		AudioChannel nonNullAudioChannel = getNonNullAudioChannel(audioChannel);
		track.audioChannel = nonNullAudioChannel;
		track.setChannelVolume(nonNullAudioChannel.getCommonVolume());
		nonNullAudioChannel.volumeListeners.addListener(track);
		return track;
	}

	private static AudioChannel getNonNullAudioChannel(AudioChannel audioChannel) {
		return audioChannel == null
				? AudioChannel.DEFAULT
				: audioChannel;
	}

	@Override
	public void handle(Float newChannelVolume) {
		setChannelVolume(newChannelVolume);
	}

	private void setChannelVolume(Float newChannelVolume) {
		this.channelVolume = newChannelVolume.floatValue();
		updateVolume();
	}

	private void updateVolume() {
		commonVolume.setVolume(getVolume() * channelVolume);
		if (isPlaying()) {
			audioClip.sound.setVolume(handle, commonVolume.getVolume());
		}
	}

	public void update(float newVolume, float newPan, float newPitch) {
		setVolume(newVolume);
		setPan(newPan);
		setPitch(newPitch);
	}

	public float getCommonVolume() {
		return commonVolume.getVolume();
	}

	public void setPitch(float newPitch) {
		task.cancel();

		if (isPlaying()) {
			updateActivity();
			pitch.setPitch(newPitch);
			audioClip.sound.setPitch(handle, pitch.getPitch());

			if (!looping) {
				scheduleTask();
			}
		}
	}

	private void updateActivity() {
		float currentPitch = getPitch();
		long newStartTimestamp = System.currentTimeMillis();
		long passedTime = newStartTimestamp - startTimestamp;
		startTimestamp = newStartTimestamp;
		trackedActivity += (long) (passedTime * currentPitch);
	}

	private void scheduleTask() {
		long durationMilliseconds = audioClip.durationMilliseconds;
		long millisTillEnd = durationMilliseconds - (trackedActivity % durationMilliseconds);
		Timer.schedule(task, millisTillEnd / getPitch() / 1000);
	}

	public float getPitch() {
		return pitch.getPitch();
	}

	public void setVolume(float newVolume) {
		volume.setVolume(newVolume);
		updateVolume();
	}

	public float getVolume() {
		return volume.getVolume();
	}

	public void setPan(float newPan) {
		pan.setPan(newPan);
		if (isPlaying()) {
			audioClip.sound.setPan(handle, pitch.getPitch(), getCommonVolume());
		}
	}

	public float getPan() {
		return pan.getPan();
	}

	public void setPriority(int priority) {
		this.priority = priority;
		if (isPlaying()) {
			audioClip.sound.setPriority(handle, priority);
		}
	}

	public int getPriority() {
		return priority;
	}

	public void play() {
		if (isPlaying()) {
			setLooping(false);
		} else {
			looping = false;
			handle = audioClip.sound.play(getCommonVolume(), getPitch(), getPan());
			startTimestamp = System.currentTimeMillis();
			scheduleTask();
		}
	}

	private void setLooping(boolean looping) {
		if (this.looping != looping) {
			this.looping = looping;
			audioClip.sound.setLooping(handle, looping);
			if (!looping) {
				scheduleTask();
			}
		}
	}

	public void loop() {
		if (isPlaying()) {
			setLooping(true);
		} else {
			looping = true;
			handle = audioClip.sound.loop(getCommonVolume(), getPitch(), getPan());
			startTimestamp = System.currentTimeMillis();
		}
	}

	public void stop() {
		if (isPlaying()) {
			task.cancel();
			paused = false;
			looping = false;
			audioClip.sound.stop(handle);
			handle = -1;
			completitionCallbacks.dispatch(AudioTrack.this);
		}
	}

	public void pause() {
		if (isPlaying()) {
			task.cancel();
			updateActivity();
			paused = true;
			audioClip.sound.pause(handle);
		}
	}

	public void resume() {
		if (isPlaying() && isPaused()) {
			paused = false;
			audioClip.sound.resume(handle);
			startTimestamp = System.currentTimeMillis();
			scheduleTask();
		}
	}

	public boolean isPlaying() {
		return handle > -1;
	}

	public boolean isPaused() {
		return paused && isPlaying();
	}

	public boolean isLooping() {
		return looping;
	}

	public long getHandle() {
		return handle;
	}

	public AudioChannel getAudioChannel() {
		return audioChannel;
	}

	public void free() {
		SynchronizedPools.free(this);
	}

	@Override
	public void reset() {
		audioChannel.volumeListeners.removeListener(this);
		audioChannel = null;
		audioClip = null;
		channelVolume = 1;
		handle = -1;
		startTimestamp = 0;
		trackedActivity = 0;
		priority = Integer.MAX_VALUE;
		volume.setVolume(1);
		pan.setPan(0);
		pitch.setPitch(1);
		looping = false;
		paused = false;
		completitionCallbacks.clear();
	}

	private class StopTrackTask extends Task {
		@Override
		public void run() {
			stop();
		}
	}
}
