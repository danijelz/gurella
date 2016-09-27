package com.gurella.engine;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.gurella.engine.application.GurellaStateProvider;

public class GurellaEngine {
	private GurellaEngine() {
	}

	public static boolean isInRenderThread() {
		ApplicationListener listener = Gdx.app.getApplicationListener();
		if (listener instanceof GurellaStateProvider) {
			return ((GurellaStateProvider) listener).isInRenderThread();
		} else {
			return true;
		}
	}
}
