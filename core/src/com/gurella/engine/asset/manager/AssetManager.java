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
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.asset.manager.AssetLoadingTask.LoadingState;
import com.gurella.engine.base.resource.AsyncCallback;
import com.gurella.engine.base.resource.AsyncCallback.SimpleAsyncCallback;
import com.gurella.engine.utils.DisposablesService;
import com.gurella.engine.utils.ValueUtils;

/**
 * Loads and stores assets like textures, bitmapfonts, tile maps, sounds, music and so on.
 * 
 * @author mzechner
 */
public class AssetManager extends com.badlogic.gdx.assets.AssetManager {
	private static final String clearRequestedMessage = "Clear requested on AssetManager.";
	private static final String assetUnloadedMessage = "Asset unloaded.";
	private static final String loadedAssetInconsistentMessage = "Asset with name '%1$s' already loaded, but has different type (expected: %2$s, found: %3$s).";
	private static final String queuedAssetInconsistentMessage = "Asset with name '%1$s' already in preload queue, but has different type (expected: %2$s, found: %3$s)";

	private final ObjectMap<String, AssetReference> assetsByFileName = new ObjectMap<String, AssetReference>();
	private final IdentityMap<Object, String> fileNamesByAsset = new IdentityMap<Object, String>();

	private final ObjectMap<Class<?>, ObjectMap<String, AssetLoader<?, ?>>> loaders = new ObjectMap<Class<?>, ObjectMap<String, AssetLoader<?, ?>>>();

	private final Array<AssetLoadingTask<?>> asyncQueue = new Array<AssetLoadingTask<?>>();
	private final Array<AssetLoadingTask<?>> waitingQueue = new Array<AssetLoadingTask<?>>();
	private final Array<AssetLoadingTask<?>> syncQueue = new Array<AssetLoadingTask<?>>();
	private AssetLoadingTask<?> currentTask;

	private final AsyncExecutor executor = DisposablesService.add(new AsyncExecutor(1));

	private final Object lock = new Object();

	public AssetManager() {
		this(new InternalFileHandleResolver());
	}

	public AssetManager(FileHandleResolver resolver) {
		this(resolver, true);
	}

	public AssetManager(FileHandleResolver resolver, boolean defaultLoaders) {
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
		return ValueUtils.cast(get(fileName, Object.class));
	}

	@Override
	@SuppressWarnings("sync-override")
	public <T> T get(AssetDescriptor<T> assetDescriptor) {
		return get(assetDescriptor.fileName, assetDescriptor.type);
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
			} else {
				return ValueUtils.cast(loadersByType.get(getFileExtension(fileName)));
			}
		}
	}

	private static String getFileExtension(final String fileName) {
		if (fileName == null) {
			return "";
		} else {
			int index = fileName.lastIndexOf('.');
			return index > 0 ? fileName.substring(index + 1) : "";
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
	public <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type, String extension,
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

			loadersByType.put(extension == null ? "" : extension, loader);
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public <T> void load(String fileName, Class<T> type) {
		// TODO free SimpleAsyncCallback on finish
		load(fileName, type, null, SimpleAsyncCallback.<T> obtain(), 0);
	}

	@Override
	@SuppressWarnings("sync-override")
	public <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
		load(fileName, type, parameter, SimpleAsyncCallback.<T> obtain(), 0);
	}

	@Override
	@SuppressWarnings({ "sync-override", "unchecked" })
	public void load(@SuppressWarnings("rawtypes") AssetDescriptor descriptor) {
		load(descriptor.fileName, descriptor.type, descriptor.params, SimpleAsyncCallback.<Object> obtain(), 0);
	}

	public <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameters, AsyncCallback<T> callback,
			int priority) {
		synchronized (lock) {
			AssetReference reference = assetsByFileName.get(fileName);
			if (reference == null) {
				addToQueue(fileName, type, parameters, callback, priority);
			} else {
				handleAssetLoaded(fileName, type, parameters, callback, reference);
			}
		}
	}

	private <T> void addToQueue(String fileName, Class<T> type, AssetLoaderParameters<T> parameters,
			AsyncCallback<T> callback, int priority) {
		AssetLoadingTask<T> queuedTask = ValueUtils.cast(findTaskInQueues(fileName));
		if (queuedTask == null) {
			asyncQueue.add(AssetLoadingTask.obtain(this, callback, fileName, type, parameters, priority));
			asyncQueue.sort();
		} else if (queuedTask.type != type) {
			String message = String.format(queuedAssetInconsistentMessage, fileName, type.getSimpleName(),
					queuedTask.type.getSimpleName());
			notifyLoadException(callback, message);
		} else {
			queuedTask.merge(AssetLoadingTask.obtain(this, callback, fileName, type, parameters, priority));
			asyncQueue.sort();
		}
	}

	private AssetLoadingTask<?> findTaskInQueues(String fileName) {
		AssetLoadingTask<?> task = findTaskInQueue(asyncQueue, fileName);
		if (task != null) {
			return task;
		}
		task = findTaskInQueue(waitingQueue, fileName);
		if (task != null) {
			return task;
		}
		return findTaskInQueue(syncQueue, fileName);
	}

	private static AssetLoadingTask<?> findTaskInQueue(Array<AssetLoadingTask<?>> queue, String fileName) {
		for (int i = 0; i < queue.size; i++) {
			AssetLoadingTask<?> task = queue.get(i);
			if (task.fileName.equals(fileName)) {
				return task;
			}
		}

		return null;
	}

	private static <T> void notifyLoadException(AsyncCallback<T> callback, String message) {
		if (callback == null) {
			throw new GdxRuntimeException(message);
		} else {
			callback.onException(new GdxRuntimeException(message));
		}
	}

	private <T> void handleAssetLoaded(String fileName, Class<T> type, AssetLoaderParameters<T> parameters,
			AsyncCallback<T> callback, AssetReference reference) {
		Object asset = reference.asset;
		Class<?> otherType = asset.getClass();

		if (otherType != type) {

			String message = String.format(loadedAssetInconsistentMessage, fileName, type.getSimpleName(),
					otherType.getSimpleName());
			notifyLoadException(callback, message);
		} else {
			reference.incRefCount();
			notifyLoadFinished(fileName, type, parameters, callback, ValueUtils.<T> cast(asset));
		}
	}

	private <T> void notifyLoadFinished(String fileName, Class<T> type, AssetLoaderParameters<T> params,
			AsyncCallback<T> callback, T asset) {
		if (params != null && params.loadedCallback != null) {
			params.loadedCallback.finishedLoading(this, fileName, type);
		}

		if (callback != null) {
			callback.onProgress(1);
			callback.onSuccess(asset);
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public void unload(String fileName) {
		synchronized (lock) {
			AssetLoadingTask<?> task = findTaskInQueues(fileName);
			if (task == null) {
				unloadAsset(fileName);
			} else {
				cancleTask(task);
			}
		}
	}

	private void unloadAsset(String fileName) {
		AssetReference reference = assetsByFileName.get(fileName);
		if (reference == null) {
			return;
		}

		reference.decRefCount();
		if (!reference.isReferenced()) {
			unloadAsset(fileName, reference);
		}
	}

	private void unloadAsset(String fileName, AssetReference reference) {
		Object asset = reference.asset;
		DisposablesService.tryDispose(asset);
		fileNamesByAsset.remove(asset);
		assetsByFileName.remove(fileName);
		dereferenceDependencies(fileName, reference);
		reference.free();
	}

	private void dereferenceDependencies(String fileName, AssetReference reference) {
		Array<String> dependencies = reference.dependencies;
		for (int i = 0; i < dependencies.size; i++) {
			String dependencyFileName = dependencies.get(i);
			AssetReference dependencyReference = assetsByFileName.get(dependencyFileName);
			dependencyReference.removeDependent(fileName);
			if (!dependencyReference.isReferenced()) {
				unloadAsset(dependencyFileName, dependencyReference);
			}
		}
	}

	private <T> void cancleTask(AssetLoadingTask<T> task) {
		AssetReference reference = task.reference;
		reference.decRefCount();
		if (reference.isReferenced()) {
			return;
		}

		asyncQueue.removeValue(task, true);
		waitingQueue.removeValue(task, true);
		syncQueue.removeValue(task, true);
		unloadLoadedDependencies(task);
		AsyncCallback<T> callback = task.callback;
		if (callback != null) {
			callback.onCancled(assetUnloadedMessage);
		}

		Array<AssetLoadingTask<T>> concurentTasks = task.concurentTasks;
		for (int i = 0; i < concurentTasks.size; i++) {
			AssetLoadingTask<T> concurentTask = concurentTasks.get(i);
			callback = concurentTask.callback;
			if (callback != null) {
				callback.onCancled(assetUnloadedMessage);
			}
		}

		task.free();
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
			processSyncQueue();
			processNextAsyncTask();
			return asyncQueue.size == 0 && syncQueue.size == 0 && waitingQueue.size == 0;
		}
	}

	private void processSyncQueue() {
		while (syncQueue.size > 0) {
			AssetLoadingTask<?> task = syncQueue.removeIndex(0);
			try {
				task.loadSync();
			} catch (Exception e) {
				handleTaskException(task);
				continue;
			}
			finishTask(task);
		}
	}

	private void processNextAsyncTask() {
		if (currentTask == null && asyncQueue.size > 0) {
			AssetLoadingTask<?> nextTask = asyncQueue.removeIndex(0);
			if (isLoaded(nextTask.fileName)) {
				throw new IllegalStateException();
			} else {
				waitingQueue.add(nextTask);
				currentTask = nextTask;
				executor.submit(nextTask);
			}
		}
	}

	void waitingForDependencies(AssetLoadingTask<?> task) {
		synchronized (lock) {
			currentTask = null;
			task.setLoadingState(LoadingState.waitingForDependencies);
			Array<AssetLoadingTask<?>> dependencies = task.dependencies;
			for (int i = 0; i < dependencies.size; i++) {
				AssetLoadingTask<?> dependency = dependencies.get(i);
				AssetReference reference = assetsByFileName.get(dependency.fileName);
				if (reference == null) {
					addToQueue(dependency);
				} else {
					handleAssetLoaded(dependency, reference);
				}
			}
		}
	}

	private <T> void addToQueue(AssetLoadingTask<T> dependency) {
		AssetLoadingTask<T> queuedTask = ValueUtils.cast(findTaskInQueues(dependency.fileName));
		if (queuedTask == null) {
			asyncQueue.add(dependency);
			asyncQueue.sort();
		} else if (queuedTask.type != dependency.type) {
			String message = String.format(queuedAssetInconsistentMessage, dependency.fileName,
					dependency.type.getSimpleName(), queuedTask.type.getSimpleName());
			dependency.exception = new GdxRuntimeException(message);
			exception(dependency);
		} else {
			queuedTask.merge(dependency);
			asyncQueue.sort();
		}
	}

	private <T> void handleAssetLoaded(AssetLoadingTask<T> dependency, AssetReference reference) {
		Object asset = reference.asset;
		String fileName = dependency.fileName;
		Class<T> type = dependency.type;
		Class<?> otherType = asset.getClass();

		if (type != otherType) {
			String message = String.format(loadedAssetInconsistentMessage, fileName, type.getSimpleName(),
					otherType.getSimpleName());
			dependency.exception = new GdxRuntimeException(message);
			exception(dependency);
		} else {
			reference.addDependent(dependency.parent.fileName);
			notifyLoadFinished(fileName, type, dependency.params, dependency.callback, ValueUtils.<T> cast(asset));
		}
	}

	<T> void readyForAsyncLoading(AssetLoadingTask<T> task) {
		synchronized (lock) {
			if (!waitingQueue.removeValue(task, true) || currentTask != task) {
				throw new IllegalStateException();
			}

			currentTask = null;
			task.setLoadingState(LoadingState.readyForAsyncLoading);
			task.updateProgress();
			asyncQueue.insert(0, task);
			asyncQueue.sort();
		}
	}

	<T> void readyForSyncLoading(AssetLoadingTask<T> task) {
		synchronized (lock) {
			if (!waitingQueue.removeValue(task, true) || currentTask != task) {
				throw new IllegalStateException();
			}

			currentTask = null;
			task.setLoadingState(LoadingState.readyForSyncLoading);
			task.updateProgress();
			syncQueue.add(task);
			syncQueue.sort();
		}
	}

	<T> void finished(AssetLoadingTask<T> task) {
		synchronized (lock) {
			if (!waitingQueue.removeValue(task, true) || currentTask != task) {
				throw new IllegalStateException();
			}

			currentTask = null;
			finishTask(task);
		}
	}

	private <T> void finishTask(AssetLoadingTask<T> task) {
		task.setLoadingState(LoadingState.finished);
		task.updateProgress();
		String fileName = task.fileName;
		AssetReference reference = task.reference;
		T asset = reference.getAsset();
		fileNamesByAsset.put(asset, fileName);
		assetsByFileName.put(fileName, reference);
		notifyTaskFinished(task, asset);
		task.reference = null;
		task.free();
	}

	private <T> void notifyTaskFinished(AssetLoadingTask<T> task, T asset) {
		String fileName = task.fileName;
		Class<T> type = task.type;

		notifyLoadFinished(fileName, type, task.params, task.callback, asset);
		Array<AssetLoadingTask<T>> concurentTasks = task.concurentTasks;
		for (int i = 0; i < concurentTasks.size; i++) {
			AssetLoadingTask<T> competingTask = concurentTasks.get(i);
			notifyLoadFinished(fileName, type, competingTask.params, competingTask.callback, asset);
		}
	}

	void exception(AssetLoadingTask<?> task) {
		synchronized (lock) {
			if (!waitingQueue.removeValue(task, true) || currentTask != task) {
				throw new IllegalStateException();
			}

			currentTask = null;
			handleTaskException(task);
		}
	}

	private void handleTaskException(AssetLoadingTask<?> task) {
		task.setLoadingState(LoadingState.error);
		unloadLoadedDependencies(task);
		Throwable ex = task.exception;
		boolean propagated = propagateException(task, ex);
		task.free();
		if (!propagated) {
			throw ex instanceof RuntimeException ? (RuntimeException) ex : new GdxRuntimeException(ex);
		}
	}

	private void unloadLoadedDependencies(AssetLoadingTask<?> task) {
		String fileName = task.fileName;
		Array<AssetLoadingTask<?>> dependencies = task.dependencies;

		for (int i = 0; i < dependencies.size; i++) {
			AssetLoadingTask<?> dependency = dependencies.get(i);
			if (dependency.loadingState == LoadingState.finished) {
				String dependencyFileName = dependency.fileName;
				dereferenceDependencies(fileName, assetsByFileName.get(dependencyFileName));
			} else {
				unloadLoadedDependencies(dependency);
			}
		}
	}

	private <T> boolean propagateException(AssetLoadingTask<T> task, Throwable exception) {
		boolean propagated = true;
		AsyncCallback<?> callback = task.callback;
		if (callback != null) {
			callback.onProgress(1);
			callback.onException(exception);
		} else if (task.parent == null) {
			propagated = false;
		}

		Array<AssetLoadingTask<T>> concurentTasks = task.concurentTasks;
		for (int i = 0; i < concurentTasks.size; i++) {
			propagated |= propagateException(concurentTasks.get(i), exception);
		}

		return propagated;
	}

	@Override
	protected <T> void addAsset(final String fileName, Class<T> type, T asset) {
		throw new UnsupportedOperationException();
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

	@Override
	protected final void taskFailed(@SuppressWarnings("rawtypes") AssetDescriptor assetDesc, RuntimeException ex) {
		throw new UnsupportedOperationException();
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
			return asyncQueue.size + waitingQueue.size + syncQueue.size;
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public float getProgress() {
		throw new UnsupportedOperationException();
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
			clearQueue(asyncQueue);
			asyncQueue.clear();
			clearQueue(waitingQueue);
			waitingQueue.clear();
			clearQueue(syncQueue);
			syncQueue.clear();
			currentTask = null;

			for (AssetReference reference : assetsByFileName.values()) {
				DisposablesService.tryDispose(reference.asset);
				reference.free();
			}

			assetsByFileName.clear();
			fileNamesByAsset.clear();
		}
	}

	private static void clearQueue(Array<AssetLoadingTask<?>> queue) {
		for (int i = 0; i < queue.size; i++) {
			AssetLoadingTask<Object> task = ValueUtils.cast(queue.get(i));
			AsyncCallback<Object> callback = task.callback;
			if (callback != null) {
				callback.onCancled(clearRequestedMessage);
			}

			Array<AssetLoadingTask<Object>> concurentTasks = task.concurentTasks;
			for (int j = 0; j < concurentTasks.size; j++) {
				AssetLoadingTask<Object> concurentTask = concurentTasks.get(j);
				callback = concurentTask.callback;
				if (callback != null) {
					callback.onCancled(clearRequestedMessage);
				}
			}

			if (task.parent == null) {
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

				Array<String> dependencies = reference.dependencies;
				int size = dependencies.size;
				if (size > 0) {
					builder.append(", deps: [");
					for (int i = 0; i < size; i++) {
						builder.append(dependencies.get(i));
						if (i < size - 1) {
							builder.append(",");
						}
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
			AssetReference reference = assetsByFileName.get(fileName);
			return reference == null ? null : reference.dependencies;
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
