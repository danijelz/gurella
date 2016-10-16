package com.gurella.engine.utils;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class Exceptions {
	private Exceptions() {
	}

	public static void rethrowAsRuntime(Exception e) {
		throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
	}

	public static void rethrowAsGdxRuntime(Exception e) {
		throw e instanceof RuntimeException ? (RuntimeException) e : new GdxRuntimeException(e);
	}
}
