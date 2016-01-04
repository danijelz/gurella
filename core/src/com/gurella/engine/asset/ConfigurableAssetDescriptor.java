package com.gurella.engine.asset;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.application.Application;

public class ConfigurableAssetDescriptor<T> {
	private static final AssetRegistry assetRegistry = Application.DISPOSABLES_SERVICE
			.add(new AssetRegistry());

	AssetType assetType;
	String fileName;
	final Array<AssetSelector<T>> selectors = new Array<AssetSelector<T>>();

	AssetLoaderParameters<T> parameters;
	AssetDescriptor<T> assetDescriptor;

	ConfigurableAssetDescriptor() {
	}

	public ConfigurableAssetDescriptor(AssetType assetType, String fileName) {
		this.assetType = assetType;
		this.fileName = fileName;
	}

	public AssetDescriptor<T> getAssetDescriptor() {
		if (assetDescriptor == null) {
			assetDescriptor = createAssetDescriptor();
		}
		return assetDescriptor;
	}

	private AssetDescriptor<T> createAssetDescriptor() {
		String resolvedFileName = null;
		AssetLoaderParameters<T> resolvedParameters = null;

		for (int i = 0; i < selectors.size; i++) {
			AssetSelector<T> selector = selectors.get(i);
			if (selector.predicate.evaluate(null)) {
				resolvedFileName = selector.fileName;
				resolvedParameters = selector.parameters;
				break;
			}
		}

		if (resolvedFileName == null) {
			resolvedFileName = fileName;
		}

		if (resolvedParameters == null) {
			resolvedParameters = parameters;
		}

		return new AssetDescriptor<T>(resolvedFileName, getAssetType(), resolvedParameters);
	}

	public String getFileName() {
		return fileName;
	}

	@SuppressWarnings("unchecked")
	public Class<T> getAssetType() {
		return (Class<T>) assetType.assetType;
	}

	public T load() {
		AssetDescriptor<T> resolvedAssetDescriptor = getAssetDescriptor();
		T asset = assetRegistry.load(resolvedAssetDescriptor);

		while (asset == null) {
			asset = assetRegistry.get(resolvedAssetDescriptor);
			try {
				synchronized (this) {
					wait(5);
				}
			} catch (@SuppressWarnings("unused") InterruptedException ignored) {
			}
		}

		return asset;
	}
}
