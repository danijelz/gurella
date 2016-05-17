package com.gurella.studio.editor.swtgl;

import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio;

class OpenAlAudioSingletone {
	private static int refCount;
	private static OpenALAudio instance;
	private static final Object mutex = new Object();

	private OpenAlAudioSingletone() {
	}

	static OpenALAudio getInstance(SwtApplicationConfig config) {
		synchronized (mutex) {
			if (SwtApplicationConfig.disableAudio) {
				return null;
			}

			refCount++;
			if (instance == null) {
				int simultaneousSources = config.audioDeviceSimultaneousSources;
				int bufferCount = config.audioDeviceBufferCount;
				int bufferSize = config.audioDeviceBufferSize;
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
