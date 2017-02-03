package com.gurella.engine.asset2;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.asset2.bundle.Bundle;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;
import com.gurella.engine.utils.Values;

public class AssetService {
	private static final MockAssetManager mockManager = new MockAssetManager();

	private static final ObjectMap<Application, AssetsManager> instances = new ObjectMap<Application, AssetsManager>();
	private static AssetsManager lastSelected;
	private static Application lastApp;

	static {
		Texture.setAssetManager(mockManager);
		Cubemap.setAssetManager(mockManager);
	}

	private AssetService() {
	}

	private static AssetsManager getManager() {
		AssetsManager manager;
		boolean subscribe = false;

		synchronized (instances) {
			Application app = Gdx.app;
			if (lastApp == app) {
				return lastSelected;
			}

			manager = instances.get(app);
			if (manager == null) {
				manager = new AssetsManager();
				instances.put(app, manager);
				subscribe = true;
			}

			lastApp = app;
			lastSelected = manager;

		}

		if (subscribe) {
			EventService.subscribe(new Cleaner());
			EventService.subscribe(manager);
		}

		return manager;
	}

	public static boolean isLoaded(String fileName) {
		return getManager().isLoaded(fileName, Assets.getFileType(fileName), Assets.getAssetClass(fileName));
	}

	public static boolean isLoaded(String fileName, FileType fileType) {
		return getManager().isLoaded(fileName, fileType, Assets.getAssetClass(fileName));
	}

	public static boolean isLoaded(String fileName, Class<?> assetType) {
		return getManager().isLoaded(fileName, Assets.getFileType(fileName), assetType);
	}

	public static boolean isLoaded(AssetId assetId) {
		return getManager().isLoaded(assetId.fileName, assetId.fileType, assetId.assetType);
	}

	public static boolean isLoaded(String fileName, FileType fileType, Class<?> assetType) {
		return getManager().isLoaded(fileName, fileType, assetType);
	}

	public static <T> UnloadResult unload(T asset) {
		return getManager().unload(asset);
	}

	public static <T> Array<T> getAll(Class<T> type, Array<T> out) {
		return getManager().getAll(type, out);
	}

	public static <T> String getFileName(T asset) {
		return getManager().getFileName(asset);
	}

	public static <T> FileType getFileType(T asset) {
		return getManager().getFileType(asset);
	}

	public static <T> AssetId getAssetId(T asset, AssetId out) {
		return getManager().getAssetId(asset, out);
	}

	public static boolean isManaged(Object asset) {
		return getManager().isManaged(asset);
	}

	public static <T> void save(T asset, String fileName) {
		getManager().save(asset, fileName, Assets.getFileType(fileName));
	}

	public static <T> void save(T asset, String fileName, FileType fileType) {
		getManager().save(asset, fileName, fileType);
	}

	public static <T> void save(T asset, FileHandle file) {
		getManager().save(asset, file.path(), file.type());
	}

	public static DeleteResult delete(String fileName, FileType fileType) {
		return getManager().delete(fileName, fileType);
	}

	public static DeleteResult delete(Object asset) {
		return getManager().delete(asset);
	}

	public static boolean update() {
		return getManager().update();
	}

	public static boolean update(int millis) {
		return getManager().update(millis);
	}

	public static void finishLoading() {
		getManager().finishLoading();
	}

	public static void finishLoading(String fileName, FileType fileType, Class<?> assetType) {
		getManager().finishLoading(fileName, fileType, assetType);
	}

	public static <T> T load(String fileName) {
		return getManager().load(fileName, Assets.getFileType(fileName), Assets.<T> getAssetClass(fileName));
	}

	public static <T> T load(String fileName, FileType fileType) {
		return getManager().load(fileName, fileType, Assets.<T> getAssetClass(fileName));
	}

	public static <T> T load(String fileName, Class<T> assetType) {
		return getManager().load(fileName, Assets.getFileType(fileName), assetType);
	}

	public static <T> T load(AssetId assetId) {
		return getManager().load(assetId.fileName, assetId.fileType, Values.<Class<T>> cast(assetId.assetType));
	}

	public static <T> T load(String fileName, FileType fileType, Class<T> assetType) {
		return getManager().load(fileName, fileType, assetType);
	}

	public static <T> void loadAsync(AsyncCallback<T> callback, String fileName, int priority) {
		Class<T> assetType = Assets.<T> getAssetClass(fileName);
		getManager().loadAsync(callback, fileName, Assets.getFileType(fileName), assetType, priority);
	}

	public static <T> void loadAsync(AsyncCallback<T> callback, String fileName, FileType fileType, int priority) {
		getManager().loadAsync(callback, fileName, fileType, Assets.<T> getAssetClass(fileName), priority);
	}

	public static <T> void loadAsync(AsyncCallback<T> callback, String fileName, Class<T> assetType, int priority) {
		getManager().loadAsync(callback, fileName, Assets.getFileType(fileName), assetType, priority);
	}

	public static <T> void loadAsync(AsyncCallback<T> callback, AssetId assetId, int priority) {
		Class<T> assetType = Values.<Class<T>> cast(assetId.assetType);
		getManager().loadAsync(callback, assetId.fileName, assetId.fileType, assetType, priority);
	}

	public static <T> void loadAsync(AsyncCallback<T> callback, String fileName, FileType fileType, Class<T> assetType,
			int priority) {
		getManager().loadAsync(callback, fileName, fileType, assetType, priority);
	}

	public static String getBundledId(Object asset) {
		return getManager().getBundleId(asset);
	}

	public static Bundle getBundle(Object asset) {
		return getManager().getBundle(asset);
	}

	public static boolean fileExists(String fileName, FileType fileType) {
		return getManager().fileExists(fileName, fileType);
	}

	private static class Cleaner implements ApplicationShutdownListener {
		@Override
		public void shutdown() {
			EventService.unsubscribe(this);
			AssetsManager removed;

			synchronized (instances) {
				removed = instances.remove(Gdx.app);

				if (removed == lastSelected) {
					lastSelected = null;
					lastApp = null;
				}
			}

			EventService.unsubscribe(removed);
			removed.dispose();
		}
	}
}
