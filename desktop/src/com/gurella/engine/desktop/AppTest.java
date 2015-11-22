package com.gurella.engine.desktop;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.application2.Application;

public class AppTest {
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Gdx Editor";
		cfg.useGL30 = false;
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		cfg.width = 800;
		cfg.height = 600;
		cfg.initialBackgroundColor = Color.BLACK;

		new LwjglApplication(Application.fromJson("project.json"), cfg);
	}
}
