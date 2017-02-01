package com.gurella.engine.asset2.resolver;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset2.FileHandleFactory;

public interface FileHandleResolver {
	public FileHandle resolve (FileHandleFactory factory, String fileName, FileType fileType);
}
