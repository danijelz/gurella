package com.gurella.engine.scene.audio;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.audio.AudioChannel;
import com.gurella.engine.audio.AudioClip;
import com.gurella.engine.audio.AudioTrack;
import com.gurella.engine.audio.Pan;
import com.gurella.engine.audio.Pitch;
import com.gurella.engine.audio.Volume;
import com.gurella.engine.event.Listener1;
import com.gurella.engine.math.geometry.Angle;
import com.gurella.engine.scene.SceneNodeComponent2;

public class AudioSourceComponent extends SceneNodeComponent2 implements Poolable {
	transient AudioSystem audioProcessor;
	public AudioClip audioClip;

	public boolean spatial = true;
	public boolean repeatable;
	public Attenuation attenuation = Attenuation.ROLLOFF;
	public final Volume volume = new Volume();
	public final Pan pan = new Pan();
	public final Pitch pitch = new Pitch();
	public float dopplerFactor = 0.5f;
	public float dopplerVelocity = 1.0f;
	public float rollOff = 0.3f;
	public float referenceDistance = 2.0f;
	public float maxDistance = 20.0f;
	public final Angle innerConeAngle = Angle.getFromDegrees(360);
	public final Angle outerConeAngle = Angle.getFromDegrees(360);
	public final Volume outerConeVolume = new Volume();
	public final Vector3 up = new Vector3(0, 1, 0);
	public final Vector3 lookAt = new Vector3(0, 0, -1);

	public AudioTrack play() {
		return play(SceneAudioChannel.GAME.getAudioChannel(), null);
	}

	public AudioTrack play(SceneAudioChannel sceneAudioChannel) {
		return play(sceneAudioChannel.getAudioChannel(), null);
	}

	public AudioTrack play(AudioChannel audioChannel) {
		return play(audioChannel, null);
	}

	public AudioTrack play(Listener1<AudioTrack> completitionCallback) {
		return play(SceneAudioChannel.GAME.getAudioChannel(), completitionCallback);
	}

	public AudioTrack play(SceneAudioChannel sceneAudioChannel, Listener1<AudioTrack> completitionCallback) {
		return play(sceneAudioChannel.getAudioChannel(), completitionCallback);
	}

	public AudioTrack play(AudioChannel audioChannel, Listener1<AudioTrack> completitionCallback) {
		if (audioProcessor == null) {
			return null;
		}
		return audioProcessor.play(this, audioChannel, completitionCallback);
	}

	public void playOnce() {
		playOnce(SceneAudioChannel.GAME.getAudioChannel(), null);
	}

	public void playOnce(SceneAudioChannel sceneAudioChannel) {
		playOnce(sceneAudioChannel.getAudioChannel(), null);
	}

	public void playOnce(AudioChannel audioChannel, Listener1<AudioTrack> completitionCallback) {
		if (audioProcessor == null) {
			return;
		}
		audioProcessor.playOnce(this, audioChannel, completitionCallback);
	}

	public AudioTrack loop() {
		return loop(SceneAudioChannel.GAME.getAudioChannel(), null);
	}

	public AudioTrack loop(SceneAudioChannel sceneAudioChannel) {
		return loop(sceneAudioChannel.getAudioChannel(), null);
	}

	public AudioTrack loop(AudioChannel audioChannel, Listener1<AudioTrack> completitionCallback) {
		if (audioProcessor == null) {
			return null;
		}
		return audioProcessor.loop(this, audioChannel, completitionCallback);
	}

	public void free(AudioTrack track) {
		if (audioProcessor != null) {
			audioProcessor.free(this, track);
		}
	}

	@Override
	public void reset() {
		audioProcessor = null;
		audioClip = null;
		spatial = true;
		repeatable = false;
		attenuation = Attenuation.ROLLOFF;
		volume.reset();
		pan.reset();
		pitch.reset();
		dopplerFactor = 0.5f;
		dopplerVelocity = 1.0f;
		rollOff = 0.3f;
		referenceDistance = 2.0f;
		maxDistance = 20.0f;
		innerConeAngle.setDegrees(360);
		outerConeAngle.setDegrees(360);
		outerConeVolume.reset();
		up.set(0, 1, 0);
		lookAt.set(0, 0, -1);
	}
}
