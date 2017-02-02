package com.gurella.engine.asset2.resolver;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset2.AssetId;
import com.gurella.engine.asset2.FileHandleFactory;

public class PredicatedFileHandleResolver implements FileHandleResolver {
	private FileHandleResolverPredicate predicate;

	@Override
	public boolean accepts(AssetId assetId) {
		return predicate.evaluate(assetId);
	}

	@Override
	public FileHandle resolve(FileHandleFactory factory, AssetId assetId) {
		// TODO Auto-generated method stub
		return null;
	}
}
