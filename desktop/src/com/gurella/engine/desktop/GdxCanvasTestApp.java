package com.gurella.engine.desktop;

import java.io.File;
import java.net.URL;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gurella.engine.desktop.gdxcanvastest.CanvasTestCase;
import com.gurella.engine.graphics.vector.Canvas;
import com.gurella.engine.graphics.vector.CanvasFlags.CanvasFlag;
import com.gurella.engine.utils.ReflectionUtils;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisSelectBox;

public class GdxCanvasTestApp {
	private static final String TEST_CLASSES_PACKAGE = "com.gurella.engine.desktop.gdxcanvastest";
	private static final String TEST_CLASSES_RELATIVE_PATH = TEST_CLASSES_PACKAGE.replace('.', '/');

	public static void main(String args[]) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "psychler1";
		cfg.useGL30 = false;
		cfg.width = 1200;
		cfg.height = 800;
		@SuppressWarnings("unused")
		LwjglApplication lwjglApplication = new LwjglApplication(new TestApp(), cfg);
	}

	private static class TestApp extends ApplicationAdapter {
		private Stage stage;
		private ScreenViewport screenViewport;
		private VisSelectBox<TestItem> testItems;
		private boolean uiVisible = true;
		private CanvasTestCase selectedTestCase;
		private Canvas canvas;

		@Override
		public void create() {
			canvas = Canvas.obtain(CanvasFlag.debug, CanvasFlag.antiAlias, CanvasFlag.stencilStrokes);

			screenViewport = new ScreenViewport();
			stage = new Stage(screenViewport) {
				@Override
				public boolean keyDown(int keyCode) {
					if (Keys.SPACE == keyCode) {
						uiVisible = !uiVisible;
						testItems.setVisible(uiVisible);
						return true;
					} else {
						return super.keyDown(keyCode);
					}
				}
			};

			Gdx.input.setInputProcessor(stage);
			VisUI.load();

			testItems = new VisSelectBox<TestItem>();
			testItems.setBounds(30, screenViewport.getScreenHeight() - 50, 350, 30);
			testItems.setDisabled(false);
			testItems.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					testItemSelected();
				}
			});

			Array<TestItem> listItems = createTestItems();
			testItems.setItems(listItems);
			stage.addActor(testItems);

			testItemSelected();
		}

		private static Array<TestItem> createTestItems() {
			Array<TestItem> listItems = new Array<TestItem>();
			for (Class<? extends CanvasTestCase> testCase : findTestClasses()) {
				listItems.add(new TestItem(testCase));
			}

			listItems.sort();
			return listItems;
		}

		private static Array<Class<? extends CanvasTestCase>> findTestClasses() {
			Array<Class<? extends CanvasTestCase>> classes = new Array<Class<? extends CanvasTestCase>>();
			URL resource = ClassLoader.getSystemClassLoader().getResource(TEST_CLASSES_RELATIVE_PATH);
			File file = new File(resource.getPath());

			String[] files = file.list();

			for (int i = 0; i < files.length; i++) {
				String fileName = files[i];

				if (!fileName.endsWith(".class")) {
					continue;
				}

				String className = TEST_CLASSES_PACKAGE + '.' + fileName.substring(0, fileName.length() - 6);
				Class<?> packageClass = ReflectionUtils.forName(className);
				if (CanvasTestCase.class.isAssignableFrom(packageClass)) {
					@SuppressWarnings("unchecked")
					Class<? extends CanvasTestCase> casted = (Class<? extends CanvasTestCase>) packageClass;
					classes.add(casted);
				}
			}

			return classes;
		}

		@Override
		public void dispose() {
			VisUI.dispose();
			stage.dispose();
			canvas.dispose();
		}

		@Override
		public void render() {
			Gdx.gl.glClearColor(1, 1, 1, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
			canvas.clear();
			if (selectedTestCase != null) {
				selectedTestCase.render(canvas);
				canvas.render();
			}
			stage.act(Gdx.graphics.getDeltaTime());
			stage.draw();
		}

		@Override
		public void resize(int width, int height) {
			screenViewport.setScreenSize(width, height);
		}

		@Override
		public void pause() {
		}

		@Override
		public void resume() {
		}

		protected void testItemSelected() {
			TestItem selected = testItems.getSelected();
			try {
				selectedTestCase = selected == null ? null : selected.testClass.newInstance();
			} catch (InstantiationException e) {
				selectedTestCase = null;
			} catch (IllegalAccessException e) {
				selectedTestCase = null;
			}
		}
	}

	private static class TestItem implements Comparable<TestItem> {
		Class<? extends CanvasTestCase> testClass;

		public TestItem(Class<? extends CanvasTestCase> testClass) {
			this.testClass = testClass;
		}

		@Override
		public String toString() {
			return testClass.getSimpleName();
		}

		@Override
		public int compareTo(TestItem o) {
			return toString().compareTo(o.toString());
		}
	}
}
