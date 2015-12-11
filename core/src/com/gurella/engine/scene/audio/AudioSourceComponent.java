package com.gurella.engine.scene.audio;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.audio.AudioChannel;
import com.gurella.engine.audio.AudioClip;
import com.gurella.engine.audio.AudioTrack;
import com.gurella.engine.audio.Pan;
import com.gurella.engine.audio.Pitch;
import com.gurella.engine.audio.Volume;
import com.gurella.engine.event.Listener1;
import com.gurella.engine.geometry.Angle;
import com.gurella.engine.resource.model.DefaultValue;
import com.gurella.engine.resource.model.PropertyValue;
import com.gurella.engine.resource.model.ResourceProperty;
import com.gurella.engine.scene.SceneNodeComponent;

public class AudioSourceComponent extends SceneNodeComponent {
	AudioSystem audioProcessor;
	public AudioClip audioClip;

	@DefaultValue(booleanValue = true)
	public boolean spatial = true;
	public boolean repeatable;
	@DefaultValue(integerValue = Integer.MAX_VALUE)
	public int priority = Integer.MAX_VALUE;
	@DefaultValue(enumOrdinal = 3)
	public Attenuation attenuation = Attenuation.ROLLOFF;
	@ResourceProperty
	public final Volume volume = new Volume();
	@ResourceProperty
	public final Pan pan = new Pan();
	@ResourceProperty
	public final Pitch pitch = new Pitch();
	@DefaultValue(floatValue = 0.5f)
	public float dopplerFactor = 0.5f;
	@DefaultValue(floatValue = 1.0f)
	public float dopplerVelocity = 1.0f;
	@DefaultValue(floatValue = 0.3f)
	public float rollOff = 0.3f;
	@DefaultValue(floatValue = 2.0f)
	public float referenceDistance = 2.0f;
	@DefaultValue(floatValue = 20.0f)
	public float maxDistance = 20.0f;
	@ResourceProperty
	@DefaultValue(compositeValues = {@PropertyValue(name = "degrees", floatValue = 360)})
	public final Angle innerConeAngle = Angle.getFromDegrees(360);
	@ResourceProperty
	@DefaultValue(compositeValues = {@PropertyValue(name = "degrees", floatValue = 360)})
	public final Angle outerConeAngle = Angle.getFromDegrees(360);
	@ResourceProperty
	public final Volume outerConeVolume = new Volume();
	@ResourceProperty
	@DefaultValue(compositeValues = {@PropertyValue(name = "y", floatValue = 1)})
	public final Vector3 up = new Vector3(0, 1, 0);
	@ResourceProperty
	@DefaultValue(compositeValues = {@PropertyValue(name = "z", floatValue = -1)})
	public final Vector3 lookAt = new Vector3(0, 0, -1);

	public AudioTrack play() {
		return play(GraphAudioChannel.GAME.getAudioChannel(), null);
	}

	public AudioTrack play(GraphAudioChannel graphAudioChannel) {
		return play(graphAudioChannel.getAudioChannel(), null);
	}

	public AudioTrack play(AudioChannel audioChannel) {
		return play(audioChannel, null);
	}

	public AudioTrack play(Listener1<AudioTrack> completitionCallback) {
		return play(GraphAudioChannel.GAME.getAudioChannel(), completitionCallback);
	}

	public AudioTrack play(GraphAudioChannel graphAudioChannel, Listener1<AudioTrack> completitionCallback) {
		return play(graphAudioChannel.getAudioChannel(), completitionCallback);
	}

	public AudioTrack play(AudioChannel audioChannel, Listener1<AudioTrack> completitionCallback) {
		if (audioProcessor == null) {
			return null;
		}
		return audioProcessor.play(this, audioChannel, completitionCallback);
	}

	public void playOnce() {
		playOnce(GraphAudioChannel.GAME.getAudioChannel(), null);
	}

	public void playOnce(GraphAudioChannel graphAudioChannel) {
		playOnce(graphAudioChannel.getAudioChannel(), null);
	}

	public void playOnce(AudioChannel audioChannel, Listener1<AudioTrack> completitionCallback) {
		if (audioProcessor == null) {
			return;
		}
		audioProcessor.playOnce(this, audioChannel, completitionCallback);
	}

	public AudioTrack loop() {
		return loop(GraphAudioChannel.GAME.getAudioChannel(), null);
	}

	public AudioTrack loop(GraphAudioChannel graphAudioChannel) {
		return loop(graphAudioChannel.getAudioChannel(), null);
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
}
