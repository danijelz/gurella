package com.gurella.engine.asset.resolver;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset.AssetId;

public interface FileHandleResolver {
	boolean accepts(AssetId assetId);

	FileHandle resolve(FileHandleFactory factory, AssetId assetId);
}
