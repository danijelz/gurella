package com.gurella.studio.launch;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.application.GurellaApplication;
import com.gurella.engine.application.ApplicationConfig;

public class LaunchSceneApplication {
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Gdx Editor";
		cfg.useGL30 = true;

		cfg.width = 800;
		cfg.height = 600;
		cfg.initialBackgroundColor = Color.BLACK;

		String initialScenePath = System.getProperty("gurellaDebugScene");
		System.out.println("Starting scene: " + initialScenePath);
		new LaunchSceneLwjglApplication(new GurellaApplication(new ApplicationConfig(initialScenePath)), cfg);
	}

	private static class LaunchSceneLwjglApplication extends LwjglApplication {
		private int sceneLogLevel = LOG_DEBUG;

		public LaunchSceneLwjglApplication(ApplicationListener listener, LwjglApplicationConfiguration config) {
			super(listener, config);
		}

		@Override
		public int getLogLevel() {
			return sceneLogLevel;
		}

		@Override
		public void setLogLevel(int logLevel) {
			this.sceneLogLevel = logLevel;
		}

		@Override
		public void debug(String tag, String message) {
			if (sceneLogLevel >= LOG_DEBUG) {
				getApplicationLogger().debug(tag, message);
			}
		}

		@Override
		public void debug(String tag, String message, Throwable exception) {
			if (sceneLogLevel >= LOG_DEBUG) {
				getApplicationLogger().debug(tag, message, exception);
			}
		}

		@Override
		public void log(String tag, String message) {
			if (sceneLogLevel >= LOG_INFO) {
				getApplicationLogger().log(tag, message);
			}
		}

		@Override
		public void log(String tag, String message, Throwable exception) {
			if (sceneLogLevel >= LOG_INFO) {
				getApplicationLogger().log(tag, message, exception);
			}
		}

		@Override
		public void error(String tag, String message) {
			if (sceneLogLevel >= LOG_ERROR) {
				getApplicationLogger().error(tag, message);
			}
		}

		@Override
		public void error(String tag, String message, Throwable exception) {
			if (sceneLogLevel >= LOG_ERROR) {
				getApplicationLogger().error(tag, message, exception);
			}
		}
	}
}
