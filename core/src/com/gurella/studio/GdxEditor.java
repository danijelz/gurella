package com.gurella.studio;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.gurella.engine.event.EventBus;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.signal.Listener1;
import com.gurella.studio.project.ProjectHeaderContainer.SceneSelectionChangedEvent;
import com.kotcrab.vis.ui.VisUI;

public class GdxEditor extends Game {
	public static Skin skin;
	public static SpriteBatch batch;
	
	public static Scene scene;
	
	private Screen screen;
	
	@Override
	public void create() {
		VisUI.load();
		EventBus.GLOBAL.addListener(SceneSelectionChangedEvent.class, new SceneSelectionChangedListener());
		skin = new Skin(Gdx.files.internal("editorui/uiskin.json"));
		batch = new SpriteBatch();
		screen = new EditorScreen();
		setScreen(screen);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		VisUI.dispose();
	}
	
	private class SceneSelectionChangedListener implements Listener1<Scene> {
		@Override
		public void handle(Scene selectedScene) {
			scene = selectedScene;
		}
	}
}
