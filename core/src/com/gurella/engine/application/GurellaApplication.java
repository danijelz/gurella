package com.gurella.engine.application;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.async.AsyncService;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.GraphicsService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.subscriptions.application.ApplicationActivityListener;
import com.gurella.engine.subscriptions.application.ApplicationCleanupListener;
import com.gurella.engine.subscriptions.application.ApplicationResizeListener;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.utils.Sequence;
import com.gurella.engine.utils.plugin.Workbench;

public final class GurellaApplication implements ApplicationListener {
	private static final String defaultConfigLocation = "application.gcfg";

	private static final PauseEvent pauseEvent = new PauseEvent();
	private static final ResumeEvent resumeEvent = new ResumeEvent();
	private static final UpdateEvent updateEvent = new UpdateEvent();
	private static final CleanupEvent cleanupEvent = new CleanupEvent();
	private static final ResizeEvent resizeEvent = new ResizeEvent();
	private static final ShutdownEvent shutdownEvent = new ShutdownEvent();

	public final int id = Sequence.next();
	private final Workbench workbench = Workbench.newInstance(id);

	private String configLocation;
	private ApplicationConfig config;

	private final SceneManager sceneManager = new SceneManager();

	private boolean paused;

	public GurellaApplication() {
		this.configLocation = defaultConfigLocation;
	}

	public GurellaApplication(ApplicationConfig config) {
		this.config = config;
	}

	public GurellaApplication(String configLocation) {
		this.configLocation = configLocation;
	}

	public static GurellaApplication current() {
		ApplicationListener applicationListener = AsyncService.getCurrentApplication().getApplicationListener();
		return applicationListener instanceof GurellaApplication ? (GurellaApplication) applicationListener : null;
	}

	@Override
	public final void create() {
		GraphicsService.init();
		config = config == null ? AssetService.<ApplicationConfig> load(configLocation) : config;
		config.init();
		AssetService.loadAsync(new SceneLoadedCallback(), config.initialScenePath, Scene.class, 0);
	}

	@Override
	public final void resize(int width, int height) {
		EventService.post(resizeEvent);
	}

	@Override
	public final void render() {
		EventService.post(updateEvent);
		EventService.post(cleanupEvent);
	}

	@Override
	public final void pause() {
		paused = true;
		EventService.post(pauseEvent);
		EventService.post(cleanupEvent);
	}

	@Override
	public final void resume() {
		paused = false;
		EventService.post(resumeEvent);
		EventService.post(cleanupEvent);
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
		// TODO sceneManager.stop();
		EventService.post(shutdownEvent);
		Workbench.close(workbench);
	}

	private static class ShutdownEvent implements Event<ApplicationShutdownListener> {
		@Override
		public Class<ApplicationShutdownListener> getSubscriptionType() {
			return ApplicationShutdownListener.class;
		}

		@Override
		public void dispatch(ApplicationShutdownListener listener) {
			listener.onShutdown();
		}
	}

	private static class PauseEvent implements Event<ApplicationActivityListener> {
		@Override
		public Class<ApplicationActivityListener> getSubscriptionType() {
			return ApplicationActivityListener.class;
		}

		@Override
		public void dispatch(ApplicationActivityListener listener) {
			listener.onPause();
		}
	}

	private static class ResumeEvent implements Event<ApplicationActivityListener> {
		@Override
		public Class<ApplicationActivityListener> getSubscriptionType() {
			return ApplicationActivityListener.class;
		}

		@Override
		public void dispatch(ApplicationActivityListener listener) {
			listener.onResume();
		}
	}

	private static class UpdateEvent implements Event<ApplicationUpdateListener> {
		@Override
		public Class<ApplicationUpdateListener> getSubscriptionType() {
			return ApplicationUpdateListener.class;
		}

		@Override
		public void dispatch(ApplicationUpdateListener listener) {
			listener.onUpdate();
		}
	}

	private static class CleanupEvent implements Event<ApplicationCleanupListener> {
		@Override
		public Class<ApplicationCleanupListener> getSubscriptionType() {
			return ApplicationCleanupListener.class;
		}

		@Override
		public void dispatch(ApplicationCleanupListener listener) {
			listener.onCleanup();
		}
	}

	private static class ResizeEvent implements Event<ApplicationResizeListener> {
		@Override
		public Class<ApplicationResizeListener> getSubscriptionType() {
			return ApplicationResizeListener.class;
		}

		@Override
		public void dispatch(ApplicationResizeListener listener) {
			listener.onResize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
	}

	private static class SceneLoadedCallback implements AsyncCallback<Scene> {
		@Override
		public void onSuccess(Scene scene) {
			scene.start();
		}

		@Override
		public void onException(Throwable exception) {
		}

		@Override
		public void onCanceled(String message) {
		}

		@Override
		public void onProgress(float progress) {
		}
	}
}
