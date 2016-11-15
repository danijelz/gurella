package com.gurella.studio.editor.swtgl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Composite;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl.LwjglClipboard;
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import com.badlogic.gdx.backends.lwjgl.LwjglNet;
import com.badlogic.gdx.backends.lwjgl.LwjglPreferences;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.studio.GurellaStudioPlugin;

//Based on https://github.com/NkD/gdx-backend-lwjgl-swt/tree/master/src/com/badlogic/gdx/backends/lwjgl/swt
public class SwtLwjglApplication implements Application {
	private final SwtLwjglGraphics graphics;
	private OpenALAudio audio;
	private final SwtLwjglFiles files;
	private final SwtLwjglInput input;
	private final LwjglNet net;
	private final ApplicationListener listener;
	private boolean running = true;

	private List<Runnable> runnables = new ArrayList<>();
	private List<Runnable> executedRunnables = new ArrayList<>();

	private final List<LifecycleListener> lifecycleListeners = new ArrayList<>();

	private int logLevel = LOG_INFO;

	private String preferencesDir;
	private ObjectMap<String, Preferences> preferences = new ObjectMap<>();

	private int lastWidth;
	private int lastHeight;
	private boolean wasActive = true;

	private int backgroundFPS;

	public SwtLwjglApplication(String internalPath, Composite parent, ApplicationListener listener) {
		this(internalPath, parent, listener, createConfig(parent.getSize().x, parent.getSize().y));
	}

	public SwtLwjglApplication(String internalPath, Composite parent, ApplicationListener listener,
			SwtApplicationConfig config) {
		this.listener = listener;
		backgroundFPS = config.backgroundFPS;
		preferencesDir = config.preferencesDirectory;

		LwjglNativesLoader.load();

		audio = OpenAlAudioSingletone.getInstance(config);
		graphics = new SwtLwjglGraphics(parent, config);
		files = new SwtLwjglFiles(internalPath);
		input = new SwtLwjglInput(graphics.getGlCanvas());
		net = new LwjglNet();

		synchronized (GurellaStudioPlugin.glMutex) {
			init();
		}

		parent.getDisplay().asyncExec(() -> mainLoop());
	}

	private static SwtApplicationConfig createConfig(int width, int height) {
		SwtApplicationConfig config = new SwtApplicationConfig();
		config.width = width;
		config.height = height;
		return config;
	}

	private void init() {
		graphics.init();
		setCurrent();

		listener.create();

		lastWidth = graphics.getWidth();
		lastHeight = graphics.getHeight();

		final GLCanvas glCanvas = graphics.getGlCanvas();
		glCanvas.addListener(SWT.Dispose, e -> onGlCanvasDisposed());
	}

	private void setCurrent() {
		Gdx.app = this;
		Gdx.graphics = graphics;
		Gdx.audio = audio;
		Gdx.files = files;
		Gdx.input = input;
		Gdx.net = net;
		Gdx.gl = graphics.gl20;
		Gdx.gl20 = graphics.gl20;
		Gdx.gl30 = graphics.gl30;
		graphics.useContext();
	}

	private void onGlCanvasDisposed() {
		running = false;
		listener.pause();
		listener.dispose();
		synchronized (lifecycleListeners) {
			new ArrayList<>(lifecycleListeners).stream().forEach(l -> disposeListener(l));
			lifecycleListeners.clear();
		}
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

		synchronized (GurellaStudioPlugin.glMutex) {
			setCurrent();
			update();
		}

		if (running && !glCanvas.isDisposed()) {
			glCanvas.getDisplay().timerExec(35, () -> mainLoop());
		}
	}

	private void update() {
		graphics.lastTime = System.nanoTime();
		boolean isActive = updateActivity();

		boolean shouldRender = graphics.shouldRender();
		shouldRender |= updateViewport();
		shouldRender |= executeRunnables();

		input.update();
		if (audio != null) {
			audio.update();
		}

		if (!isActive && backgroundFPS == -1) {
			shouldRender = false;
		}

		if (shouldRender) {
			render();
		}
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
				lifecycleListeners.stream().forEach(l -> l.pause());
			}
			listener.pause();
		} else if (!wasActive && isActive) {
			wasActive = true;
			listener.resume();
			synchronized (lifecycleListeners) {
				lifecycleListeners.stream().forEach(l -> l.resume());
			}
		}
		return isActive;
	}

	private void render() {
		graphics.setCurrent();
		graphics.update();
		listener.render();
		graphics.swapBuffer();
	}

	public boolean executeRunnables() {
		if (runnables.size() == 0) {
			return false;
		}

		switchRunnables();
		executedRunnables.stream().forEach(r -> r.run());
		executedRunnables.clear();
		return true;
	}

	private void switchRunnables() {
		synchronized (runnables) {
			List<Runnable> temp = runnables;
			runnables = executedRunnables;
			executedRunnables = temp;
		}
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

	public void stop() {
		running = false;
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
		synchronized (runnables) {
			runnables.add(runnable);
			graphics.requestRendering();
		}
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
		postRunnable(() -> running = false);
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
}
