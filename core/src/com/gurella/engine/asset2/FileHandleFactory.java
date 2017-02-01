package com.gurella.engine.asset2;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;

//TODO add internal files cache???
public interface FileHandleFactory {
	FileHandle create(String fileName, FileType fileType);
}
