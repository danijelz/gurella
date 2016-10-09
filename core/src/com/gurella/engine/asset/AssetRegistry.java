package com.gurella.engine.asset;

import static com.gurella.engine.asset.AssetLoadingTask.obtain;

import java.util.Iterator;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.CubemapLoader;
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
import com.badlogic.gdx.graphics.Cubemap;
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
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.asset.AssetLoadingTask.LoadingState;
import com.gurella.engine.asset.loader.audio.SoundClipLoader;
import com.gurella.engine.asset.loader.object.JsonObjectLoader;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.audio.SoundClip;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.material.MaterialDescriptor;
import com.gurella.engine.graphics.render.RenderTarget;
import com.gurella.engine.graphics.render.RenderTargetLoader;
import com.gurella.engine.graphics.render.shader.template.ShaderTemplate;
import com.gurella.engine.graphics.render.shader.template.ShaderTemplateLoader;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.subscriptions.base.resource.ResourceActivityListener;
import com.gurella.engine.utils.Values;

public class AssetRegistry extends AssetManager {
	private static final String clearRequestedMessage = "Clear requested on AssetManager.";
	private static final String assetUnloadedMessage = "Asset unloaded.";
	private static final String loadedAssetInconsistentMessage = "Asset with name '%s' already loaded, but has different type (expected: %s, found: %s).";
	private static final String queuedAssetInconsistentMessage = "Asset with name '%s' already in preload queue, but has different type (expected: %s, found: %s)";

	private final ObjectMap<String, AssetReference> assetsByFileName = new ObjectMap<String, AssetReference>();
	private final IdentityMap<Object, String> fileNamesByAsset = new IdentityMap<Object, String>();

	private final ObjectMap<Class<?>, ObjectMap<String, AssetLoader<?, ?>>> loaders = new ObjectMap<Class<?>, ObjectMap<String, AssetLoader<?, ?>>>();

	private final Array<AssetLoadingTask<?>> asyncQueue = new Array<AssetLoadingTask<?>>();
	private final Array<AssetLoadingTask<?>> waitingQueue = new Array<AssetLoadingTask<?>>();
	private final Array<AssetLoadingTask<?>> syncQueue = new Array<AssetLoadingTask<?>>();
	private AssetLoadingTask<?> currentTask;

	private final AsyncExecutor executor = DisposablesService.add(new AsyncExecutor(1));

	private final Array<Object> tempListeners = new Array<Object>(64);

	private final Object mutex = new Object();

	public AssetRegistry() {
		this(new InternalFileHandleResolver(), true);
	}

	public AssetRegistry(FileHandleResolver resolver) {
		this(resolver, true);
	}

	public AssetRegistry(FileHandleResolver resolver, boolean defaultLoaders) {
		super(resolver, false);
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
			setLoader(Model.class, "g3dj", new G3dModelLoader(new JsonReader(), resolver));
			setLoader(Model.class, "g3db", new G3dModelLoader(new UBJsonReader(), resolver));
			setLoader(Model.class, "obj", new ObjLoader(resolver));
			setLoader(SoundClip.class, "scl", new SoundClipLoader(resolver));
			setLoader(Scene.class, "gscn", new JsonObjectLoader<Scene>(resolver, Scene.class));
			setLoader(SceneNode2.class, "pref", new JsonObjectLoader<SceneNode2>(resolver, SceneNode2.class));
			setLoader(MaterialDescriptor.class, "gmat",
					new JsonObjectLoader<MaterialDescriptor>(resolver, MaterialDescriptor.class));
			setLoader(ShaderTemplate.class, "glslt", new ShaderTemplateLoader(resolver));
			setLoader(RenderTarget.class, "rt", new RenderTargetLoader(resolver));
			setLoader(Cubemap.class, "zktx", new CubemapLoader(resolver));
		}
	}

	// TODO make package private and only accessible for prefabs and bundles
	public <T> void put(T asset, String fileName) {
		// TODO check if asset in other file -> renamed(oldFileName, newFileName);
		AssetReference reference = AssetReference.obtain();
		reference.asset = asset;
		// TODO initial refCount, dependencies and dependents
		reference.refCount = 1;
		fileNamesByAsset.put(asset, fileName);
		assetsByFileName.put(fileName, reference);
		DisposablesService.tryAdd(asset);
	}

	@Override
	@SuppressWarnings("sync-override")
	public <T> T get(String fileName) {
		return Values.cast(get(fileName, Object.class));
	}

	@Override
	@SuppressWarnings("sync-override")
	public <T> T get(AssetDescriptor<T> assetDescriptor) {
		return get(assetDescriptor.fileName, assetDescriptor.type);
	}

	@Override
	@SuppressWarnings("sync-override")
	public <T> T get(String fileName, Class<T> type) {
		synchronized (mutex) {
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
		synchronized (mutex) {
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
		synchronized (mutex) {
			return fileNamesByAsset.containsKey(asset);
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public <T> String getAssetFileName(T asset) {
		synchronized (mutex) {
			return fileNamesByAsset.get(asset);
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public boolean isLoaded(String fileName) {
		synchronized (mutex) {
			return assetsByFileName.containsKey(fileName);
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public boolean isLoaded(String fileName, @SuppressWarnings("rawtypes") Class type) {
		synchronized (mutex) {
			return assetsByFileName.containsKey(fileName);
		}
	}

	@Override
	public <T> AssetLoader<T, ?> getLoader(final Class<T> type) {
		return getLoader(type, null);
	}

	@Override
	public <T> AssetLoader<T, AssetLoaderParameters<T>> getLoader(final Class<T> type, final String fileName) {
		synchronized (mutex) {
			final ObjectMap<String, AssetLoader<?, ?>> loadersByType = loaders.get(type);
			if (loadersByType == null || loadersByType.size < 1) {
				return null;
			} else if (fileName == null) {
				return Values.cast(loadersByType.get(""));
			}

			String fileExtension = Assets.getFileExtension(fileName);
			if (fileExtension == null) {
				return Values.cast(loadersByType.get(""));
			}

			AssetLoader<T, AssetLoaderParameters<T>> loader = Values.cast(loadersByType.get(fileExtension));
			if (loader == null && loadersByType.size == 1) {
				return Values.cast(loadersByType.get(""));
			} else {
				return loader;
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
	public <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type, String extension,
			AssetLoader<T, P> loader) {
		synchronized (mutex) {
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
		load(fileName, type, null, null, 0, false);
	}

	@Override
	@SuppressWarnings("sync-override")
	public <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
		load(fileName, type, parameter, null, 0, false);
	}

	@Override
	@SuppressWarnings({ "sync-override", "unchecked" })
	public void load(@SuppressWarnings("rawtypes") AssetDescriptor descriptor) {
		load(descriptor.fileName, descriptor.type, descriptor.params, null, 0, false);
	}

	public <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameters, AsyncCallback<T> callback,
			int priority, boolean sticky) {
		synchronized (mutex) {
			AssetReference reference = assetsByFileName.get(fileName);
			if (reference == null) {
				addToQueue(fileName, type, parameters, callback, priority, sticky);
			} else {
				handleAssetLoaded(fileName, type, parameters, callback, reference);
			}
		}
	}

	private <T> void addToQueue(String fileName, Class<T> type, AssetLoaderParameters<T> parameters,
			AsyncCallback<T> callback, int priority, boolean sticky) {
		AssetLoadingTask<T> queuedTask = Values.cast(findTaskInQueues(fileName));
		if (queuedTask == null) {
			asyncQueue.add(obtain(this, callback, fileName, type, parameters, priority, sticky));
			asyncQueue.sort();
		} else if (queuedTask.type != type) {
			String typeName = type.getSimpleName();
			String otherTypeName = queuedTask.type.getSimpleName();
			String message = Values.format(queuedAssetInconsistentMessage, fileName, typeName, otherTypeName);
			notifyLoadException(callback, message);
		} else {
			queuedTask.merge(obtain(this, callback, fileName, type, parameters, priority, sticky));
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
			String typeName = type.getSimpleName();
			String otherTypeName = otherType.getSimpleName();
			String message = Values.format(loadedAssetInconsistentMessage, fileName, typeName, otherTypeName);
			notifyLoadException(callback, message);
		} else {
			reference.incRefCount();
			notifyLoadFinished(fileName, type, parameters, callback, Values.<T> cast(asset));
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

	public <T> void unload(T asset) {
		String fileName = fileNamesByAsset.get(asset);
		if (fileName != null) {
			unloadAsset(fileName);
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public void unload(String fileName) {
		synchronized (mutex) {
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
		Array<ResourceActivityListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(ResourceActivityListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).resourceUnloaded(fileName);
		}

		Object asset = reference.asset;
		fileNamesByAsset.remove(asset);
		assetsByFileName.remove(fileName);
		dereferenceDependencies(fileName, reference);

		if (asset instanceof Poolable) {
			PoolService.free(asset);
		} else {
			DisposablesService.tryDispose(asset);
		}
		reference.free();
	}

	private void dereferenceDependencies(String fileName, AssetReference reference) {
		for (String dependencyFileName : reference.dependencies) {
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

		removeTaskFromQueues(task);
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

	private <T> void removeTaskFromQueues(AssetLoadingTask<T> task) {
		asyncQueue.removeValue(task, true);
		waitingQueue.removeValue(task, true);
		syncQueue.removeValue(task, true);
	}

	public <T> void reload(String fileName, AsyncCallback<T> callback, int priority) {
		AssetLoadingTask<T> queuedTask;
		synchronized (mutex) {
			queuedTask = Values.cast(findTaskInQueues(fileName));
		}

		if (queuedTask != null) {
			finishLoadingAsset(fileName);
		}

		synchronized (mutex) {
			AssetReference reference = assetsByFileName.remove(fileName);
			if (reference == null) {
				return;
			} else {
				Array<ResourceActivityListener> listeners = Values.cast(tempListeners);
				EventService.getSubscribers(ResourceActivityListener.class, listeners);
				for (int i = 0; i < listeners.size; i++) {
					listeners.get(i).resourceReloaded(fileName);
				}

				Object asset = reference.asset;
				fileNamesByAsset.remove(asset);
				Class<T> type = Values.cast(asset.getClass());
				DisposablesService.tryDispose(asset);
				ConfigurableAssetDescriptor<T> descriptor = AssetService.getAssetDescriptor(fileName);
				AssetLoaderParameters<T> parameters = descriptor == null ? null : descriptor.getParameters();
				asyncQueue.add(obtain(this, callback, fileName, type, reference, parameters, priority));
				asyncQueue.sort();
			}
		}
	}

	public void reloadInvalidated() {
		finishLoading();
		synchronized (mutex) {
			Array<ResourceActivityListener> listeners = Values.cast(tempListeners);
			EventService.getSubscribers(ResourceActivityListener.class, listeners);

			Entries<String, AssetReference> entries = assetsByFileName.entries();
			while (entries.hasNext()) {
				Entry<String, AssetReference> entry = entries.next();
				AssetReference reference = entry.value;
				Object asset = reference.asset;
				if (asset instanceof Texture || asset instanceof Cubemap) {
					String fileName = entry.key;
					for (int i = 0; i < listeners.size; i++) {
						listeners.get(i).resourceReloaded(fileName);
					}

					entries.remove();
					fileNamesByAsset.remove(asset);
					Class<Object> type = Values.cast(asset.getClass());
					DisposablesService.tryDispose(asset);
					ConfigurableAssetDescriptor<Object> descriptor = AssetService.getAssetDescriptor(fileName);
					AssetLoaderParameters<Object> params = descriptor == null ? null : descriptor.getParameters();
					asyncQueue.add(obtain(this, null, fileName, type, reference, params, Integer.MAX_VALUE));
				}
			}

			asyncQueue.sort();
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
		synchronized (mutex) {
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
		synchronized (mutex) {
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
		AssetLoadingTask<T> queuedTask = Values.cast(findTaskInQueues(dependency.fileName));
		if (queuedTask == null) {
			asyncQueue.add(dependency);
			asyncQueue.sort();
		} else if (queuedTask.type != dependency.type) {
			String type = dependency.type.getSimpleName();
			String otherType = queuedTask.type.getSimpleName();
			String message = Values.format(queuedAssetInconsistentMessage, dependency.fileName, type, otherType);
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
			String typeName = type.getSimpleName();
			String otherTypeName = otherType.getSimpleName();
			String message = Values.format(loadedAssetInconsistentMessage, fileName, typeName, otherTypeName);
			dependency.exception = new GdxRuntimeException(message);
			exception(dependency);
		} else {
			reference.addDependent(dependency.parent.fileName);
			dependency.setLoadingState(LoadingState.finished);
			dependency.updateProgress();
			notifyLoadFinished(fileName, type, dependency.params, dependency.callback, Values.<T> cast(asset));
		}
	}

	<T> void readyForAsyncLoading(AssetLoadingTask<T> task) {
		synchronized (mutex) {
			if (!waitingQueue.removeValue(task, true)) {
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
		synchronized (mutex) {
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
		synchronized (mutex) {
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
		DisposablesService.tryAdd(asset);
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

		Array<ResourceActivityListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(ResourceActivityListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).resourceLoaded(fileName, asset);
		}
	}

	void exception(AssetLoadingTask<?> task) {
		synchronized (mutex) {
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
			//TODO throw exception on rendering thread
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
		boolean propagated = false;

		AsyncCallback<?> callback = task.callback;
		if (callback != null) {
			callback.onProgress(1);
			callback.onException(exception);
			propagated = true;
		}

		AssetLoadingTask<?> parent = task.parent;
		if (parent != null) {
			propagated |= propagateException(parent, exception);
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

	public <T> T finishLoading(String fileName) {
		while (!isLoaded(fileName)) {
			update();
			ThreadUtils.yield();
		}
		return get(fileName);
	}

	@Override
	protected void taskFailed(@SuppressWarnings("rawtypes") AssetDescriptor assetDesc, RuntimeException ex) {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("sync-override")
	public int getLoadedAssets() {
		synchronized (mutex) {
			return assetsByFileName.size;
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public int getQueuedAssets() {
		synchronized (mutex) {
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
		synchronized (mutex) {
			clear();
			DisposablesService.dispose(executor);
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public void clear() {
		synchronized (mutex) {
			clearQueue(asyncQueue);
			clearQueue(waitingQueue);
			clearQueue(syncQueue);
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
			AssetLoadingTask<Object> task = Values.cast(queue.get(i));
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

		queue.clear();
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
		synchronized (mutex) {
			return assetsByFileName.get(fileName).refCount;
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public void setReferenceCount(String fileName, int refCount) {
		synchronized (mutex) {
			assetsByFileName.get(fileName).refCount = refCount;
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public String getDiagnostics() {
		synchronized (mutex) {
			StringBuilder builder = new StringBuilder();
			for (String fileName : assetsByFileName.keys()) {
				AssetReference reference = assetsByFileName.get(fileName);

				builder.append(fileName);
				builder.append(", ");
				builder.append(reference.asset.getClass().getSimpleName());
				builder.append(", refCount: ");
				builder.append(reference.refCount);

				ObjectSet<String> dependencies = reference.dependencies;
				int size = dependencies.size;
				if (size > 0) {
					builder.append(", deps: [");
					for (Iterator<String> iter = dependencies.iterator(); iter.hasNext();) {
						builder.append(iter.next());
						if (iter.hasNext()) {
							builder.append(",");
						}
					}

					builder.append("]");
				}

				ObjectSet<String> dependents = reference.dependents;
				size = dependents.size;
				if (size > 0) {
					builder.append(", rels: [");
					for (Iterator<String> iter = dependents.iterator(); iter.hasNext();) {
						builder.append(iter.next());
						if (iter.hasNext()) {
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
		synchronized (mutex) {
			return assetsByFileName.keys().toArray();
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public Array<String> getDependencies(String fileName) {
		synchronized (mutex) {
			AssetReference reference = assetsByFileName.get(fileName);
			return reference == null ? null : reference.dependencies.iterator().toArray();
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public Class<?> getAssetType(String fileName) {
		synchronized (mutex) {
			return assetsByFileName.get(fileName).getClass();
		}
	}
}
