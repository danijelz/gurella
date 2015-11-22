package com.gurella.engine.graph.audio;

import com.gurella.engine.audio.AudioChannel;

public enum GraphAudioChannel {
	AMBIENT, GAME, HUD;

	private AudioChannel audioChannel;

	private GraphAudioChannel() {
		this.audioChannel = AudioChannel.newInstance();
	}

	AudioChannel getAudioChannel() {
		return audioChannel;
	}
}
