package com.gurella.engine.asset.loader;

import com.badlogic.gdx.Files.FileType;

public interface DependencyCollector {
	<D> void collectDependency(String fileName, FileType fileType, Class<D> assetType);
}
