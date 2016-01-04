package com.gurella.engine.asset;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.gurella.engine.application.CommonUpdatePriority;
import com.gurella.engine.application.events.UpdateListener;

public class AssetRegistry implements UpdateListener, Disposable {
	private final AssetManager assetManager = new AssetManager();
	private final ObjectIntMap<String> managedAssets = new ObjectIntMap<String>();

	public <T> T load(AssetDescriptor<T> assetDescriptor) {
		synchronized (assetManager) {
			String fileName = assetDescriptor.fileName;
			int refCount = managedAssets.get(fileName, 0);
			if (refCount == 0) {
				assetManager.load(assetDescriptor);
			}
			managedAssets.put(fileName, refCount + 1);
			return assetManager.isLoaded(fileName) ? assetManager.get(assetDescriptor) : null;
		}
	}

	public boolean isLoaded(AssetDescriptor<?> assetDescriptor) {
		synchronized (assetManager) {
			return assetManager.isLoaded(assetDescriptor.fileName);
		}
	}

	public <T> T get(AssetDescriptor<T> assetDescriptor) {
		String fileName = assetDescriptor.fileName;
		synchronized (assetManager) {
			return assetManager.isLoaded(fileName) ? assetManager.get(assetDescriptor) : null;
		}
	}

	public void unload(AssetDescriptor<?> assetDescriptor) {
		String fileName = assetDescriptor.fileName;

		synchronized (assetManager) {
			int refCount = managedAssets.get(fileName, 0) - 1;
			if (refCount == 0) {
				managedAssets.remove(fileName, 0);
				assetManager.unload(fileName);
			} else {
				managedAssets.put(fileName, refCount);
			}
		}
	}

	public <T> String getAssetFileName(T asset) {
		synchronized (assetManager) {
			return assetManager.getAssetFileName(asset);
		}
	}

	@Override
	public int getPriority() {
		return CommonUpdatePriority.INPUT;
	}

	@Override
	public void update() {
		assetManager.update();
	}

	@Override
	public void dispose() {
		assetManager.dispose();
	}
}
