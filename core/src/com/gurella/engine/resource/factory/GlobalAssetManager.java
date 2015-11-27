package com.gurella.engine.resource.factory;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.gurella.engine.application.UpdateListener;
import com.gurella.engine.application.CommonUpdateOrder;

public class GlobalAssetManager implements UpdateListener, Disposable {
	private final AssetManager assetManager = new AssetManager();
	private final ObjectIntMap<String> managedAssets = new ObjectIntMap<String>();

	public synchronized <T> T load(AssetDescriptor<T> assetDescriptor) {
		String fileName = assetDescriptor.fileName;
		int refCount = managedAssets.get(fileName, 0);

		if (refCount == 0) {
			assetManager.load(assetDescriptor);
		}

		managedAssets.put(fileName, refCount + 1);
		return assetManager.isLoaded(fileName)
				? assetManager.get(assetDescriptor)
				: null;
	}

	public synchronized boolean isLoaded(AssetDescriptor<?> assetDescriptor) {
		return assetManager.isLoaded(assetDescriptor.fileName);
	}

	public <T> T get(AssetDescriptor<T> assetDescriptor) {
		String fileName = assetDescriptor.fileName;
		return assetManager.isLoaded(fileName)
				? assetManager.get(assetDescriptor)
				: null;
	}

	public synchronized void unload(AssetDescriptor<?> assetDescriptor) {
		String fileName = assetDescriptor.fileName;
		int refCount = managedAssets.get(fileName, 0) - 1;

		if (refCount == 0) {
			managedAssets.remove(fileName, refCount);
			assetManager.unload(fileName);
		} else {
			managedAssets.put(fileName, refCount);
		}
	}

	@Override
	public int getOrdinal() {
		return CommonUpdateOrder.INPUT;
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
