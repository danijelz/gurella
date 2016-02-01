package com.gurella.engine.asset.manager;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.PixmapLoader;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonRegionLoader;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.asset.manager.AssetLoadingTask2.LoadingState;
import com.gurella.engine.base.resource.AsyncCallback;
import com.gurella.engine.utils.DisposablesService;
import com.gurella.engine.utils.ValueUtils;

/**
 * Loads and stores assets like textures, bitmapfonts, tile maps, sounds, music and so on.
 * 
 * @author mzechner
 */
public class AssetManager2 extends com.badlogic.gdx.assets.AssetManager {
	private final ObjectMap<String, AssetReference> assetsByFileName = new ObjectMap<String, AssetReference>();
	private final IdentityMap<Object, String> fileNamesByAsset = new IdentityMap<Object, String>();
	private final ObjectMap<String, Array<String>> assetDependencies = new ObjectMap<String, Array<String>>();

	private final ObjectMap<Class<?>, ObjectMap<String, AssetLoader<?, ?>>> loaders = new ObjectMap<Class<?>, ObjectMap<String, AssetLoader<?, ?>>>();

	private final Array<AssetLoadingTask2<?>> asyncQueue = new Array<AssetLoadingTask2<?>>();
	private final Array<AssetLoadingTask2<?>> waitingQueue = new Array<AssetLoadingTask2<?>>();
	private final Array<AssetLoadingTask2<?>> syncQueue = new Array<AssetLoadingTask2<?>>();

	private final AsyncExecutor executor = DisposablesService.add(new AsyncExecutor(1));

	private int loaded = 0;
	private int toLoad = 0;

	private final ObjectSet<String> injected = new ObjectSet<String>();
	private final Object lock = new Object();

	public AssetManager2() {
		this(new InternalFileHandleResolver());
	}

	public AssetManager2(FileHandleResolver resolver) {
		this(resolver, true);
	}

	public AssetManager2(FileHandleResolver resolver, boolean defaultLoaders) {
		if (defaultLoaders) {
			setLoader(BitmapFont.class, new BitmapFontLoader(resolver));
			setLoader(Music.class, new MusicLoader(resolver));
			setLoader(Pixmap.class, new PixmapLoader(resolver));
			setLoader(Sound.class, new SoundLoader(resolver));
			setLoader(TextureAtlas.class, new TextureAtlasLoader(resolver));
			setLoader(Texture.class, new TextureLoader(resolver));
			setLoader(Skin.class, new SkinLoader(resolver));
			setLoader(ParticleEffect.class, new ParticleEffectLoader(resolver));
			setLoader(com.badlogic.gdx.graphics.g3d.particles.ParticleEffect.class,
					new com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader(resolver));
			setLoader(PolygonRegion.class, new PolygonRegionLoader(resolver));
			setLoader(I18NBundle.class, new I18NBundleLoader(resolver));
			setLoader(Model.class, ".g3dj", new G3dModelLoader(new JsonReader(), resolver));
			setLoader(Model.class, ".g3db", new G3dModelLoader(new UBJsonReader(), resolver));
			setLoader(Model.class, ".obj", new ObjLoader(resolver));
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public <T> T get(String fileName) {
		@SuppressWarnings("unchecked")
		T value = (T) get(fileName, Object.class);
		return value;
	}

	@Override
	@SuppressWarnings("sync-override")
	public <T> T get(String fileName, Class<T> type) {
		synchronized (lock) {
			AssetReference reference = assetsByFileName.get(fileName);
			if (reference == null) {
				throw new GdxRuntimeException("Asset not loaded: " + fileName);
			}

			T asset = reference.getAsset();
			if (asset == null) {
				throw new GdxRuntimeException("Asset not loaded: " + fileName);
			}

			return asset;
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public <T> Array<T> getAll(Class<T> type, Array<T> out) {
		synchronized (lock) {
			boolean all = type == null || type == Object.class;
			for (Object asset : fileNamesByAsset.keys()) {
				if (all || ClassReflection.isAssignableFrom(type, asset.getClass())) {
					@SuppressWarnings("unchecked")
					T casted = (T) asset;
					out.add(casted);
				}
			}

			return out;
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public <T> T get(AssetDescriptor<T> assetDescriptor) {
		return get(assetDescriptor.fileName, assetDescriptor.type);
	}

	@Override
	@SuppressWarnings("sync-override")
	public <T> boolean containsAsset(T asset) {
		synchronized (lock) {
			return fileNamesByAsset.containsKey(asset);
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public <T> String getAssetFileName(T asset) {
		synchronized (lock) {
			return fileNamesByAsset.get(asset);
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public boolean isLoaded(String fileName) {
		synchronized (lock) {
			return assetsByFileName.containsKey(fileName);
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public boolean isLoaded(String fileName, @SuppressWarnings("rawtypes") Class type) {
		synchronized (lock) {
			return assetsByFileName.containsKey(fileName);
		}
	}

	@Override
	public <T> AssetLoader<T, ?> getLoader(final Class<T> type) {
		return getLoader(type, null);
	}

	@Override
	public <T> AssetLoader<T, AssetLoaderParameters<T>> getLoader(final Class<T> type, final String fileName) {
		synchronized (lock) {
			final ObjectMap<String, AssetLoader<?, ?>> loadersByType = loaders.get(type);
			if (loadersByType == null || loadersByType.size < 1) {
				return null;
			}

			if (fileName == null) {
				return ValueUtils.cast(loadersByType.get(""));
			} else {
				AssetLoader<T, AssetLoaderParameters<T>> result = null;
				int length = -1;
				for (ObjectMap.Entry<String, AssetLoader<?, ?>> entry : loadersByType.entries()) {
					if (entry.key.length() > length && fileName.endsWith(entry.key)) {
						result = ValueUtils.cast(entry.value);
						length = entry.key.length();
					}
				}
				return result;
			}
		}
	}

	<T> AssetLoader<T, AssetLoaderParameters<T>> findLoader(Class<T> type, String fileName) {
		AssetLoader<T, AssetLoaderParameters<T>> loader = getLoader(type, fileName);
		if (loader == null) {
			throw new GdxRuntimeException("No loader for type: " + type.getSimpleName());
		}
		return loader;
	}

	@Override
	@SuppressWarnings("sync-override")
	public <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type, AssetLoader<T, P> loader) {
		setLoader(type, null, loader);
	}

	@Override
	@SuppressWarnings("sync-override")
	public <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type, String suffix,
			AssetLoader<T, P> loader) {
		synchronized (lock) {
			if (type == null) {
				throw new IllegalArgumentException("type cannot be null.");
			}

			if (loader == null) {
				throw new IllegalArgumentException("loader cannot be null.");
			}

			ObjectMap<String, AssetLoader<?, ?>> loadersByType = loaders.get(type);
			if (loadersByType == null) {
				loadersByType = new ObjectMap<String, AssetLoader<?, ?>>();
				loaders.put(type, loadersByType);
			}

			loadersByType.put(suffix == null ? "" : suffix, loader);
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public <T> void load(String fileName, Class<T> type) {
		load(fileName, type, null, null, 0);
	}

	@Override
	@SuppressWarnings("sync-override")
	public <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
		load(fileName, type, parameter, null, 0);
	}

	@Override
	@SuppressWarnings({ "sync-override", "unchecked" })
	public void load(@SuppressWarnings("rawtypes") AssetDescriptor descriptor) {
		load(descriptor.fileName, descriptor.type, descriptor.params, null, 0);
	}

	public <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameters, AsyncCallback<T> callback,
			int priority) {
		synchronized (lock) {
			resetStats();

			AssetReference reference = assetsByFileName.get(fileName);
			if (reference == null) {
				addToQueue(fileName, type, parameters, callback, priority);
			} else {
				notifyTaskLoaded(fileName, type, parameters, callback, reference);
			}
		}
	}

	private void resetStats() {
		if (asyncQueue.size == 0 && syncQueue.size == 0 && waitingQueue.size == 0) {
			loaded = 0;
			toLoad = 0;
		}
	}

	private <T> void addToQueue(String fileName, Class<T> type, AssetLoaderParameters<T> parameters,
			AsyncCallback<T> callback, int priority) {
		AssetLoadingTask2<?> queuedTask = findTaskInQueues(fileName);
		if (queuedTask == null) {
			toLoad++;
			asyncQueue.add(AssetLoadingTask2.obtain(this, callback, fileName, type, parameters, priority));
			asyncQueue.sort();
			if (asyncQueue.size == 1) {
				nextTask();
			}
		} else if (queuedTask.type != type) {
			String message = "Asset with name '" + fileName
					+ "' already in preload queue, but has different type (expected: " + type.getSimpleName()
					+ ", found: " + queuedTask.type.getSimpleName() + ")";
			handleLoadException(callback, new GdxRuntimeException(message));
		} else {
			queuedTask.renice(priority);
		}
	}

	private AssetLoadingTask2<?> findTaskInQueues(String fileName) {
		AssetLoadingTask2<?> task = findTaskInQueue(asyncQueue, fileName);
		if (task != null) {
			return task;
		}
		task = findTaskInQueue(waitingQueue, fileName);
		if (task != null) {
			return task;
		}
		return findTaskInQueue(syncQueue, fileName);
	}

	private static AssetLoadingTask2<?> findTaskInQueue(Array<AssetLoadingTask2<?>> queue, String fileName) {
		for (int i = 0; i < queue.size; i++) {
			AssetLoadingTask2<?> task = queue.get(i);
			if (task.fileName.equals(fileName)) {
				return task;
			}
		}

		return null;
	}

	private static <T> void handleLoadException(AsyncCallback<T> callback, GdxRuntimeException exception) {
		if (callback == null) {
			throw exception;
		} else {
			callback.onException(exception);
		}
	}

	private <T> void notifyTaskLoaded(String fileName, Class<T> type, AssetLoaderParameters<T> parameters,
			AsyncCallback<T> callback, AssetReference reference) {
		Object asset = reference.asset;
		Class<?> otherType = asset.getClass();

		if (otherType != type) {
			String message = "Asset with name '" + fileName + "' already loaded, but has different type (expected: "
					+ type.getSimpleName() + ", found: " + otherType.getSimpleName() + ")";
			handleLoadException(callback, new GdxRuntimeException(message));
		} else {
			if (parameters != null && parameters.loadedCallback != null) {
				parameters.loadedCallback.finishedLoading(this, fileName, type);
			}

			if (callback != null) {
				callback.onProgress(1);
				callback.onSuccess(ValueUtils.<T> cast(asset));
			}
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public void unload(String fileName) {
		synchronized (lock) {
			AssetLoadingTask2<?> task = findTaskInQueues(fileName);
			if (task == null) {
				unloadAsset(fileName);
			} else {
				cancleTask(task);
			}
		}
	}

	private void cancleTask(AssetLoadingTask2<?> task) {
		// TODO Auto-generated method stub

	}

	void unloadAsset(String fileName) {
		synchronized (lock) {
			AssetReference reference = assetsByFileName.get(fileName);
			if (reference == null) {
				throw new GdxRuntimeException("Asset not loaded: " + fileName);
			}

			reference.decRefCount();
			Array<String> dependencies = assetDependencies.get(fileName);
			if (dependencies != null) {
				for (String dependency : dependencies) {
					if (isLoaded(dependency)) {
						unloadAsset(dependency);
					}
				}
			}

			if (reference.refCount <= 0) {
				Object asset = reference.asset;
				DisposablesService.tryDispose(asset);
				fileNamesByAsset.remove(asset);
				assetsByFileName.remove(fileName);
				assetDependencies.remove(fileName);
				reference.free();
			}
		}
	}

	@Override
	public boolean update(int millis) {
		long endTime = TimeUtils.millis() + millis;
		while (true) {
			boolean done = update();
			if (done || TimeUtils.millis() > endTime) {
				return done;
			}
			ThreadUtils.yield();
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public boolean update() {
		synchronized (lock) {
			for (int i = 0; i < syncQueue.size; i++) {
				AssetLoadingTask2<?> task = syncQueue.get(i);
				task.loadSync();
			}
			syncQueue.clear();

			if (allTasksHalted() && asyncQueue.size > 0) {
				nextTask();
			}

			return asyncQueue.size == 0 && syncQueue.size == 0 && waitingQueue.size == 0;
		}
	}

	private boolean allTasksHalted() {
		for (int i = 0; i < waitingQueue.size; i++) {
			AssetLoadingTask2<?> task = waitingQueue.get(i);
			if (task.loadingState == LoadingState.ready) {
				return false;
			}
		}
		return true;
	}

	private void nextTask() {
		AssetLoadingTask2<?> nextTask = asyncQueue.removeIndex(0);

		if (isLoaded(nextTask.fileName)) {
			AssetReference reference = assetsByFileName.get(nextTask.fileName);
			reference.incRefCount();
			incrementRefCountedDependencies(nextTask.fileName);

			if (nextTask.params != null && nextTask.params.loadedCallback != null) {
				nextTask.params.loadedCallback.finishedLoading(this, nextTask.fileName, nextTask.type);
			}

			if (nextTask.parent == null) {
				loaded++;
			}

			nextTask.free();
			if (asyncQueue.size > 0) {
				nextTask();
			}
		} else {
			waitingQueue.add(nextTask);
			executor.submit(nextTask);
		}
	}

	<T> void readyForAsyncLoading(AssetLoadingTask2<T> task) {
		synchronized (lock) {
			waitingQueue.removeValue(task, true);
			asyncQueue.add(task);
			asyncQueue.sort();
		}
	}

	<T> void readyForSyncLoading(AssetLoadingTask2<T> task) {
		synchronized (lock) {
			waitingQueue.removeValue(task, true);
			syncQueue.add(task);
			syncQueue.sort();
		}
	}

	<T> void finished(AssetLoadingTask2<T> task) {
		synchronized (lock) {
			waitingQueue.removeValue(task, true);
			if (task.parent == null) {
				loaded++;
			}
			task.free();
		}
	}

	@Override
	protected <T> void addAsset(final String fileName, Class<T> type, T asset) {
		fileNamesByAsset.put(asset, fileName);
		assetsByFileName.put(fileName, AssetReference.obtain(asset));
	}

	@Override
	public void finishLoading() {
		while (!update()) {
			ThreadUtils.yield();
		}
	}

	@Override
	public void finishLoadingAsset(String fileName) {
		while (!isLoaded(fileName)) {
			update();
			ThreadUtils.yield();
		}
	}

	void injectDependencies(Array<AssetLoadingTask2<?>> dependencies) {
		synchronized (lock) {
			ObjectSet<String> injected = this.injected;
			for (AssetLoadingTask2<?> dependency : dependencies) {
				String fileName = dependency.fileName;
				if (injected.contains(fileName)) {
					continue;
				}
				injected.add(fileName);
				injectDependency(dependency);
			}
			injected.clear();
		}
	}

	private <T> void injectDependency(AssetLoadingTask2<T> dependency) {
		String parentFilename = dependency.parent.fileName;
		String fileName = dependency.fileName;

		addDependency(parentFilename, fileName);

		if (isLoaded(fileName)) {
			AssetReference reference = assetsByFileName.get(fileName);
			reference.incRefCount();
			incrementRefCountedDependencies(fileName);

			if (dependency.params != null && dependency.params.loadedCallback != null) {
				dependency.params.loadedCallback.finishedLoading(this, dependency.fileName, dependency.type);
			}
		} else {
			asyncQueue.add(dependency);
			asyncQueue.sort();
		}
	}

	private void addDependency(String parentFileName, String fileName) {
		Array<String> dependencies = assetDependencies.get(parentFileName);
		if (dependencies == null) {
			dependencies = new Array<String>();
			assetDependencies.put(parentFileName, dependencies);
		}
		dependencies.add(fileName);
	}

	@Override
	protected final void taskFailed(@SuppressWarnings("rawtypes") AssetDescriptor assetDesc, RuntimeException ex) {
		throw new UnsupportedOperationException();
	}

	private void incrementRefCountedDependencies(String parent) {
		Array<String> dependencies = assetDependencies.get(parent);
		if (dependencies == null) {
			return;
		}

		for (String dependency : dependencies) {
			AssetReference reference = assetsByFileName.get(dependency);
			reference.incRefCount();
			incrementRefCountedDependencies(dependency);
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public int getLoadedAssets() {
		synchronized (lock) {
			return assetsByFileName.size;
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public int getQueuedAssets() {
		synchronized (lock) {
			return asyncQueue.size;
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public float getProgress() {
		synchronized (lock) {
			if (toLoad == 0) {
				return 1;
			} else {
				return Math.min(1, loaded / (float) toLoad);
			}
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public void setErrorListener(AssetErrorListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("sync-override")
	public void dispose() {
		synchronized (lock) {
			clear();
			DisposablesService.dispose(executor);
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public void clear() {
		synchronized (lock) {
			GdxRuntimeException message = new GdxRuntimeException("Clear requested on assetManager.");
			clearQueue(asyncQueue, message);
			asyncQueue.clear();
			clearQueue(waitingQueue, message);
			waitingQueue.clear();
			clearQueue(syncQueue, message);
			syncQueue.clear();

			for (AssetReference reference : assetsByFileName.values()) {
				DisposablesService.tryDispose(reference.asset);
				reference.free();
			}

			assetsByFileName.clear();
			fileNamesByAsset.clear();
			assetDependencies.clear();
			injected.clear();

			loaded = 0;
			toLoad = 0;
		}
	}

	private static void clearQueue(Array<AssetLoadingTask2<?>> queue, GdxRuntimeException message) {
		for (int i = 0; i < queue.size; i++) {
			AssetLoadingTask2<?> task = queue.get(i);
			if (task.callback != null) {
				task.callback.onException(message);
				task.free();
			}
		}
	}

	@Override
	public Logger getLogger() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLogger(Logger logger) {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("sync-override")
	public int getReferenceCount(String fileName) {
		synchronized (lock) {
			return assetsByFileName.get(fileName).refCount;
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public void setReferenceCount(String fileName, int refCount) {
		synchronized (lock) {
			assetsByFileName.get(fileName).refCount = refCount;
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public String getDiagnostics() {
		synchronized (lock) {
			StringBuilder builder = new StringBuilder();
			for (String fileName : assetsByFileName.keys()) {
				AssetReference reference = assetsByFileName.get(fileName);

				builder.append(fileName);
				builder.append(", ");
				builder.append(reference.asset.getClass().getSimpleName());
				builder.append(", refCount: ");
				builder.append(reference.refCount);

				Array<String> dependencies = assetDependencies.get(fileName);
				if (dependencies != null) {
					builder.append(", deps: [");
					for (String dep : dependencies) {
						builder.append(dep);
						builder.append(",");
					}

					builder.append("]");
				}

				builder.append("\n");
			}

			return builder.toString();
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public Array<String> getAssetNames() {
		synchronized (lock) {
			return assetsByFileName.keys().toArray();
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public Array<String> getDependencies(String fileName) {
		synchronized (lock) {
			return assetDependencies.get(fileName);
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public Class<?> getAssetType(String fileName) {
		synchronized (lock) {
			return assetsByFileName.get(fileName).getClass();
		}
	}
}
