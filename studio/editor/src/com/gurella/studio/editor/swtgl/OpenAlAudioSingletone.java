package com.gurella.studio.editor.swtgl;

import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio;

class OpenAlAudioSingletone {
	private static final int simultaneousSources = 16;
	private static final int bufferCount = 9;
	private static final int bufferSize = 512;

	private static int refCount;
	private static OpenALAudio instance;
	private static final Object mutex = new Object();

	private OpenAlAudioSingletone() {
	}

	static OpenALAudio getInstance() {
		synchronized (mutex) {
			if (SwtApplicationConfig.disableAudio) {
				return null;
			}

			refCount++;
			if (instance == null) {
				instance = new OpenALAudio(simultaneousSources, bufferCount, bufferSize);
			}

			return instance;
		}
	}

	static void dispose(OpenALAudio audio) {
		synchronized (mutex) {
			if (audio == instance) {
				refCount--;
				if (refCount < 1) {
					instance.dispose();
					instance = null;
				}
			}
		}
	}
}
