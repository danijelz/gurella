package com.gurella.engine.application;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.GraphicsService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.subscriptions.application.ApplicationActivityListener;
import com.gurella.engine.subscriptions.application.ApplicationResizeListener;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;

public final class Application implements ApplicationListener {
	private static final PauseEvent pauseEvent = new PauseEvent();
	private static final ResumeEvent resumeEvent = new ResumeEvent();
	private static final UpdateEvent updateEvent = new UpdateEvent();
	private static final ResizeEvent resizeEvent = new ResizeEvent();

	private static boolean initialized;

	public static float deltaTime;
	private static boolean paused;

	private static ApplicationConfig config;
	private static String initialScenePath;
	private static final SceneManager sceneManager = new SceneManager(null);

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
		resizeEvent.width = width;
		resizeEvent.height = height;
		EventService.notify(resizeEvent, null);
	}

	@Override
	public final void render() {
		deltaTime = Gdx.graphics.getDeltaTime();
		// TODO clear must be handled by RenderSystem with spec from camera
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		EventService.notify(updateEvent, null);
	}

	@Override
	public final void pause() {
		paused = true;
		EventService.notify(pauseEvent, null);
	}

	@Override
	public final void resume() {
		paused = false;
		if (Gdx.app.getType() == ApplicationType.Android) {
			AssetService.reloadInvalidated();
		}
		EventService.notify(resumeEvent, null);
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
		EventService.notify(new ApplicationShutdownEvent(), null);
		// TODO sceneManager.stop();
		DisposablesService.disposeAll();
	}

	private static class ApplicationShutdownEvent implements Event<ApplicationShutdownListener, Void> {
		@Override
		public Class<ApplicationShutdownListener> getSubscriptionType() {
			return ApplicationShutdownListener.class;
		}

		@Override
		public void notify(ApplicationShutdownListener listener, Void data) {
			listener.shutdown();
		}
	}

	private static class PauseEvent implements Event<ApplicationActivityListener, Void> {
		@Override
		public Class<ApplicationActivityListener> getSubscriptionType() {
			return ApplicationActivityListener.class;
		}

		@Override
		public void notify(ApplicationActivityListener listener, Void data) {
			listener.pause();
		}
	}

	private static class ResumeEvent implements Event<ApplicationActivityListener, Void> {
		@Override
		public Class<ApplicationActivityListener> getSubscriptionType() {
			return ApplicationActivityListener.class;
		}

		@Override
		public void notify(ApplicationActivityListener listener, Void data) {
			listener.resume();
		}
	}

	private static class UpdateEvent implements Event<ApplicationUpdateListener, Void> {
		@Override
		public Class<ApplicationUpdateListener> getSubscriptionType() {
			return ApplicationUpdateListener.class;
		}

		@Override
		public void notify(ApplicationUpdateListener listener, Void data) {
			listener.update();
		}
	}

	private static class ResizeEvent implements Event<ApplicationResizeListener, Void> {
		int width;
		int height;

		@Override
		public Class<ApplicationResizeListener> getSubscriptionType() {
			return ApplicationResizeListener.class;
		}

		@Override
		public void notify(ApplicationResizeListener listener, Void data) {
			listener.resize(width, height);
		}
	}
}
