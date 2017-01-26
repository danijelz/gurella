package com.gurella.engine.asset2.loader;

import com.badlogic.gdx.Files.FileType;

public interface DependencyProvider {
	<T> T getDependency(String fileName, FileType fileType, Class<T> assetType);
}
