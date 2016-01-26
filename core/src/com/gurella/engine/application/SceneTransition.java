package com.gurella.engine.application;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.badlogic.gdx.Gdx;
import com.gurella.engine.scene.Scene;

public class SceneTransition {
	private Scene sourceScene;
	private Scene destinationScene;

	//private Texture background;
	//private Sprite sprite;

	public SceneTransition() {
		//TODO
		/*Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.drawPixel(0, 0);
		background = new Texture(new PixmapTextureData(pixmap, null, true, true));
		sprite = new Sprite(background);
		sprite.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());*/
	}

	public Scene getSourceScene() {
		return sourceScene;
	}

	public Scene getDestinationScene() {
		return destinationScene;
	}

	protected void init(Scene sourceScene, Scene destinationScene) {
		this.sourceScene = sourceScene;
		this.destinationScene = destinationScene;
	}

	public void beforeTransitionOut() {
		Gdx.app.debug("SceneTransition2", "beforeTransitionOut");
	}

	public boolean onTransitionOut() {
		Gdx.app.debug("SceneTransition2", "onTransitionOut");
		return true;
	}

	public void afterTransitionOut() {
		Gdx.app.debug("SceneTransition2", "afterTransitionOut");
	}

	public void beforeTransitionHold() {
		Gdx.app.debug("SceneTransition2", "beforeTransitionHold");
	}

	public boolean onTransitionHold(float initializationProgress) {
		Gdx.app.debug("SceneTransition2", "onTransitionHold progress: " + initializationProgress);
		return true;
	}

	public void afterTransitionHold() {
		Gdx.app.debug("SceneTransition2", "afterTransitionHold");
	}

	public void beforeTransitionIn() {
		Gdx.app.debug("SceneTransition2", "beforeTransitionIn");
	}

	public boolean onTransitionIn() {
		Gdx.app.debug("SceneTransition2", "onTransitionHold");
		return true;
	}

	public void afterTransitionIn() {
		Gdx.app.debug("SceneTransition2", "afterTransitionIn");
	}
	
	public void onTransitionException(Throwable exception) {
		System.out.println("Error");
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		System.out.println(sw.toString());
	}

//	private void fillRectangle() {
//		Gdx.gl20.glEnable(GL20.GL_BLEND);
//		Application.SPRITE_BATCH.begin();
//		sprite.draw(Application.SPRITE_BATCH);
//		Application.SPRITE_BATCH.end();
//	}
}
