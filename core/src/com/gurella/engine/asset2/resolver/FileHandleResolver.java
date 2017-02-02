package com.gurella.engine.asset2.resolver;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset2.AssetId;
import com.gurella.engine.asset2.FileHandleFactory;

public interface FileHandleResolver {
	boolean accepts(AssetId assetId);

	FileHandle resolve(FileHandleFactory factory, AssetId assetId);
}
