package com.gurella.studio.gdx;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jdt.core.IJavaProject;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.asset.bundle.Bundle;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.event.Dispatcher;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.subscriptions.application.ApplicationCleanupListener;
import com.gurella.studio.common.AssetsFolderLocator;

public class GdxContext {
	private static final ReentrantLock lock = new ReentrantLock();
	private static int counter = 0;

	private static final IntMap<Application> appById = new IntMap<>();
	private static final IntMap<IJavaProject> javaProjectById = new IntMap<>();

	private static Application current;
	private static int currentId;

	private GdxContext() {
	}

	public static void activate(int contextId, Application application, IJavaProject javaProject) {
		try {
			lock.lock();
			appById.put(contextId, application);
			javaProjectById.put(contextId, javaProject);
		} finally {
			lock.unlock();
		}
	}

	public static void deactivate(int contextId) {
		try {
			lock.lock();
			appById.remove(contextId);
			javaProjectById.remove(contextId);
		} finally {
			lock.unlock();
		}
	}

	public static IJavaProject getJavaProject(int contextId) {
		return javaProjectById.get(contextId);
	}

	public static IFolder getAssetsFolder(int contextId) {
		return AssetsFolderLocator.getAssetsFolder(javaProjectById.get(contextId));
	}

	public static void run(int contextId, Runnable action) {
		try {
			lock.lock();
			counter++;
			runSynchronized(contextId, action);
		} finally {
			counter--;
			lock.unlock();
		}
	}

	private static void runSynchronized(int contextId, Runnable action) {
		Application gdxApplication = appById.get(contextId);
		if (current == gdxApplication) {
			action.run();
		} else {
			runInSwitchedGdxContext(gdxApplication, contextId, action);
		}
	}

	private static void runInSwitchedGdxContext(Application gdxApplication, int contextId, Runnable action) {
		Application previous = current;
		current = gdxApplication;
		currentId = contextId;

		updateContext();

		try {
			action.run();
		} finally {
			rollbackContext(previous);
		}
	}

	private static void rollbackContext(Application previous) {
		Application app = counter > 1 ? previous : appById.containsKey(currentId) ? current : null;
		if (current != app) {
			current = app;
			updateContext();
		}
	}

	public static <T> T get(int contextId, Supplier<T> supplier) {
		try {
			lock.lock();
			counter++;
			return getSynchronized(contextId, supplier);
		} finally {
			counter--;
			lock.unlock();
		}
	}

	private static <T> T getSynchronized(int contextId, Supplier<T> supplier) {
		Application gdxApplication = appById.get(contextId);
		if (current == gdxApplication) {
			return supplier.get();
		} else {
			return getInSwitchedGdxContext(gdxApplication, contextId, supplier);
		}
	}

	private static <T> T getInSwitchedGdxContext(Application gdxApplication, int contextId, Supplier<T> supplier) {
		Application previous = current;
		current = gdxApplication;
		currentId = contextId;

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
			Gdx.audio = current.getAudio();
			Gdx.files = current.getFiles();
			Gdx.input = current.getInput();
			Gdx.net = current.getNet();
			Graphics graphics = current.getGraphics();
			Gdx.graphics = graphics;
			Gdx.gl = graphics.getGL20();
			Gdx.gl20 = graphics.getGL20();
			Gdx.gl30 = graphics.getGL30();

			if (current instanceof ContextAwareApplication) {
				((ContextAwareApplication) current).activate();
			}
		}
	}

	// events

	public static void subscribe(int contextId, EventSubscription subscriber) {
		run(contextId, () -> EventService.subscribe(subscriber));
	}

	public static void unsubscribe(int contextId, EventSubscription subscriber) {
		run(contextId, () -> EventService.unsubscribe(subscriber));
	}

	public static <L extends EventSubscription> void post(int contextId, Event<L> event) {
		run(contextId, () -> EventService.post(event));
	}

	public static <L extends EventSubscription> void post(int contextId, Class<L> subscriptionType,
			Dispatcher<L> dispatcher) {
		run(contextId, () -> EventService.post(subscriptionType, dispatcher));
	}

	public static void subscribe(int contextId, int channel, EventSubscription subscriber) {
		run(contextId, () -> EventService.subscribe(channel, subscriber));
	}

	public static void unsubscribe(int contextId, int channel, EventSubscription subscriber) {
		run(contextId, () -> EventService.unsubscribe(channel, subscriber));
	}

	public static <L extends EventSubscription> void post(int contextId, int channel, Event<L> event) {
		run(contextId, () -> EventService.post(channel, event));
	}

	public static <L extends EventSubscription> void post(int contextId, int channel, Class<L> subscriptionType,
			Dispatcher<L> dispatcher) {
		run(contextId, () -> EventService.post(channel, subscriptionType, dispatcher));
	}

	public static void clean(int contextId) {
		run(contextId, () -> EventService.post(ApplicationCleanupListener.class, l -> l.onCleanup()));
	}

	// assets

	public static <T> void loadAsync(int contextId, String fileName, AsyncCallback<T> callback, int priority) {
		run(contextId, () -> AssetService.loadAsync(callback, fileName, priority));
	}

	public static <T> void loadAsync(int contextId, String fileName, Class<T> type, AsyncCallback<T> callback,
			int priority) {
		run(contextId, () -> AssetService.loadAsync(callback, fileName, type, priority));
	}

	public static <T> T load(int contextId, String fileName) {
		return get(contextId, () -> AssetService.load(fileName));
	}

	public static <T> T load(int contextId, String fileName, Class<T> type) {
		return get(contextId, () -> AssetService.load(fileName, type));
	}

	public static <T> void unload(int contextId, T asset) {
		run(contextId, () -> AssetService.unload(asset));
	}

	public static <T> String getFileName(int contextId, T asset) {
		return get(contextId, () -> AssetService.getFileName(asset));
	}

	public static boolean isManaged(int contextId, Object asset) {
		return get(contextId, () -> Boolean.valueOf(AssetService.isManaged(asset))).booleanValue();
	}

	public static boolean isManaged(int contextId, String fileName) {
		return get(contextId, () -> Boolean.valueOf(AssetService.isManaged(fileName))).booleanValue();
	}

	public static <T> void save(int contextId, T asset) {
		run(contextId, () -> AssetService.save(asset));
	}

	public static <T> void save(int contextId, T asset, String fileName) {
		run(contextId, () -> AssetService.save(asset, fileName));
	}

	public static <T> void save(int contextId, T asset, String fileName, FileType fileType) {
		run(contextId, () -> AssetService.save(asset, fileName, fileType));
	}

	public static <T> void save(int contextId, T asset, FileHandle handle) {
		run(contextId, () -> AssetService.save(asset, handle));
	}

	public static void delete(int contextId, String fileName) {
		run(contextId, () -> AssetService.delete(fileName));
	}

	public static void addDependency(int contextId, Object asset, Object dependency) {
		run(contextId, () -> AssetService.addDependency(asset, dependency));
	}

	public static void removeDependency(int contextId, Object asset, Object dependency) {
		run(contextId, () -> AssetService.removeDependency(asset, dependency));
	}

	public static void replaceDependency(int contextId, Object asset, Object oldDependency, Object newDependency) {
		run(contextId, () -> AssetService.replaceDependency(asset, oldDependency, newDependency));
	}

	public static void addToBundle(int contextId, Bundle bundle, Object asset, String bundleId) {
		run(contextId, () -> AssetService.addToBundle(bundle, asset, bundleId));
	}

	public static void removeFromBundle(int contextId, Bundle bundle, Object asset) {
		run(contextId, () -> AssetService.removeFromBundle(bundle, asset));
	}

	public static boolean fileExists(int contextId, String fileName, FileType fileType) {
		return get(contextId, () -> Boolean.valueOf(AssetService.fileExists(fileName, fileType))).booleanValue();
	}
}
