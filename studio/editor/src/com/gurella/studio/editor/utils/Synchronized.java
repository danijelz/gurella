package com.gurella.studio.editor.utils;

public class Synchronized {
	private Synchronized() {
	}

	public static void run(Object mutex, Runnable action) {
		synchronized (mutex) {
			action.run();
		}
	}
}
