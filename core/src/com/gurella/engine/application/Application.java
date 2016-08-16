package com.gurella.engine.application;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.GraphicsService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.subscriptions.application.ApplicationActivityListener;
import com.gurella.engine.subscriptions.application.ApplicationResizeListener;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.utils.Values;

public final class Application implements ApplicationListener {
	private static boolean initialized;

	public static float deltaTime;
	private static boolean paused;

	private static ApplicationConfig config;
	private static String initialScenePath;
	private static final SceneManager sceneManager = new SceneManager(null);

	private static final Array<Object> tempListeners = new Array<Object>(64);

	Application(ApplicationConfig config) {
		this.config = config;
	}

	@Override
	public final void create() {
		if (initialized) {
			throw new GdxRuntimeException("Application already initialized.");
		}

		// TODO create services by checking if this is studio
		Gdx.app.setLogLevel(com.badlogic.gdx.Application.LOG_DEBUG);
		//TODO config.init(this);
		// TODO add init scripts to initializer
		GraphicsService.init();
		sceneManager.showScene(initialScenePath);
	}

	@Override
	public final void resize(int width, int height) {
		Array<ApplicationResizeListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(ApplicationResizeListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).resize(width, height);
		}
	}

	@Override
	public final void render() {
		deltaTime = Gdx.graphics.getDeltaTime();
		// TODO clear must be handled by RenderSystem with spec from camera
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Array<ApplicationUpdateListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(ApplicationUpdateListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).update();
		}
	}

	@Override
	public final void pause() {
		paused = true;
		Array<ApplicationActivityListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(ApplicationActivityListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).pause();
		}
	}

	@Override
	public final void resume() {
		paused = false;
		if (Gdx.app.getType() == ApplicationType.Android) {
			AssetService.reloadInvalidated();
		}
		Array<ApplicationActivityListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(ApplicationActivityListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).resume();
		}
	}

	public final boolean isPaused() {
		return paused;
	}

	public IntMap<Scene> getScenes() {
		return sceneManager.getScenes();
	}

	public Scene getCurrentScene() {
		return sceneManager.getCurrentScene();
	}

	public String getCurrentSceneGroup() {
		return sceneManager.getCurrentSceneGroup();
	}

	@Override
	public void dispose() {
		Array<ApplicationShutdownListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(ApplicationShutdownListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).shutdown();
		}
		// TODO sceneManager.stop();
		DisposablesService.disposeAll();
	}
}
