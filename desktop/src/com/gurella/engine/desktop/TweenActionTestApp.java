package com.gurella.engine.desktop;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.gurella.engine.scene.action.PropertiesAccessor;
import com.gurella.engine.scene.action.TweenAction;

public class TweenActionTestApp {
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Gdx Editor";
		cfg.useGL30 = false;

		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		cfg.width = 800;
		cfg.height = 600;
		cfg.initialBackgroundColor = Color.BLACK;

		new LwjglApplication(new TestApplicationListener(), cfg);
	}

	private static class TestApplicationListener extends ApplicationAdapter {
		TestTweenAction action;

		@Override
		public void create() {
			action = new TestTweenAction(0.3f, Interpolation.linear,
					new PropertiesAccessor<Color>(new Color(), Color.BLACK, Color.WHITE));
		}

		@Override
		public void render() {
			super.render();
			if (!action.isComplete()) {
				action.act(Gdx.graphics.getDeltaTime());
			}
		}
	}

	private static class TestTweenAction extends TweenAction {
		private PropertiesAccessor<Color> accessor;

		public TestTweenAction(float duration, Interpolation interpolation, PropertiesAccessor<Color> accessor) {
			super(duration, interpolation);
			this.accessor = accessor;
		}

		@Override
		protected void update(float percent) {
			accessor.update(percent);
			System.out.println(accessor.getDiagnostics());
		}

	}
}
