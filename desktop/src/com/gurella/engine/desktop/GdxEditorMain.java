package com.gurella.engine.desktop;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.gurella.studio.GdxEditor;

public class GdxEditorMain {
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Gdx Editor";
		cfg.useGL30 = false;
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		cfg.width = gd.getDisplayMode().getWidth();
		cfg.height = gd.getDisplayMode().getHeight() - 80;
		cfg.x = 0;
		cfg.y = 0;
		cfg.initialBackgroundColor = Color.BLACK;

		new LwjglApplication(new GdxEditor(), cfg);
	}
}
