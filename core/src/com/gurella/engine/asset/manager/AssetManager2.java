package com.gurella.engine.asset.manager;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.RefCountedContainer;
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
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.asset.manager.AssetLoadingTask.DependencyCallback;
import com.gurella.engine.base.resource.AsyncCallback;
import com.gurella.engine.utils.DisposablesService;
import com.gurella.engine.utils.SynchronizedPools;

/**
 * Loads and stores assets like textures, bitmapfonts, tile maps, sounds, music
 * and so on.
 * 
 * @author mzechner
 */
public class AssetManager2 extends com.badlogic.gdx.assets.AssetManager {
	private final ObjectMap<String, RefCountedContainer> assetsByFileName = new ObjectMap<String, RefCountedContainer>();
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

	Logger log = new Logger("gurella.AssetManager", Application.LOG_NONE);

	/** Creates a new AssetManager with all default loaders. */
	public AssetManager2() {
		this(new InternalFileHandleResolver());
	}

	/** Creates a new AssetManager with all default loaders. */
	public AssetManager2(FileHandleResolver resolver) {
		this(resolver, true);
	}

	/**
	 * Creates a new AssetManager with optionally all default loaders. If you
	 * don't add the default loaders then you do have to manually add the
	 * loaders you need, including any loaders they might depend on.
	 * 
	 * @param defaultLoaders
	 *            whether to add the default loaders
	 */
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

	/**
	 * @param fileName the asset file name
	 * @return the asset
	 */
	@Override
	@SuppressWarnings("sync-override")
	public <T> T get(String fileName) {
		@SuppressWarnings("unchecked")
		T value = (T) get(fileName, Object.class);
		return value;
	}

	/**
	 * @param fileName the asset file name
	 * @param type the asset type
	 * @return the asset
	 */
	@Override
	@SuppressWarnings("sync-override")
	public <T> T get(String fileName, Class<T> type) {
		synchronized (lock) {
			RefCountedContainer assetContainer = assetsByFileName.get(fileName);
			if (assetContainer == null) {
				throw new GdxRuntimeException("Asset not loaded: " + fileName);
			}

			T asset = assetContainer.getObject(type);
			if (asset == null) {
				throw new GdxRuntimeException("Asset not loaded: " + fileName);
			}

			return asset;
		}
	}

	/**
	 * @param type the asset type
	 * @return all the assets matching the specified type
	 */
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

	/**
	 * @param assetDescriptor the asset descriptor
	 * @return the asset
	 */
	@Override
	@SuppressWarnings("sync-override")
	public <T> T get(AssetDescriptor<T> assetDescriptor) {
		return get(assetDescriptor.fileName, assetDescriptor.type);
	}

	/**
	 * Removes the asset and all its dependencies, if they are not used by other assets.
	 * 
	 * @param fileName the file name
	 */
	@Override
	@SuppressWarnings("sync-override")
	public void unload(String fileName) {
		synchronized (lock) {
			// check if it's currently processed (and the first element in the
			// stack, thus not a dependency)
			// and cancel if necessary
			if (currentTask != null && currentTask.fileName.equals(fileName)) {
				currentTask.cancel = true;
				log.debug("Unload (from tasks): " + fileName);
				return;
			}

			// check if it's in the queue
			for (int i = 0; i < asyncQueue.size; i++) {
				if (asyncQueue.get(i).fileName.equals(fileName)) {
					toLoad--;
					asyncQueue.removeIndex(i).free();
					log.debug("Unload (from queue): " + fileName);
					return;
				}
			}

			RefCountedContainer assetRef = assetsByFileName.get(fileName);
			if (assetRef == null) {
				throw new GdxRuntimeException("Asset not loaded: " + fileName);
			}

			// if it is reference counted, decrement ref count and check if we can really get rid of it.
			assetRef.decRefCount();
			if (assetRef.getRefCount() <= 0) {
				log.debug("Unload (dispose): " + fileName);
				Object asset = assetRef.getObject(Object.class);
				if (asset instanceof Disposable) {
					DisposablesService.dispose(((Disposable) asset));
				}

				fileNamesByAsset.remove(asset);
				assetsByFileName.remove(fileName);
			} else {
				log.debug("Unload (decrement): " + fileName);
			}

			// remove any dependencies (or just decrement their ref count).
			Array<String> dependencies = assetDependencies.get(fileName);
			if (dependencies != null) {
				for (String dependency : dependencies) {
					if (isLoaded(dependency)) {
						unload(dependency);
					}
				}
			}

			// remove dependencies if ref count < 0
			if (assetRef.getRefCount() <= 0) {
				assetDependencies.remove(fileName);
			}
		}
	}

	/**
	 * @param asset the asset
	 * @return whether the asset is contained in this manager
	 */
	@Override
	@SuppressWarnings("sync-override")
	public <T> boolean containsAsset(T asset) {
		synchronized (lock) {
			return fileNamesByAsset.containsKey(asset);
		}
	}

	/**
	 * @param asset the asset
	 * @return the filename of the asset or null
	 */
	@Override
	@SuppressWarnings("sync-override")
	public <T> String getAssetFileName(T asset) {
		synchronized (lock) {
			return fileNamesByAsset.get(asset);
		}
	}

	/**
	 * @param fileName the file name of the asset
	 * @return whether the asset is loaded
	 */
	@Override
	@SuppressWarnings("sync-override")
	public boolean isLoaded(String fileName) {
		synchronized (lock) {
			return assetsByFileName.containsKey(fileName);
		}
	}

	/**
	 * @param fileName the file name of the asset
	 * @return whether the asset is loaded
	 */
	@Override
	@SuppressWarnings("sync-override")
	public boolean isLoaded(String fileName, @SuppressWarnings("rawtypes") Class type) {
		synchronized (lock) {
			return assetsByFileName.containsKey(fileName);
		}
	}

	/**
	 * Returns the default loader for the given type
	 * 
	 * @param type The type of the loader to get
	 * @return The loader capable of loading the type, or null if none exists
	 */
	@Override
	public <T> AssetLoader<T, ?> getLoader(final Class<T> type) {
		return getLoader(type, null);
	}

	/**
	 * Returns the loader for the given type and the specified filename. If no
	 * loader exists for the specific filename, the default loader for that type
	 * is returned.
	 * 
	 * @param type The type of the loader to get
	 * @param fileName The filename of the asset to get a loader for, or null to get
	 *            the default loader
	 * @return The loader capable of loading the type and filename, or null if none exists
	 */
	@Override
	public <T> AssetLoader<T, AssetLoaderParameters<T>> getLoader(final Class<T> type, final String fileName) {
		synchronized (lock) {
			final ObjectMap<String, AssetLoader<?, ?>> loaders = this.loaders.get(type);
			if (loaders == null || loaders.size < 1) {
				return null;
			}

			if (fileName == null) {
				@SuppressWarnings("unchecked")
				AssetLoader<T, AssetLoaderParameters<T>> casted = (AssetLoader<T, AssetLoaderParameters<T>>) loaders
						.get("");
				return casted;
			}

			AssetLoader<T, AssetLoaderParameters<T>> result = null;
			int length = -1;
			for (ObjectMap.Entry<String, AssetLoader<?, ?>> entry : loaders.entries()) {
				if (entry.key.length() > length && fileName.endsWith(entry.key)) {
					@SuppressWarnings("unchecked")
					AssetLoader<T, AssetLoaderParameters<T>> casted = (AssetLoader<T, AssetLoaderParameters<T>>) entry.value;
					result = casted;
					length = entry.key.length();
				}
			}

			return result;
		}
	}

	/**
	 * Adds the given asset to the loading queue of the AssetManager.
	 * 
	 * @param fileName the file name (interpretation depends on {@link AssetLoader})
	 * @param type the type of the asset.
	 */
	@Override
	@SuppressWarnings("sync-override")
	public <T> void load(String fileName, Class<T> type) {
		load(fileName, type, null, null, 0);
	}

	/**
	 * Adds the given asset to the loading queue of the AssetManager.
	 * 
	 * @param fileName the file name (interpretation depends on {@link AssetLoader})
	 * @param type the type of the asset.
	 * @param parameter parameters for the AssetLoader.
	 */
	@Override
	@SuppressWarnings("sync-override")
	public <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
		load(fileName, type, parameter, null, 0);
	}

	public <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter, AsyncCallback<T> callback,
			int priority) {
		synchronized (lock) {
			// reset stats
			if (asyncQueue.size == 0 && syncQueue.size == 0) {
				loaded = 0;
				toLoad = 0;
			}

			// check if an asset with the same name but a different type has already been added.
			// check preload queue
			for (int i = 0; i < asyncQueue.size; i++) {
				AssetLoadingTask2<?> task = asyncQueue.get(i);
				if (task.fileName.equals(fileName) && !task.type.equals(type)) {
					throw new GdxRuntimeException("Asset with name '" + fileName
							+ "' already in preload queue, but has different type (expected: " + type.getSimpleName()
							+ ", found: " + task.type.getSimpleName() + ")");
				}
			}

			// check currentTask
			for (int i = 0; i < syncQueue.size; i++) {
				AssetLoadingTask2<?> task = syncQueue.get(i);
				if (task.fileName.equals(fileName) && !task.type.equals(type)) {
					throw new GdxRuntimeException("Asset with name '" + fileName
							+ "' already in preload queue, but has different type (expected: " + type.getSimpleName()
							+ ", found: " + task.type.getSimpleName() + ")");
				}
			}

			// check loaded assets
			RefCountedContainer assetRef = assetsByFileName.get(fileName);
			Class<?> otherType = assetRef == null ? null : assetRef.getObject(Object.class).getClass();
			if (assetRef != null && otherType != type)
				throw new GdxRuntimeException(
						"Asset with name '" + fileName + "' already loaded, but has different type (expected: "
								+ type.getSimpleName() + ", found: " + otherType.getSimpleName() + ")");
			toLoad++;

			asyncQueue.insert(0, AssetLoadingTask2.obtain(this, callback, fileName, type, parameter, priority));
			asyncQueue.sort();

			log.debug("Queued: " + fileName + " " + type.getSimpleName());

			update();
		}
	}

	<T> AssetLoader<T, AssetLoaderParameters<T>> findLoader(Class<T> type, String fileName) {
		AssetLoader<T, AssetLoaderParameters<T>> loader = getLoader(type, fileName);
		if (loader == null) {
			throw new GdxRuntimeException("No loader for type: " + type.getSimpleName());
		}
		return loader;
	}

	/**
	 * Adds the given asset to the loading queue of the AssetManager.
	 */
	@Override
	@SuppressWarnings({ "sync-override", "unchecked" })
	public void load(@SuppressWarnings("rawtypes") AssetDescriptor descriptor) {
		load(descriptor.fileName, descriptor.type, descriptor.params, null, 0);
	}

	/**
	 * Updates the AssetManager continuously for the specified number of
	 * milliseconds, yielding the CPU to the loading thread between updates.
	 * This may block for less time if all loading tasks are complete. This may
	 * block for more time if the portion of a single task that happens in the
	 * GL thread takes a long time.
	 * 
	 * @return true if all loading is finished.
	 */
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

	/** Blocks until all assets are loaded. */
	@Override
	public void finishLoading() {
		log.debug("Waiting for loading to complete...");
		while (!update()) {
			ThreadUtils.yield();
		}
		log.debug("Loading complete.");
	}

	/**
	 * Blocks until the specified aseet is loaded.
	 * 
	 * @param fileName
	 *            the file name (interpretation depends on {@link AssetLoader})
	 */
	@Override
	public void finishLoadingAsset(String fileName) {
		log.debug("Waiting for asset to be loaded: " + fileName);
		while (!isLoaded(fileName)) {
			update();
			ThreadUtils.yield();
		}
		log.debug("Asset loaded: " + fileName);
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
			log.debug("Dependency already loaded: " + dependency);
			RefCountedContainer assetRef = assetsByFileName.get(fileName);
			assetRef.incRefCount();
			incrementRefCountedDependencies(fileName);
		} else {
			log.info("Loading dependency: " + dependency);
			asyncQueue.insert(0, dependency);
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

	/** Adds an asset to this AssetManager */
	@Override
	protected <T> void addAsset(final String fileName, Class<T> type, T asset) {
		fileNamesByAsset.put(asset, fileName);
		//TODO garbage
		assetsByFileName.put(fileName, new RefCountedContainer(asset));
	}

	@Override
	protected final void taskFailed(@SuppressWarnings("rawtypes") AssetDescriptor assetDesc, RuntimeException ex) {
	}

	private void incrementRefCountedDependencies(String parent) {
		Array<String> dependencies = assetDependencies.get(parent);
		if (dependencies == null)
			return;

		for (String dependency : dependencies) {
			RefCountedContainer assetRef = assetsByFileName.get(dependency);
			assetRef.incRefCount();
			incrementRefCountedDependencies(dependency);
		}
	}

	/**
	 * Updates the AssetManager, keeping it loading any assets in the preload
	 * queue.
	 * 
	 * @return true if all loading is finished.
	 */
	@Override
	@SuppressWarnings("sync-override")
	public boolean update() {
		synchronized (lock) {
			for (int i = 0; i < syncQueue.size; i++) {
				AssetLoadingTask2<?> task = syncQueue.get(i);
				task.loadSync();
				task.free();
			}
			syncQueue.clear();

			if (asyncQueue.size > 0) {
				nextTask();
			}

			return asyncQueue.size == 0 && asyncQueue.size == 0 && waitingQueue.size == 0;
		}
	}

	/**
	 * Removes a task from the loadQueue and adds it to the task stack. If the
	 * asset is already loaded (which can happen if it was a dependency of a
	 * previously loaded asset) its reference count will be increased.
	 */
	private void nextTask() {
		AssetLoadingTask2<?> nextTask = asyncQueue.removeIndex(0);

		if (isLoaded(nextTask.fileName)) {
			log.debug("Already loaded: " + nextTask);
			RefCountedContainer assetRef = assetsByFileName.get(nextTask.fileName);
			assetRef.incRefCount();
			incrementRefCountedDependencies(nextTask.fileName);

			if (nextTask.params != null && nextTask.params.loadedCallback != null) {
				nextTask.params.loadedCallback.finishedLoading(this, nextTask.fileName, nextTask.type);
			}

			if (nextTask.parent == null) {
				loaded++;
			}

			nextTask.free();
		} else {
			waitingQueue.add(nextTask);
			executor.submit(nextTask);
		}
	}

	/**
	 * Sets a new {@link AssetLoader} for the given type.
	 * 
	 * @param type the type of the asset
	 * @param loader the loader
	 */
	@Override
	@SuppressWarnings("sync-override")
	public <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type, AssetLoader<T, P> loader) {
		setLoader(type, null, loader);
	}

	/**
	 * Sets a new {@link AssetLoader} for the given type.
	 * 
	 * @param type the type of the asset
	 * @param suffix the suffix the filename must have for this loader to be used
	 *            or null to specify the default loader.
	 * @param loader the loader
	 */
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

			log.debug("Loader set: " + type.getSimpleName() + " -> " + loader.getClass().getSimpleName());
			ObjectMap<String, AssetLoader<?, ?>> loaders = this.loaders.get(type);
			if (loaders == null) {
				this.loaders.put(type, loaders = new ObjectMap<String, AssetLoader<?, ?>>());
			}

			loaders.put(suffix == null ? "" : suffix, loader);
		}
	}

	/** @return the number of loaded assets */
	@Override
	@SuppressWarnings("sync-override")
	public int getLoadedAssets() {
		synchronized (lock) {
			return assetsByFileName.size;
		}
	}

	/** @return the number of currently queued assets */
	@Override
	@SuppressWarnings("sync-override")
	public int getQueuedAssets() {
		synchronized (lock) {
			return asyncQueue.size;
		}
	}

	/** @return the progress in percent of completion. */
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

	/**
	 * Sets an {@link AssetErrorListener} to be invoked in case loading an asset
	 * failed.
	 * 
	 * @param listener the listener or null
	 */
	@Override
	@SuppressWarnings("sync-override")
	public void setErrorListener(AssetErrorListener listener) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Disposes all assets in the manager and stops all asynchronous loading.
	 */
	@Override
	@SuppressWarnings("sync-override")
	public void dispose() {
		synchronized (lock) {
			log.debug("Disposing.");
			clear();
			DisposablesService.dispose(executor);
		}
	}

	/** Clears and disposes all assets and the preloading queue. */
	@Override
	@SuppressWarnings("sync-override")
	public void clear() {
		synchronized (lock) {
			SynchronizedPools.freeAll(asyncQueue);
			asyncQueue.clear();

			while (!update()) {
			}

			ObjectIntMap<String> dependencyCount = new ObjectIntMap<String>();
			while (assetsByFileName.size > 0) {
				// for each asset, figure out how often it was referenced
				dependencyCount.clear();
				Array<String> assets = assetsByFileName.keys().toArray();
				for (String asset : assets) {
					dependencyCount.put(asset, 0);
				}

				for (String asset : assets) {
					Array<String> dependencies = assetDependencies.get(asset);
					if (dependencies == null) {
						continue;
					}

					for (String dependency : dependencies) {
						int count = dependencyCount.get(dependency, 0);
						count++;
						dependencyCount.put(dependency, count);
					}
				}

				// only dispose of assets that are root assets (not referenced)
				for (String asset : assets) {
					if (dependencyCount.get(asset, 0) == 0) {
						unload(asset);
					}
				}

				assets.clear();
			}

			assetsByFileName.clear();
			fileNamesByAsset.clear();
			assetDependencies.clear();
			loaded = 0;
			toLoad = 0;
		}
	}

	/** @return the {@link Logger} used by the {@link AssetManager} */
	@Override
	public Logger getLogger() {
		return log;
	}

	@Override
	public void setLogger(Logger logger) {
		log = logger;
	}

	/**
	 * Returns the reference count of an asset.
	 * 
	 * @param fileName
	 */
	@Override
	@SuppressWarnings("sync-override")
	public int getReferenceCount(String fileName) {
		synchronized (lock) {
			return assetsByFileName.get(fileName).getRefCount();
		}
	}

	/**
	 * Sets the reference count of an asset.
	 * 
	 * @param fileName
	 */
	@Override
	@SuppressWarnings("sync-override")
	public void setReferenceCount(String fileName, int refCount) {
		synchronized (lock) {
			assetsByFileName.get(fileName).setRefCount(refCount);
		}
	}

	/**
	 * @return a string containing ref count and dependency information for all assets.
	 */
	@Override
	@SuppressWarnings("sync-override")
	public String getDiagnostics() {
		synchronized (lock) {
			StringBuffer buffer = new StringBuffer();
			for (String fileName : assetsByFileName.keys()) {
				buffer.append(fileName);
				buffer.append(", ");

				RefCountedContainer assetRef = assetsByFileName.get(fileName);
				Array<String> dependencies = assetDependencies.get(fileName);

				buffer.append(assetRef.getObject(Object.class).getClass().getSimpleName());

				buffer.append(", refs: ");
				buffer.append(assetRef.getRefCount());

				if (dependencies != null) {
					buffer.append(", deps: [");
					for (String dep : dependencies) {
						buffer.append(dep);
						buffer.append(",");
					}
					buffer.append("]");
				}

				buffer.append("\n");
			}
			return buffer.toString();
		}
	}

	/** @return the file names of all loaded assets. */
	@Override
	@SuppressWarnings("sync-override")
	public Array<String> getAssetNames() {
		synchronized (lock) {
			return assetsByFileName.keys().toArray();
		}
	}

	/**
	 * @return the dependencies of an asset or null if the asset has no
	 *         dependencies.
	 */
	@Override
	@SuppressWarnings("sync-override")
	public Array<String> getDependencies(String fileName) {
		synchronized (lock) {
			return assetDependencies.get(fileName);
		}
	}

	/** @return the type of a loaded asset. */
	@Override
	@SuppressWarnings("sync-override")
	public Class<?> getAssetType(String fileName) {
		synchronized (lock) {
			return assetsByFileName.get(fileName).getClass();
		}
	}
}
