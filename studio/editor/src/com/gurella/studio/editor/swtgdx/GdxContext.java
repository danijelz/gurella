package com.gurella.studio.editor.swtgdx;

import java.util.concurrent.locks.ReentrantLock;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;

public class GdxContext {
	private static final ReentrantLock lock = new ReentrantLock();
	private static final IntMap<SwtLwjglApplication> gdxAppByEditorId = new IntMap<>();
	private static SwtLwjglApplication current;

	private GdxContext() {
	}

	static void put(int editorId, SwtLwjglApplication application) {
		gdxAppByEditorId.put(editorId, application);
	}

	static void remove(int editorId) {
		gdxAppByEditorId.remove(editorId);
	}

	public static void run(int editorId, Runnable action) {
		try {
			lock.lock();
			runSynchronized(editorId, action);
		} finally {
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
		setCurrent(current);
		try {
			action.run();
		} finally {
			current = previous;
			setCurrent(current);
		}
	}

	private static void setCurrent(SwtLwjglApplication gdxApplication) {
		if (gdxApplication == null) {
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
			Gdx.app = gdxApplication;
			SwtLwjglGraphics graphics = gdxApplication.getGraphics();
			Gdx.graphics = graphics;
			Gdx.audio = gdxApplication.getAudio();
			Gdx.files = gdxApplication.getFiles();
			Gdx.input = gdxApplication.getInput();
			Gdx.net = gdxApplication.getNet();
			Gdx.gl = graphics.getGL20();
			Gdx.gl20 = graphics.getGL20();
			Gdx.gl30 = graphics.getGL30();
			graphics.setContext();
		}
	}
}
