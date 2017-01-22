package com.gurella.studio.editor.swtgdx;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Composite;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationLogger;
import com.badlogic.gdx.backends.lwjgl.LwjglClipboard;
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import com.badlogic.gdx.backends.lwjgl.LwjglNet;
import com.badlogic.gdx.backends.lwjgl.LwjglPreferences;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;
import com.gurella.studio.editor.utils.Synchronized;

//Based on https://github.com/NkD/gdx-backend-lwjgl-swt/tree/master/src/com/badlogic/gdx/backends/lwjgl/swt
public class SwtLwjglApplication implements Application {
	private final int editorId;

	private final SwtLwjglGraphics graphics;
	private final OpenALAudio audio;
	private final SwtLwjglFiles files;
	private final SwtLwjglInput input;
	private final LwjglNet net;
	private final ApplicationListener listener;
	private boolean running = true;

	private List<Runnable> runnables = new ArrayList<>();
	private List<Runnable> executedRunnables = new ArrayList<>();

	private final List<LifecycleListener> lifecycleListeners = new ArrayList<>();

	private int logLevel = LOG_DEBUG;

	private String preferencesDir = ".prefs/";
	private ObjectMap<String, Preferences> preferences = new ObjectMap<>();

	private int lastWidth;
	private int lastHeight;
	private boolean wasActive = true;

	private int backgroundFps = 60;
	private ApplicationLogger applicationLogger;

	public SwtLwjglApplication(int editorId, Composite parent, String internalAssetsPath) {
		this.editorId = editorId;
		GdxContext.put(editorId, this);

		this.listener = new EditorApplicationListener(editorId);

		LwjglNativesLoader.load();
		setApplicationLogger(new LwjglApplicationLogger());

		audio = OpenAlAudioSingletone.getInstance();
		graphics = new SwtLwjglGraphics(parent);
		files = new SwtLwjglFiles(internalAssetsPath);
		input = new SwtLwjglInput(graphics.getGlCanvas());
		net = new LwjglNet();

		lastWidth = graphics.getWidth();
		lastHeight = graphics.getHeight();
	}

	public void init() {
		listener.create();
		final GLCanvas glCanvas = graphics.getGlCanvas();
		glCanvas.addListener(SWT.Dispose, e -> GdxContext.run(editorId, this::onGlCanvasDisposed));
		mainLoop();
	}

	private void onGlCanvasDisposed() {
		running = false;
		EventService.post(editorId, EditorPreCloseListener.class, l -> l.onEditorPreClose());

		listener.pause();
		listener.dispose();

		synchronized (lifecycleListeners) {
			new ArrayList<>(lifecycleListeners).stream().forEachOrdered(l -> disposeListener(l));
			lifecycleListeners.clear();
		}

		EventService.post(editorId, EditorCloseListener.class, l -> l.onEditorClose());
		EventService.post(ApplicationShutdownListener.class, l -> l.shutdown());
		OpenAlAudioSingletone.dispose(audio);
	}

	private static void disposeListener(LifecycleListener listener) {
		listener.pause();
		listener.dispose();
	}

	private void mainLoop() {
		GLCanvas glCanvas = graphics.getGlCanvas();
		if (!running || glCanvas.isDisposed()) {
			return;
		}

		graphics.lastTime = System.nanoTime();
		boolean isActive = updateActivity();

		boolean shouldRender = graphics.shouldRender();
		shouldRender |= updateViewport();
		shouldRender |= executeRunnables();

		input.update();
		if (audio != null) {
			audio.update();
		}

		if (!isActive && backgroundFps == -1) {
			shouldRender = false;
		}

		if (shouldRender) {
			graphics.setCurrent();
			graphics.update();
			listener.render();
			graphics.swapBuffer();
		}

		glCanvas.getDisplay().timerExec(35, () -> GdxContext.run(editorId, this::mainLoop));
	}

	private boolean updateViewport() {
		int width = graphics.getWidth();
		int height = graphics.getHeight();
		if (lastWidth != width || lastHeight != height) {
			lastWidth = width;
			lastHeight = height;
			graphics.setCurrent();
			Gdx.gl.glViewport(0, 0, lastWidth, lastHeight);
			listener.resize(lastWidth, lastHeight);
			return true;
		} else {
			return false;
		}
	}

	private boolean updateActivity() {
		boolean isActive = graphics.getGlCanvas().isCurrent();
		if (wasActive && !isActive) {
			wasActive = false;
			synchronized (lifecycleListeners) {
				lifecycleListeners.stream().forEachOrdered(l -> l.pause());
			}
			listener.pause();
		} else if (!wasActive && isActive) {
			wasActive = true;
			listener.resume();
			synchronized (lifecycleListeners) {
				lifecycleListeners.stream().forEachOrdered(l -> l.resume());
			}
		}
		return isActive;
	}

	public boolean executeRunnables() {
		if (runnables.size() == 0) {
			return false;
		}

		Synchronized.run(runnables, () -> switchRunnables());
		executedRunnables.stream().forEachOrdered(r -> r.run());
		executedRunnables.clear();
		return true;
	}

	private void switchRunnables() {
		List<Runnable> temp = runnables;
		runnables = executedRunnables;
		executedRunnables = temp;
	}

	@Override
	public ApplicationListener getApplicationListener() {
		return listener;
	}

	@Override
	public Audio getAudio() {
		return audio;
	}

	@Override
	public Files getFiles() {
		return files;
	}

	@Override
	public SwtLwjglGraphics getGraphics() {
		return graphics;
	}

	@Override
	public Input getInput() {
		return input;
	}

	@Override
	public Net getNet() {
		return net;
	}

	@Override
	public ApplicationType getType() {
		return ApplicationType.Desktop;
	}

	@Override
	public int getVersion() {
		return 0;
	}

	@Override
	public long getJavaHeap() {
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

	@Override
	public long getNativeHeap() {
		return getJavaHeap();
	}

	@Override
	public Preferences getPreferences(String name) {
		if (preferences.containsKey(name)) {
			return preferences.get(name);
		} else {
			Preferences prefs = new LwjglPreferences(name, this.preferencesDir);
			preferences.put(name, prefs);
			return prefs;
		}
	}

	@Override
	public Clipboard getClipboard() {
		return new LwjglClipboard();
	}

	@Override
	public void postRunnable(Runnable runnable) {
		Synchronized.run(runnables, () -> {
			runnables.add(runnable);
			graphics.requestRendering();
		});
	}

	@Override
	public void debug(String tag, String message) {
		if (logLevel >= LOG_DEBUG) {
			System.out.println(tag + ": " + message);
		}
	}

	@Override
	public void debug(String tag, String message, Throwable exception) {
		if (logLevel >= LOG_DEBUG) {
			System.out.println(tag + ": " + message);
			exception.printStackTrace(System.out);
		}
	}

	@Override
	public void log(String tag, String message) {
		if (logLevel >= LOG_INFO) {
			System.out.println(tag + ": " + message);
		}
	}

	@Override
	public void log(String tag, String message, Throwable exception) {
		if (logLevel >= LOG_INFO) {
			System.out.println(tag + ": " + message);
			exception.printStackTrace(System.out);
		}
	}

	@Override
	public void error(String tag, String message) {
		if (logLevel >= LOG_ERROR) {
			System.err.println(tag + ": " + message);
		}
	}

	@Override
	public void error(String tag, String message, Throwable exception) {
		if (logLevel >= LOG_ERROR) {
			System.err.println(tag + ": " + message);
			exception.printStackTrace(System.err);
		}
	}

	@Override
	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	@Override
	public int getLogLevel() {
		return logLevel;
	}

	@Override
	public void exit() {
		GdxContext.remove(editorId);
	}

	@Override
	public void addLifecycleListener(LifecycleListener lifecycleListener) {
		synchronized (lifecycleListeners) {
			lifecycleListeners.add(lifecycleListener);
		}
	}

	@Override
	public void removeLifecycleListener(LifecycleListener lifecycleListener) {
		synchronized (lifecycleListeners) {
			lifecycleListeners.remove(lifecycleListener);
		}
	}

	@Override
	public void setApplicationLogger(ApplicationLogger applicationLogger) {
		this.applicationLogger = applicationLogger;
	}

	@Override
	public ApplicationLogger getApplicationLogger() {
		return applicationLogger;
	}
}
