package com.gurella.studio.editor.launch;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.application.Application;
import com.gurella.engine.application.ApplicationConfig;

public class LaunchSceneApplication {
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Gdx Editor";
		cfg.useGL30 = false;

		cfg.width = 800;
		cfg.height = 600;
		cfg.initialBackgroundColor = Color.BLACK;
		
		String initialScenePath = System.getProperty("gurellaDebugScene");
		System.out.println(initialScenePath);
		new LwjglApplication(new Application(new ApplicationConfig(initialScenePath)), cfg);
	}
}
