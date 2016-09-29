package com.gurella.engine.application;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
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

public final class Application implements ApplicationListener, GurellaStateProvider {
	private static final PauseEvent pauseEvent = new PauseEvent();
	private static final ResumeEvent resumeEvent = new ResumeEvent();
	private static final UpdateEvent updateEvent = new UpdateEvent();
	private static final ResizeEvent resizeEvent = new ResizeEvent();

	public float deltaTime;
	private boolean paused;

	private ApplicationConfig config;
	private String initialScenePath;
	private final SceneManager sceneManager = new SceneManager(null);

	private Thread renderThread;

	Application(ApplicationConfig config) {
		this.config = config;
	}
	
	public static Application current() {
		return (Application) Gdx.app.getApplicationListener();
	}

	@Override
	public final void create() {
		renderThread = Thread.currentThread();

		// TODO create services by checking if this is studio
		Gdx.app.setLogLevel(com.badlogic.gdx.Application.LOG_DEBUG);
		// TODO config.init(this);
		// TODO add init scripts to initializer
		GraphicsService.init();
		sceneManager.showScene(initialScenePath);
	}

	@Override
	public final void resize(int width, int height) {
		EventService.post(resizeEvent);
	}

	@Override
	public final void render() {
		deltaTime = Gdx.graphics.getDeltaTime();
		// TODO clear must be handled by RenderSystem with spec from camera
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		EventService.post(updateEvent);
	}

	@Override
	public final void pause() {
		paused = true;
		EventService.post(pauseEvent);
	}

	@Override
	public final void resume() {
		paused = false;
		if (Gdx.app.getType() == ApplicationType.Android) {
			AssetService.reloadInvalidated();
		}
		EventService.post(resumeEvent);
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
		EventService.post(new ApplicationShutdownEvent());
		// TODO sceneManager.stop();
		DisposablesService.disposeAll();
	}
	
	@Override
	public boolean isInRenderThread() {
		return renderThread == Thread.currentThread();
	}

	private static class ApplicationShutdownEvent implements Event<ApplicationShutdownListener> {
		@Override
		public Class<ApplicationShutdownListener> getSubscriptionType() {
			return ApplicationShutdownListener.class;
		}

		@Override
		public void dispatch(ApplicationShutdownListener listener) {
			listener.shutdown();
		}
	}

	private static class PauseEvent implements Event<ApplicationActivityListener> {
		@Override
		public Class<ApplicationActivityListener> getSubscriptionType() {
			return ApplicationActivityListener.class;
		}

		@Override
		public void dispatch(ApplicationActivityListener listener) {
			listener.pause();
		}
	}

	private static class ResumeEvent implements Event<ApplicationActivityListener> {
		@Override
		public Class<ApplicationActivityListener> getSubscriptionType() {
			return ApplicationActivityListener.class;
		}

		@Override
		public void dispatch(ApplicationActivityListener listener) {
			listener.resume();
		}
	}

	private static class UpdateEvent implements Event<ApplicationUpdateListener> {
		@Override
		public Class<ApplicationUpdateListener> getSubscriptionType() {
			return ApplicationUpdateListener.class;
		}

		@Override
		public void dispatch(ApplicationUpdateListener listener) {
			listener.update();
		}
	}

	private static class ResizeEvent implements Event<ApplicationResizeListener> {
		@Override
		public Class<ApplicationResizeListener> getSubscriptionType() {
			return ApplicationResizeListener.class;
		}

		@Override
		public void dispatch(ApplicationResizeListener listener) {
			listener.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
	}
}
