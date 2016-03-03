package com.gurella.engine.scene.audio;

import com.gurella.engine.audio.AudioChannel;

public enum SceneAudioChannel {
	AMBIENT, GAME, HUD;

	private AudioChannel audioChannel;

	private SceneAudioChannel() {
		this.audioChannel = AudioChannel.newInstance();
	}

	AudioChannel getAudioChannel() {
		return audioChannel;
	}
}
