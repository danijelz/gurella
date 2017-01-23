package com.gurella.studio.editor.swtgdx;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;

public class GdxContext {
	private static final ReentrantLock lock = new ReentrantLock();
	private static final AtomicInteger lockCounter = new AtomicInteger();
	private static final IntMap<SwtLwjglApplication> gdxAppByEditorId = new IntMap<>();
	private static SwtLwjglApplication current;

	private GdxContext() {
	}

	static void put(int editorId, SwtLwjglApplication application) {
		try {
			lock.lock();
			gdxAppByEditorId.put(editorId, application);
		} finally {
			lock.unlock();
		}
	}

	static void remove(int editorId) {
		try {
			lock.lock();
			gdxAppByEditorId.remove(editorId);
		} finally {
			lock.unlock();
		}
	}

	public static void run(int editorId, Runnable action) {
		try {
			lock.lock();
			lockCounter.incrementAndGet();
			runSynchronized(editorId, action);
		} finally {
			lockCounter.decrementAndGet();
			lock.unlock();
		}
	}

	private static void runSynchronized(int editorId, Runnable action) {
		SwtLwjglApplication gdxApplication = gdxAppByEditorId.get(editorId);
		if (current == gdxApplication) {
			action.run();
		} else {
			runInSwitchedGdxContext(gdxApplication, action);
		}
	}

	private static void runInSwitchedGdxContext(SwtLwjglApplication gdxApplication, Runnable action) {
		SwtLwjglApplication previous = current;
		current = gdxApplication;
		updateContext();

		try {
			action.run();
		} finally {
			rollbackContext(previous);
		}
	}

	private static void rollbackContext(SwtLwjglApplication previous) {
		if (previous != null && (gdxAppByEditorId.containsKey(previous.editorId) || lockCounter.get() > 1)) {
			current = previous;
			updateContext();
		}
	}

	public static <T> T get(int editorId, Supplier<T> supplier) {
		try {
			lock.lock();
			lockCounter.incrementAndGet();
			return getSynchronized(editorId, supplier);
		} finally {
			lockCounter.decrementAndGet();
			lock.unlock();
		}
	}

	private static <T> T getSynchronized(int editorId, Supplier<T> supplier) {
		SwtLwjglApplication gdxApplication = gdxAppByEditorId.get(editorId);
		if (current == gdxApplication) {
			return supplier.get();
		} else {
			return getInSwitchedGdxContext(gdxApplication, supplier);
		}
	}

	private static <T> T getInSwitchedGdxContext(SwtLwjglApplication gdxApplication, Supplier<T> supplier) {
		SwtLwjglApplication previous = current;
		current = gdxApplication;
		updateContext();

		try {
			return supplier.get();
		} finally {
			rollbackContext(previous);
		}
	}

	private static void updateContext() {
		if (current == null) {
			Gdx.app = null;
			Gdx.graphics = null;
			Gdx.audio = null;
			Gdx.files = null;
			Gdx.input = null;
			Gdx.net = null;
			Gdx.gl = null;
			Gdx.gl20 = null;
			Gdx.gl30 = null;
		} else {
			Gdx.app = current;
			SwtLwjglGraphics graphics = current.getGraphics();
			Gdx.graphics = graphics;
			Gdx.audio = current.getAudio();
			Gdx.files = current.getFiles();
			Gdx.input = current.getInput();
			Gdx.net = current.getNet();
			Gdx.gl = graphics.getGL20();
			Gdx.gl20 = graphics.getGL20();
			Gdx.gl30 = graphics.getGL30();
			graphics.setContext();
		}
	}
}
