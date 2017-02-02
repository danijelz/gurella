package com.gurella.engine.asset2.resolver;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.asset2.AssetId;
import com.gurella.engine.asset2.FileHandleFactory;

public class CompositeFileHandleResolver implements FileHandleResolver {
	public final Array<FileHandleResolver> resolvers = new Array<FileHandleResolver>();

	@Override
	public boolean accepts(AssetId assetId) {
		return getResolver(assetId) != null;
	}

	private FileHandleResolver getResolver(AssetId assetId) {
		for (int i = 0, n = resolvers.size; i < n; i++) {
			FileHandleResolver resolver = resolvers.get(i);
			if (resolver.accepts(assetId)) {
				return resolver;
			}
		}
		return null;
	}

	@Override
	public FileHandle resolve(FileHandleFactory factory, AssetId assetId) {
		return getResolver(assetId).resolve(factory, assetId);
	}
}
