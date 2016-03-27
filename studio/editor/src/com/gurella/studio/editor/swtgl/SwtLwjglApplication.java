package com.gurella.studio.editor.swtgl;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Composite;
import org.lwjgl.LWJGLException;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglClipboard;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import com.badlogic.gdx.backends.lwjgl.LwjglNet;
import com.badlogic.gdx.backends.lwjgl.LwjglPreferences;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;

//https://github.com/NkD/gdx-backend-lwjgl-swt/tree/master/src/com/badlogic/gdx/backends/lwjgl/swt
public class SwtLwjglApplication implements Application {
	protected final SwtLwjglGraphics graphics;
	protected OpenALAudio audio;
	protected final LwjglFiles files;
	protected final SwtLwjglInput input;
	protected final LwjglNet net;
	protected final ApplicationListener listener;
	protected boolean running = true;
	protected final Array<Runnable> runnables = new Array<Runnable>();
	protected final Array<Runnable> executedRunnables = new Array<Runnable>();
	protected final Array<LifecycleListener> lifecycleListeners = new Array<LifecycleListener>();
	protected int logLevel = LOG_INFO;
	protected String preferencesdir;

	protected int lastWidth;
	protected int lastHeight;
	protected boolean wasActive = true;

	public SwtLwjglApplication(ApplicationListener listener, Composite parentComposite) {
		this(listener, createConfig(parentComposite.getSize().x, parentComposite.getSize().y), parentComposite);
	}

	public SwtLwjglApplication(ApplicationListener listener, SwtLwjglApplicationConfiguration config,
			Composite parentComposite) {
		this(listener, config, new SwtLwjglGraphics(parentComposite, config));
	}

	public SwtLwjglApplication(ApplicationListener listener, SwtLwjglApplicationConfiguration config,
			SwtLwjglGraphics graphics) {
		LwjglNativesLoader.load();

		this.graphics = graphics;
		if (!LwjglApplicationConfiguration.disableAudio) {
			audio = new OpenALAudio(config.audioDeviceSimultaneousSources, config.audioDeviceBufferCount,
					config.audioDeviceBufferSize);
		}
		files = new LwjglFiles();
		input = new SwtLwjglInput(graphics.getGlCanvas());
		net = new LwjglNet();
		this.listener = listener;
		this.preferencesdir = config.preferencesDirectory;

		Gdx.app = this;
		Gdx.graphics = graphics;
		Gdx.audio = audio;
		Gdx.files = files;
		Gdx.input = input;
		Gdx.net = net;
		initialize();
	}

	private static SwtLwjglApplicationConfiguration createConfig(int width, int height) {
		SwtLwjglApplicationConfiguration config = new SwtLwjglApplicationConfiguration();
		config.width = width;
		config.height = height;
		config.vSyncEnabled = true;
		return config;
	}

	private void initialize() {
		try {
			graphics.setupDisplay();
		} catch (LWJGLException e) {
			throw new GdxRuntimeException(e);
		}

		listener.create();
		graphics.resize = true;

		lastWidth = graphics.getWidth();
		lastHeight = graphics.getHeight();

		graphics.lastTime = System.nanoTime();

		GLCanvas glCanvas = graphics.getGlCanvas();
		glCanvas.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				running = false;
				listener.pause();
				listener.dispose();
				for (LifecycleListener l : lifecycleListeners) {
					l.pause();
					l.dispose();
				}

				if (audio != null)
					audio.dispose();
			}
		});

		glCanvas.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				graphics.setVSync(graphics.config.vSyncEnabled);
				try {
					SwtLwjglApplication.this.mainLoop();
				} catch (Throwable t) {
					if (audio != null)
						audio.dispose();
					if (t instanceof RuntimeException)
						throw (RuntimeException) t;
					else
						throw new GdxRuntimeException(t);
				}
			}
		});

		glCanvas.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (running && !glCanvas.isDisposed()) {
					mainLoop();
				}
				if (running && !glCanvas.isDisposed()) {
					glCanvas.getDisplay().asyncExec(this);
				}
			}
		});
	}

	void mainLoop() {
		graphics.lastTime = System.nanoTime();
		boolean isActive = graphics.getGlCanvas().isCurrent();
		if (wasActive && !isActive) {
			wasActive = false;
			synchronized (lifecycleListeners) {
				for (LifecycleListener lifecycleListener : lifecycleListeners)
					lifecycleListener.pause();
			}
			listener.pause();
		}
		if (!wasActive && isActive) {
			wasActive = true;
			listener.resume();
			synchronized (lifecycleListeners) {
				for (LifecycleListener lifecycleListener : lifecycleListeners)
					lifecycleListener.resume();
			}
		}

		boolean shouldRender = false;

		int width = graphics.getWidth();
		int height = graphics.getHeight();
		if (lastWidth != width || lastHeight != height) {
			lastWidth = width;
			lastHeight = height;
			graphics.setCurrent();
			Gdx.gl.glViewport(0, 0, lastWidth, lastHeight);
			listener.resize(lastWidth, lastHeight);
			shouldRender = true;
		}

		if (executeRunnables()) {
			shouldRender = true;
		}

		input.update();
		shouldRender |= graphics.shouldRender();
		if (audio != null)
			audio.update();

		if (!isActive && graphics.config.backgroundFPS == -1) {
			shouldRender = false;
		}
		int frameRate = isActive ? graphics.config.foregroundFPS : graphics.config.backgroundFPS;
		if (shouldRender) {
			graphics.setCurrent();
			graphics.updateTime();
			listener.render();
			graphics.swapBuffer();
		} else {
			// Sleeps to avoid wasting CPU in an empty loop.
			if (frameRate == -1)
				frameRate = 10;
			if (frameRate == 0)
				frameRate = graphics.config.backgroundFPS;
			if (frameRate == 0)
				frameRate = 30;
		}
	}

	public boolean executeRunnables() {
		synchronized (runnables) {
			executedRunnables.addAll(runnables);
			runnables.clear();
		}
		if (executedRunnables.size == 0) {
			return false;
		}
		for (int i = 0; i < executedRunnables.size; i++) {
			executedRunnables.get(i).run();
		}
		executedRunnables.clear();
		return true;
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

	ObjectMap<String, Preferences> preferences = new ObjectMap<String, Preferences>();

	@Override
	public Preferences getPreferences(String name) {
		if (preferences.containsKey(name)) {
			return preferences.get(name);
		} else {
			Preferences prefs = new LwjglPreferences(name, this.preferencesdir);
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
			Gdx.graphics.requestRendering();
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
		postRunnable(new Runnable() {
			@Override
			public void run() {
				running = false;
			}
		});
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
			lifecycleListeners.removeValue(lifecycleListener, true);
		}
	}
}
