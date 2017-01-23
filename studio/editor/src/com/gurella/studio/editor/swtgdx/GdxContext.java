package com.gurella.studio.editor.swtgdx;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.asset.Bundle;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.event.Dispatcher;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.EventSubscription;

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

	// events

	public static void subscribe(int editorId, EventSubscription subscriber) {
		run(editorId, () -> EventService.subscribe(subscriber));
	}

	public static void unsubscribe(int editorId, EventSubscription subscriber) {
		run(editorId, () -> EventService.unsubscribe(subscriber));
	}

	public static <L extends EventSubscription> void post(int editorId, Event<L> event) {
		run(editorId, () -> EventService.post(event));
	}

	public static <L extends EventSubscription> void post(int editorId, Class<L> subscriptionType,
			Dispatcher<L> dispatcher) {
		run(editorId, () -> EventService.post(subscriptionType, dispatcher));
	}

	public static void subscribe(int editorId, int channel, EventSubscription subscriber) {
		run(editorId, () -> EventService.subscribe(channel, subscriber));
	}

	public static void unsubscribe(int editorId, int channel, EventSubscription subscriber) {
		run(editorId, () -> EventService.unsubscribe(channel, subscriber));
	}

	public static <L extends EventSubscription> void post(int editorId, int channel, Event<L> event) {
		run(editorId, () -> EventService.post(channel, event));
	}

	public static <L extends EventSubscription> void post(int editorId, int channel, Class<L> subscriptionType,
			Dispatcher<L> dispatcher) {
		run(editorId, () -> EventService.post(channel, subscriptionType, dispatcher));
	}

	// assets

	public static <T> void loadAsync(int editorId, String fileName, AsyncCallback<T> callback, int priority) {
		run(editorId, () -> AssetService.loadAsync(fileName, callback, priority));
	}

	public static <T> void loadAsync(int editorId, String fileName, Class<T> type, AsyncCallback<T> callback,
			int priority) {
		run(editorId, () -> AssetService.loadAsync(fileName, type, callback, priority));
	}

	public static <T> void loadAsync(int editorId, String fileName, Class<T> type, AsyncCallback<T> callback,
			int priority, boolean sticky) {
		run(editorId, () -> AssetService.loadAsync(fileName, type, callback, priority, sticky));
	}

	public static <T> T load(int editorId, String fileName) {
		return get(editorId, () -> AssetService.load(fileName));
	}

	public static <T> T load(int editorId, String fileName, Class<T> type) {
		return get(editorId, () -> AssetService.load(fileName, type));
	}

	public static <T> T load(int editorId, String fileName, Class<T> type, int priority) {
		return get(editorId, () -> AssetService.load(fileName, type, priority));
	}

	public static <T> T load(int editorId, String fileName, Class<T> type, int priority, boolean sticky) {
		return get(editorId, () -> AssetService.load(fileName, type, priority, sticky));
	}

	public static <T> void unload(int editorId, T asset) {
		run(editorId, () -> AssetService.unload(asset));
	}

	public static <T> String getFileName(int editorId, T asset) {
		return get(editorId, () -> AssetService.getFileName(asset));
	}

	public static boolean isManaged(int editorId, Object asset) {
		return get(editorId, () -> Boolean.valueOf(AssetService.isManaged(asset))).booleanValue();
	}

	public static boolean isManaged(int editorId, String fileName) {
		return get(editorId, () -> Boolean.valueOf(AssetService.isManaged(fileName))).booleanValue();
	}

	public static <T> void save(int editorId, T asset) {
		run(editorId, () -> AssetService.save(asset));
	}

	public static <T> void save(int editorId, T asset, String fileName) {
		run(editorId, () -> AssetService.save(asset, fileName));
	}

	public static <T> void save(int editorId, T asset, String fileName, FileType fileType) {
		run(editorId, () -> AssetService.save(asset, fileName, fileType));
	}

	public static <T> void save(int editorId, T asset, FileHandle handle) {
		run(editorId, () -> AssetService.save(asset, handle));
	}

	public static void delete(int editorId, String fileName) {
		run(editorId, () -> AssetService.delete(fileName));
	}

	public static void addDependency(int editorId, Object asset, Object dependency) {
		run(editorId, () -> AssetService.addDependency(asset, dependency));
	}

	public static void removeDependency(int editorId, Object asset, Object dependency) {
		run(editorId, () -> AssetService.removeDependency(asset, dependency));
	}

	public static void replaceDependency(int editorId, Object asset, Object oldDependency, Object newDependency) {
		run(editorId, () -> AssetService.replaceDependency(asset, oldDependency, newDependency));
	}

	public static void addToBundle(int editorId, Bundle bundle, String internalId, Object asset) {
		run(editorId, () -> AssetService.addToBundle(bundle, internalId, asset));
	}

	public static void removeFromBundle(int editorId, Bundle bundle, String internalId, Object asset) {
		run(editorId, () -> AssetService.removeFromBundle(bundle, internalId, asset));
	}
}
