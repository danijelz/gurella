package com.gurella.engine.asset2.config;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.utils.Array;

//TODO unused
public class AssetConfig<T> {
	boolean sticky;
	Class<T> type;

	String fileNameUuid;
	AssetLoaderParameters<T> parameters;

	final Array<AssetSelector<T>> selectors = new Array<AssetSelector<T>>();

	boolean resolved;
	String resolvedFileNameUuid;
	AssetLoaderParameters<T> resolvedParameters;

	private void resolve() {
		resolved = true;
		for (int i = 0; i < selectors.size; i++) {
			AssetSelector<T> selector = selectors.get(i);
			if (selector.predicate.evaluate()) {
				resolvedFileNameUuid = selector.fileName;
				resolvedParameters = selector.parameters;
				return;
			}
		}

		resolvedFileNameUuid = fileNameUuid;
		resolvedParameters = parameters;
	}

	public String getFileNameUuid() {
		if (!resolved) {
			resolve();
		}
		return fileNameUuid;
	}

	public Class<T> getType() {
		return type;
	}

	public AssetLoaderParameters<T> getParameters() {
		if (!resolved) {
			resolve();
		}
		return parameters;
	}
}
