package com.gurella.engine.audio.loader;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

public class SoundClip implements Disposable {
	public final Sound sound;
	public final float duration;
	
	public SoundClip(Sound sound, float duration) {
		this.sound = sound;
		this.duration = duration;
	}

	@Override
	public void dispose() {
		sound.dispose();
	}
}
