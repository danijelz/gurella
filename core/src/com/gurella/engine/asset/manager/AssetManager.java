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
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Sort;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.base.resource.AsyncCallback;

/**
 * Loads and stores assets like textures, bitmapfonts, tile maps, sounds, music and so on.
 * 
 * @author mzechner
 */
public class AssetManager extends com.badlogic.gdx.assets.AssetManager {
	private final ObjectMap<Class<?>, ObjectMap<String, RefCountedContainer>> assets = new ObjectMap<Class<?>, ObjectMap<String, RefCountedContainer>>();
	private final ObjectMap<String, Class<?>> assetTypes = new ObjectMap<String, Class<?>>();
	private final ObjectMap<String, Array<String>> assetDependencies = new ObjectMap<String, Array<String>>();
	private final ObjectSet<String> injected = new ObjectSet<String>();

	private final ObjectMap<Class<?>, ObjectMap<String, AssetLoader<?, ?>>> loaders = new ObjectMap<Class<?>, ObjectMap<String, AssetLoader<?, ?>>>();
	private final Array<CallbackAssetDescriptor<?>> loadQueue = new Array<CallbackAssetDescriptor<?>>();
	private final Array<AssetLoadingTask<?, ?>> tasks = new Array<AssetLoadingTask<?, ?>>();
	final AsyncExecutor executor;

	private final Sort sort = new Sort();

	private AssetErrorListener listener = null;
	private int loaded = 0;
	private int toLoad = 0;

	private final Object lock = new Object();

	Logger log = new Logger("gurella.AssetManager", Application.LOG_NONE);

	/** Creates a new AssetManager with all default loaders. */
	public AssetManager() {
		this(new InternalFileHandleResolver());
	}

	/** Creates a new AssetManager with all default loaders. */
	public AssetManager(FileHandleResolver resolver) {
		this(resolver, true);
	}

	/**
	 * Creates a new AssetManager with optionally all default loaders. If you don't add the default loaders then you do
	 * have to manually add the loaders you need, including any loaders they might depend on.
	 * 
	 * @param defaultLoaders
	 *            whether to add the default loaders
	 */
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
		executor = new AsyncExecutor(1);
	}

	/**
	 * @param fileName
	 *            the asset file name
	 * @return the asset
	 */
	@Override
	@SuppressWarnings("sync-override")
	public <T> T get(String fileName) {
		synchronized (lock) {
			@SuppressWarnings("unchecked")
			Class<T> type = (Class<T>) assetTypes.get(fileName);
			if (type == null) {
				throw new GdxRuntimeException("Asset not loaded: " + fileName);
			}

			ObjectMap<String, RefCountedContainer> assetsByType = assets.get(type);
			if (assetsByType == null) {
				throw new GdxRuntimeException("Asset not loaded: " + fileName);
			}

			RefCountedContainer assetContainer = assetsByType.get(fileName);
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
	 * @param fileName
	 *            the asset file name
	 * @param type
	 *            the asset type
	 * @return the asset
	 */
	@Override
	@SuppressWarnings("sync-override")
	public <T> T get(String fileName, Class<T> type) {
		synchronized (lock) {
			ObjectMap<String, RefCountedContainer> assetsByType = assets.get(type);
			if (assetsByType == null) {
				throw new GdxRuntimeException("Asset not loaded: " + fileName);
			}

			RefCountedContainer assetContainer = assetsByType.get(fileName);
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
	 * @param type
	 *            the asset type
	 * @return all the assets matching the specified type
	 */
	@Override
	@SuppressWarnings("sync-override")
	public <T> Array<T> getAll(Class<T> type, Array<T> out) {
		synchronized (lock) {
			ObjectMap<String, RefCountedContainer> assetsByType = assets.get(type);
			if (assetsByType != null) {
				for (ObjectMap.Entry<String, RefCountedContainer> asset : assetsByType.entries()) {
					out.add(asset.value.getObject(type));
				}
			}
			return out;
		}
	}

	/**
	 * @param assetDescriptor
	 *            the asset descriptor
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
	 * @param fileName
	 *            the file name
	 */
	@Override
	@SuppressWarnings("sync-override")
	public void unload(String fileName) {
		synchronized (lock) {
			// check if it's currently processed (and the first element in the
			// stack, thus not a dependency)
			// and cancel if necessary
			if (tasks.size > 0) {
				AssetLoadingTask<?, ?> currAsset = tasks.get(0);
				if (currAsset.assetDesc.fileName.equals(fileName)) {
					currAsset.cancel = true;
					log.debug("Unload (from tasks): " + fileName);
					return;
				}
			}

			// check if it's in the queue
			int foundIndex = -1;
			for (int i = 0; i < loadQueue.size; i++) {
				if (loadQueue.get(i).fileName.equals(fileName)) {
					foundIndex = i;
					break;
				}
			}

			if (foundIndex != -1) {
				toLoad--;
				loadQueue.removeIndex(foundIndex).free();
				log.debug("Unload (from queue): " + fileName);
				return;
			}

			// get the asset and its type
			Class<?> type = assetTypes.get(fileName);
			if (type == null) {
				throw new GdxRuntimeException("Asset not loaded: " + fileName);
			}

			RefCountedContainer assetRef = assets.get(type).get(fileName);
			// if it is reference counted, decrement ref count and check if we can
			// really get rid of it.
			assetRef.decRefCount();
			if (assetRef.getRefCount() <= 0) {
				log.debug("Unload (dispose): " + fileName);

				// if it is disposable dispose it
				if (assetRef.getObject(Object.class) instanceof Disposable) {
					((Disposable) assetRef.getObject(Object.class)).dispose();
				}

				// remove the asset from the manager.
				assetTypes.remove(fileName);
				assets.get(type).remove(fileName);
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
	 * @param asset
	 *            the asset
	 * @return whether the asset is contained in this manager
	 */
	@Override
	@SuppressWarnings("sync-override")
	public <T> boolean containsAsset(T asset) {
		synchronized (lock) {
			ObjectMap<String, RefCountedContainer> typedAssets = assets.get(asset.getClass());
			if (typedAssets == null) {
				return false;
			}

			for (String fileName : typedAssets.keys()) {
				@SuppressWarnings("unchecked")
				T otherAsset = (T) typedAssets.get(fileName).getObject(Object.class);
				if (otherAsset == asset || asset.equals(otherAsset)) {
					return true;
				}
			}

			return false;
		}
	}

	/**
	 * @param asset
	 *            the asset
	 * @return the filename of the asset or null
	 */
	@Override
	@SuppressWarnings("sync-override")
	public <T> String getAssetFileName(T asset) {
		synchronized (lock) {
			for (Class<?> assetType : assets.keys()) {
				ObjectMap<String, RefCountedContainer> typedAssets = assets.get(assetType);
				for (String fileName : typedAssets.keys()) {
					@SuppressWarnings("unchecked")
					T otherAsset = (T) typedAssets.get(fileName).getObject(Object.class);
					if (otherAsset == asset || asset.equals(otherAsset))
						return fileName;
				}
			}
			return null;
		}
	}

	/**
	 * @param fileName
	 *            the file name of the asset
	 * @return whether the asset is loaded
	 */
	@Override
	@SuppressWarnings("sync-override")
	public boolean isLoaded(String fileName) {
		synchronized (lock) {
			if (fileName == null) {
				return false;
			}
			return assetTypes.containsKey(fileName);
		}
	}

	/**
	 * @param fileName
	 *            the file name of the asset
	 * @return whether the asset is loaded
	 */
	@Override
	@SuppressWarnings("sync-override")
	public boolean isLoaded(String fileName, @SuppressWarnings("rawtypes") Class type) {
		synchronized (lock) {
			ObjectMap<String, RefCountedContainer> assetsByType = assets.get(type);
			if (assetsByType == null) {
				return false;
			}
			RefCountedContainer assetContainer = assetsByType.get(fileName);
			if (assetContainer == null) {
				return false;
			}
			@SuppressWarnings("unchecked")
			Object object = assetContainer.getObject(type);
			return object != null;
		}
	}

	/**
	 * Returns the default loader for the given type
	 * 
	 * @param type
	 *            The type of the loader to get
	 * @return The loader capable of loading the type, or null if none exists
	 */
	@Override
	public <T> AssetLoader<T, ?> getLoader(final Class<T> type) {
		return getLoader(type, null);
	}

	/**
	 * Returns the loader for the given type and the specified filename. If no loader exists for the specific filename,
	 * the default loader for that type is returned.
	 * 
	 * @param type
	 *            The type of the loader to get
	 * @param fileName
	 *            The filename of the asset to get a loader for, or null to get the default loader
	 * @return The loader capable of loading the type and filename, or null if none exists
	 */
	@Override
	public <T> AssetLoader<T, ?> getLoader(final Class<T> type, final String fileName) {
		synchronized (lock) {
			final ObjectMap<String, AssetLoader<?, ?>> loaders = this.loaders.get(type);
			if (loaders == null || loaders.size < 1) {
				return null;
			}

			if (fileName == null) {
				@SuppressWarnings("unchecked")
				AssetLoader<T, ?> casted = (AssetLoader<T, ?>) loaders.get("");
				return casted;
			}

			AssetLoader<T, ?> result = null;
			int length = -1;
			for (ObjectMap.Entry<String, AssetLoader<?, ?>> entry : loaders.entries()) {
				if (entry.key.length() > length && fileName.endsWith(entry.key)) {
					@SuppressWarnings("unchecked")
					AssetLoader<T, ?> casted = (AssetLoader<T, ?>) entry.value;
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
	 * @param fileName
	 *            the file name (interpretation depends on {@link AssetLoader})
	 * @param type
	 *            the type of the asset.
	 */
	@Override
	@SuppressWarnings("sync-override")
	public <T> void load(String fileName, Class<T> type) {
		load(fileName, type, null, null, 0);
	}

	/**
	 * Adds the given asset to the loading queue of the AssetManager.
	 * 
	 * @param fileName
	 *            the file name (interpretation depends on {@link AssetLoader})
	 * @param type
	 *            the type of the asset.
	 * @param parameter
	 *            parameters for the AssetLoader.
	 */
	@Override
	@SuppressWarnings("sync-override")
	public <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
		load(fileName, type, parameter, null, 0);
	}

	public <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter, AsyncCallback<T> callback,
			int priority) {
		synchronized (lock) {
			// check if an asset with the same name but a different type has already
			// been added.
			AssetLoader<T, ?> loader = getLoader(type, fileName);
			if (loader == null) {
				throw new GdxRuntimeException("No loader for type: " + ClassReflection.getSimpleName(type));
			}
			// reset stats
			if (loadQueue.size == 0) {
				loaded = 0;
				toLoad = 0;
			}

			// check preload queue
			for (int i = 0; i < loadQueue.size; i++) {
				CallbackAssetDescriptor<?> desc = loadQueue.get(i);
				if (desc.fileName.equals(fileName) && !desc.type.equals(type))
					throw new GdxRuntimeException("Asset with name '" + fileName
							+ "' already in preload queue, but has different type (expected: "
							+ ClassReflection.getSimpleName(type) + ", found: "
							+ ClassReflection.getSimpleName(desc.type) + ")");
			}

			// check task list
			for (int i = 0; i < tasks.size; i++) {
				CallbackAssetDescriptor<?> desc = tasks.get(i).assetDesc;
				if (desc.fileName.equals(fileName) && !desc.type.equals(type))
					throw new GdxRuntimeException("Asset with name '" + fileName
							+ "' already in task list, but has different type (expected: "
							+ ClassReflection.getSimpleName(type) + ", found: "
							+ ClassReflection.getSimpleName(desc.type) + ")");
			}

			// check loaded assets
			Class<?> otherType = assetTypes.get(fileName);
			if (otherType != null && !otherType.equals(type))
				throw new GdxRuntimeException("Asset with name '" + fileName
						+ "' already loaded, but has different type (expected: " + ClassReflection.getSimpleName(type)
						+ ", found: " + ClassReflection.getSimpleName(otherType) + ")");
			toLoad++;
			CallbackAssetDescriptor<T> assetDesc = CallbackAssetDescriptor.obtain(fileName, type, parameter, callback,
					priority);
			loadQueue.add(CallbackAssetDescriptor.obtain(fileName, type, parameter, callback, priority));
			sort.sort(loadQueue);
			log.debug("Queued: " + assetDesc);
		}
	}

	/**
	 * Adds the given asset to the loading queue of the AssetManager.
	 * 
	 * @param desc
	 *            the {@link CallbackAssetDescriptor}
	 */
	@Override
	@SuppressWarnings({ "sync-override", "unchecked" })
	public void load(@SuppressWarnings("rawtypes") AssetDescriptor desc) {
		load(desc.fileName, desc.type, desc.params, null, 0);
	}

	/**
	 * Updates the AssetManager continuously for the specified number of milliseconds, yielding the CPU to the loading
	 * thread between updates. This may block for less time if all loading tasks are complete. This may block for more
	 * time if the portion of a single task that happens in the GL thread takes a long time.
	 * 
	 * @return true if all loading is finished.
	 */
	@Override
	public boolean update(int millis) {
		long endTime = TimeUtils.millis() + millis;
		while (true) {
			boolean done = update();
			if (done || TimeUtils.millis() > endTime)
				return done;
			ThreadUtils.yield();
		}
	}

	/** Blocks until all assets are loaded. */
	@Override
	public void finishLoading() {
		log.debug("Waiting for loading to complete...");
		while (!update())
			ThreadUtils.yield();
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

	void injectDependencies(String parentAssetFilename, AsyncCallback<?> parentCallback,
			Array<AssetDescriptor<?>> dependendAssetDescriptors) {
		synchronized (lock) {
			ObjectSet<String> injected = this.injected;
			for (AssetDescriptor<?> descriptor : dependendAssetDescriptors) {
				if (injected.contains(descriptor.fileName)) {
					// Ignore subsequent dependencies if there are duplicates.
					continue;
				}
				injected.add(descriptor.fileName);
				injectDependency(parentAssetFilename, parentCallback, descriptor);
			}
			injected.clear();
		}
	}

	private <T> void injectDependency(String parentAssetFilename, AsyncCallback<?> parentCallback,
			AssetDescriptor<T> dependendAssetDesc) {
		synchronized (lock) {
			// add the asset as a dependency of the parent asset
			Array<String> dependencies = assetDependencies.get(parentAssetFilename);
			if (dependencies == null) {
				dependencies = new Array<String>();
				assetDependencies.put(parentAssetFilename, dependencies);
			}

			String fileName = dependendAssetDesc.fileName;
			dependencies.add(fileName);

			if (isLoaded(fileName)) {
				log.debug("Dependency already loaded: " + dependendAssetDesc);
				Class<?> type = assetTypes.get(fileName);
				RefCountedContainer assetRef = assets.get(type).get(fileName);
				assetRef.incRefCount();
				incrementRefCountedDependencies(fileName);
			} else {
				log.info("Loading dependency: " + dependendAssetDesc);
				CallbackAssetDescriptor<T> assetDesc = new CallbackAssetDescriptor<T>(fileName, dependendAssetDesc.type,
						dependendAssetDesc.params, callback, 0);
				addTask(assetDesc);
			}
		}
	}

	/**
	 * Adds a {@link AssetLoadingTask} to the task stack for the given asset.
	 * 
	 * @param assetDesc
	 */
	private <T, P extends AssetLoaderParameters<T>> void addTask(CallbackAssetDescriptor<T> assetDesc) {
		@SuppressWarnings("unchecked")
		AssetLoader<T, P> loader = (AssetLoader<T, P>) getLoader(assetDesc.type, assetDesc.fileName);
		if (loader == null) {
			throw new GdxRuntimeException("No loader for type: " + ClassReflection.getSimpleName(assetDesc.type));
		}
		tasks.add(AssetLoadingTask.obtain(this, assetDesc, loader));
		sort.sort(tasks);
	}

	/** Adds an asset to this AssetManager */
	@Override
	protected <T> void addAsset(final String fileName, Class<T> type, T asset) {
		// add the asset to the filename lookup
		assetTypes.put(fileName, type);

		// add the asset to the type lookup
		ObjectMap<String, RefCountedContainer> typeToAssets = assets.get(type);
		if (typeToAssets == null) {
			typeToAssets = new ObjectMap<String, RefCountedContainer>();
			assets.put(type, typeToAssets);
		}
		typeToAssets.put(fileName, new RefCountedContainer(asset));
	}

	/**
	 * Updates the current task on the top of the task stack.
	 * 
	 * @return true if the asset is loaded or the task was cancelled.
	 */
	private boolean updateTask() {
		@SuppressWarnings("unchecked")
		AssetLoadingTask<Object, ?> task = (AssetLoadingTask<Object, ?>) tasks.peek();

		boolean complete = true;
		try {
			complete = task.cancel || task.update();
		} catch (RuntimeException ex) {
			task.cancel = true;

			if (task.assetDesc.callback == null) {
				throw ex;
			} else {
				task.assetDesc.callback.onException(ex);
				return true;
			}
		}

		// if the task has been cancelled or has finished loading
		if (complete) {
			// increase the number of loaded assets and pop the task from the
			// stack
			if (tasks.size == 1) {
				loaded++;
			}

			tasks.pop().free();

			if (task.cancel) {
				return true;
			}

			addAsset(task.assetDesc.fileName, task.assetDesc.type, task.getAsset());

			// otherwise, if a listener was found in the parameter invoke it
			if (task.assetDesc.params != null && task.assetDesc.params.loadedCallback != null) {
				task.assetDesc.params.loadedCallback.finishedLoading(this, task.assetDesc.fileName,
						task.assetDesc.type);
			}

			long endTime = TimeUtils.nanoTime();
			log.debug("Loaded: " + (endTime - task.startTime) / 1000000f + "ms " + task.assetDesc);

			return true;
		}
		return false;
	}

	@Override
	protected final void taskFailed(@SuppressWarnings("rawtypes") AssetDescriptor assetDesc, RuntimeException ex) {
	}

	private void incrementRefCountedDependencies(String parent) {
		Array<String> dependencies = assetDependencies.get(parent);
		if (dependencies == null)
			return;

		for (String dependency : dependencies) {
			Class<?> type = assetTypes.get(dependency);
			RefCountedContainer assetRef = assets.get(type).get(dependency);
			assetRef.incRefCount();
			incrementRefCountedDependencies(dependency);
		}
	}

	/**
	 * Updates the AssetManager, keeping it loading any assets in the preload queue.
	 * 
	 * @return true if all loading is finished.
	 */
	@Override
	@SuppressWarnings("sync-override")
	public boolean update() {
		synchronized (lock) {
			try {
				if (tasks.size == 0) {
					// loop until we have a new task ready to be processed
					while (loadQueue.size != 0 && tasks.size == 0) {
						nextTask();
					}

					// have we not found a task? We are done!
					if (tasks.size == 0) {
						return true;
					}
				}

				return updateTask() && loadQueue.size == 0 && tasks.size == 0;
			} catch (Throwable t) {
				handleTaskError(t);
				return loadQueue.size == 0;
			}
		}
	}

	/**
	 * Removes a task from the loadQueue and adds it to the task stack. If the asset is already loaded (which can happen
	 * if it was a dependency of a previously loaded asset) its reference count will be increased.
	 */
	private void nextTask() {
		CallbackAssetDescriptor<?> assetDesc = loadQueue.removeIndex(0);

		// if the asset not meant to be reloaded and is already loaded, increase
		// its reference count
		if (isLoaded(assetDesc.fileName)) {
			log.debug("Already loaded: " + assetDesc);
			Class<?> type = assetTypes.get(assetDesc.fileName);
			RefCountedContainer assetRef = assets.get(type).get(assetDesc.fileName);
			assetRef.incRefCount();
			incrementRefCountedDependencies(assetDesc.fileName);
			if (assetDesc.params != null && assetDesc.params.loadedCallback != null) {
				assetDesc.params.loadedCallback.finishedLoading(this, assetDesc.fileName, assetDesc.type);
			}
			loaded++;
			assetDesc.free();
		} else {
			// else add a new task for the asset.
			log.info("Loading: " + assetDesc);
			addTask(assetDesc);
		}
	}

	/**
	 * Handles a runtime/loading error in {@link #update()} by optionally invoking the {@link AssetErrorListener}.
	 * 
	 * @param t
	 */
	private void handleTaskError(Throwable t) {
		log.error("Error loading asset.", t);

		if (tasks.size == 0) {
			throw new GdxRuntimeException(t);
		}

		// pop the faulty task from the stack
		AssetLoadingTask<?, ?> task = tasks.pop();
		CallbackAssetDescriptor<?> assetDesc = task.assetDesc;

		// remove all dependencies
		if (task.dependenciesLoaded && task.dependencies != null) {
			for (AssetDescriptor<?> desc : task.dependencies) {
				unload(desc.fileName);
			}
		}
		task.free();

		// clear the rest of the stack
		for(int i = 0; i < tasks.size; i++){
			tasks.get(i).free();
		}
		tasks.clear();

		// inform the listener that something bad happened
		if (listener != null) {
			listener.error(assetDesc, t);
		} else {
			throw new GdxRuntimeException(t);
		}
	}

	/**
	 * Sets a new {@link AssetLoader} for the given type.
	 * 
	 * @param type
	 *            the type of the asset
	 * @param loader
	 *            the loader
	 */
	@Override
	@SuppressWarnings("sync-override")
	public <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type, AssetLoader<T, P> loader) {
		setLoader(type, null, loader);
	}

	/**
	 * Sets a new {@link AssetLoader} for the given type.
	 * 
	 * @param type
	 *            the type of the asset
	 * @param suffix
	 *            the suffix the filename must have for this loader to be used or null to specify the default loader.
	 * @param loader
	 *            the loader
	 */
	@Override
	@SuppressWarnings("sync-override")
	public <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type, String suffix,
			AssetLoader<T, P> loader) {
		synchronized (lock) {
			if (type == null)
				throw new IllegalArgumentException("type cannot be null.");
			if (loader == null)
				throw new IllegalArgumentException("loader cannot be null.");
			log.debug("Loader set: " + ClassReflection.getSimpleName(type) + " -> "
					+ ClassReflection.getSimpleName(loader.getClass()));
			ObjectMap<String, AssetLoader<?, ?>> loaders = this.loaders.get(type);
			if (loaders == null)
				this.loaders.put(type, loaders = new ObjectMap<String, AssetLoader<?, ?>>());
			loaders.put(suffix == null ? "" : suffix, loader);
		}
	}

	/** @return the number of loaded assets */
	@Override
	@SuppressWarnings("sync-override")
	public int getLoadedAssets() {
		synchronized (lock) {
			return assetTypes.size;
		}
	}

	/** @return the number of currently queued assets */
	@Override
	@SuppressWarnings("sync-override")
	public int getQueuedAssets() {
		synchronized (lock) {
			return loadQueue.size + tasks.size;
		}
	}

	/** @return the progress in percent of completion. */
	@Override
	@SuppressWarnings("sync-override")
	public float getProgress() {
		synchronized (lock) {
			if (toLoad == 0) {
				return 1;
			}
			return Math.min(1, loaded / (float) toLoad);
		}
	}

	/**
	 * Sets an {@link AssetErrorListener} to be invoked in case loading an asset failed.
	 * 
	 * @param listener
	 *            the listener or null
	 */
	@Override
	@SuppressWarnings("sync-override")
	public void setErrorListener(AssetErrorListener listener) {
		synchronized (lock) {
			this.listener = listener;
		}
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
			executor.dispose();
		}
	}

	/** Clears and disposes all assets and the preloading queue. */
	@Override
	@SuppressWarnings("sync-override")
	public void clear() {
		synchronized (lock) {
			for(int i = 0; i < loadQueue.size; i++){
				loadQueue.get(i).free();
			}
			loadQueue.clear();
			
			while (!update()) {
			}
			ObjectIntMap<String> dependencyCount = new ObjectIntMap<String>();
			while (assetTypes.size > 0) {
				// for each asset, figure out how often it was referenced
				dependencyCount.clear();
				Array<String> assets = assetTypes.keys().toArray();
				for (String asset : assets) {
					dependencyCount.put(asset, 0);
				}

				for (String asset : assets) {
					Array<String> dependencies = assetDependencies.get(asset);
					if (dependencies == null)
						continue;
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
			}
			
			this.assets.clear();
			this.assetTypes.clear();
			this.assetDependencies.clear();
			this.loaded = 0;
			this.toLoad = 0;
			
			for(int i = 0; i < tasks.size; i++){
				tasks.get(i).free();
			}
			this.tasks.clear();
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
			Class<?> type = assetTypes.get(fileName);
			if (type == null)
				throw new GdxRuntimeException("Asset not loaded: " + fileName);
			return assets.get(type).get(fileName).getRefCount();
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
			Class<?> type = assetTypes.get(fileName);
			if (type == null) {
				throw new GdxRuntimeException("Asset not loaded: " + fileName);
			}
			assets.get(type).get(fileName).setRefCount(refCount);
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
			for (String fileName : assetTypes.keys()) {
				buffer.append(fileName);
				buffer.append(", ");

				Class<?> type = assetTypes.get(fileName);
				RefCountedContainer assetRef = assets.get(type).get(fileName);
				Array<String> dependencies = assetDependencies.get(fileName);

				buffer.append(ClassReflection.getSimpleName(type));

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
			return assetTypes.keys().toArray();
		}
	}

	/**
	 * @return the dependencies of an asset or null if the asset has no dependencies.
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
			return assetTypes.get(fileName);
		}
	}
}
