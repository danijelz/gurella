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

	private static SwtLwjglApplication getGdxApplication(int editorId) {
		return gdxAppByEditorId.get(editorId);
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
		SwtLwjglApplication gdxApplication = getGdxApplication(editorId);
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
		Gdx.app = gdxApplication;
		Gdx.graphics = gdxApplication.getGraphics();
		Gdx.audio = gdxApplication.getAudio();
		Gdx.files = gdxApplication.getFiles();
		Gdx.input = gdxApplication.getInput();
		Gdx.net = gdxApplication.getNet();
		Gdx.gl = graphics.gl20;
		Gdx.gl20 = graphics.gl20;
		Gdx.gl30 = graphics.gl30;
		graphics.setContext();
	}
}
