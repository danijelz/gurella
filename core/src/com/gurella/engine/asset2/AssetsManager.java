package com.gurella.engine.asset2;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.gurella.engine.asset2.bundle.Bundle;
import com.gurella.engine.asset2.persister.AssetsPersister;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.async.SimpleAsyncCallback;

public class AssetsManager implements Disposable {
	private final Object mutex = new Object();

	private final Files files = Gdx.files;
	private final AssetRegistry registry = new AssetRegistry();
	private final AssetsLoader loader = new AssetsLoader();
	private final AssetsPersister persister = new AssetsPersister(this);
	private final AssetId tempId = new AssetId();

	public <T> void loadAsync(AsyncCallback<T> callback, String fileName, FileType fileType, Class<T> assetType,
			int priority) {
		T asset;
		synchronized (mutex) {
			asset = registry.getIfLoaded(fileName, fileType, assetType, null);
			if (asset == null) {
				FileHandle file = resolveFile(fileName, fileType);
				loader.load(callback, file, assetType, priority);
			}
		}

		if (asset != null) {
			callback.onProgress(1f);
			callback.onSuccess(asset);
		}
	}

	private FileHandle resolveFile(String fileName, FileType fileType) {
		// TODO resolve by AssetConfig
		return files.getFileHandle(fileName, fileType);
	}

	public <T> T load(String fileName, FileType fileType, Class<T> assetType) {
		synchronized (mutex) {
			T asset = registry.getIfLoaded(fileName, fileType, assetType, null);
			if (asset != null) {
				return asset;
			}

			SimpleAsyncCallback<T> callback = SimpleAsyncCallback.obtain();
			FileHandle file = resolveFile(fileName, fileType);
			loader.load(callback, file, assetType, Integer.MAX_VALUE);

			while (!callback.isDone()) {
				loader.update();
				ThreadUtils.yield();
			}

			if (callback.isDoneWithException()) {
				Throwable exception = callback.getException();
				callback.free();
				throw new RuntimeException("Error loading asset " + fileName, exception);
			} else {
				asset = callback.getValue();
				callback.free();
				return asset;
			}
		}
	}

	public boolean isLoaded(String fileName) {
		synchronized (mutex) {
			return registry.isLoaded(fileName, FileType.Internal, Assets.getAssetClass(fileName));
		}
	}

	public boolean isLoaded(String fileName, FileType fileType, Class<?> assetType) {
		synchronized (mutex) {
			return registry.isLoaded(fileName, fileType, assetType);
		}
	}

	public <T> boolean unload(T asset) {
		synchronized (mutex) {
			return registry.remove(asset);
		}
	}

	// TODO not needed
	public <T> T get(String fileName, FileType fileType, Class<T> assetType, String bundleId) {
		synchronized (mutex) {
			return registry.get(fileName, fileType, assetType, bundleId);
		}
	}

	public <T> Array<T> getAll(Class<T> type, Array<T> out) {
		synchronized (mutex) {
			return registry.getAll(type, out);
		}
	}

	public <T> String getFileName(T asset) {
		synchronized (mutex) {
			return registry.getFileName(asset);
		}
	}

	public <T> FileType getFileType(T asset) {
		synchronized (mutex) {
			return registry.getFileType(asset);
		}
	}

	public <T> AssetId getId(T asset, AssetId out) {
		synchronized (mutex) {
			return registry.getAssetId(asset, out);
		}
	}

	public boolean isManaged(Object asset) {
		return registry.isManaged(asset);
	}

	public <T> void save(T asset, String fileName, FileType fileType, boolean sticky) {
		synchronized (mutex) {
			FileHandle file = resolveFile(fileName, fileType);
			persister.persist(file, asset);
			if (!registry.isManaged(asset)) {
				Class<Object> assetType = Assets.getAssetRootClass(asset);
				registry.add(fileName, fileType, assetType, asset, sticky);
			}
		}
	}

	public boolean delete(String fileName, FileType fileType) {
		synchronized (mutex) {
			registry.removeAll(fileName, fileType);
			FileHandle file = files.getFileHandle(fileName, fileType);
			if (file.exists()) {
				file.delete();
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean delete(Object asset) {
		synchronized (mutex) {
			registry.getAssetId(asset, tempId);
			if (tempId.isEmpty()) {
				return false;
			}

			String fileName = tempId.fileName;
			FileType fileType = tempId.fileType;
			registry.removeAll(fileName, fileType);

			FileHandle file = files.getFileHandle(fileName, fileType);
			if (file.exists()) {
				file.delete();
				return true;
			} else {
				return false;
			}
		}
	}

	// TODO dependencies and bundle contents should be handled diferently
	public void addDependency(Object asset, Object dependency) {
		synchronized (mutex) {
			registry.addDependency(asset, dependency);
		}
	}

	public void removeDependency(Object asset, Object dependency) {
		synchronized (mutex) {
			registry.removeDependency(asset, dependency);
		}
	}

	public void replaceDependency(Object asset, Object oldDependency, Object newDependency) {
		synchronized (mutex) {
			registry.replaceDependency(asset, oldDependency, newDependency);
		}
	}

	public void addToBundle(Bundle bundle, Object asset, String internalId) {
		synchronized (mutex) {
			registry.addToBundle(bundle, asset, internalId);
		}
	}

	public void removeFromBundle(Bundle bundle, Object asset) {
		synchronized (mutex) {
			registry.removeFromBundle(bundle, asset);
		}
	}

	public String getBundledId(Object asset) {
		synchronized (mutex) {
			return registry.getBundleId(asset);
		}
	}
	
	@Override
	public void dispose() {
		synchronized (mutex) {
			loader.dispose();
			registry.dispose();
		}
	}
}
