package com.gurella.engine.asset.resolver;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Sort;
import com.gurella.engine.asset.AssetId;
import com.gurella.engine.utils.priority.PriorityComparator;

public class FilehandleResolverRegistry implements FileHandleFactory {
	private final Files files = Gdx.files;
	private final Array<FileHandleResolver> resolvers = new Array<FileHandleResolver>();
	private final Sort sort = new Sort();

	@Override
	public FileHandle create(String fileName, FileType fileType) {
		return files.getFileHandle(fileName, fileType);
	}

	public FileHandle resolveFile(AssetId assetId) {
		for (int i = 0, n = resolvers.size; i < n; i++) {
			FileHandleResolver resolver = resolvers.get(i);
			if (resolver.accepts(assetId)) {
				return resolver.resolve(this, assetId);
			}
		}

		return create(assetId.getFileName(), assetId.getFileType());
	}

	public void registerResolver(FileHandleResolver resolver) {
		resolvers.add(resolver);
		sort.sort(resolvers, PriorityComparator.instance);
	}

	public boolean unregisterResolver(FileHandleResolver resolver) {
		return resolvers.removeValue(resolver, true);
	}
}
