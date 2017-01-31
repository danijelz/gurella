package com.gurella.engine.asset2.loader;

import com.badlogic.gdx.Files.FileType;

public interface DependencyCollector {
	<D> void addDependency(String fileName, FileType fileType, Class<D> assetType);
}
