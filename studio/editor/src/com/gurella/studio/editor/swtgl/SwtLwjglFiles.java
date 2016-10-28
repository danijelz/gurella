package com.gurella.studio.editor.swtgl;

import java.io.File;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;

public class SwtLwjglFiles implements Files {
	static public final String externalPath = System.getProperty("user.home") + File.separator;
	static public final String localPath = new File("").getAbsolutePath() + File.separator;
	
	private final String internalPath;

	public SwtLwjglFiles(String internalPath) {
		this.internalPath = internalPath;
	}

	@Override
	public FileHandle getFileHandle(String fileName, FileType type) {
		return new SwtLwjglFileHandle(internalPath, fileName, type);
	}

	@Override
	public FileHandle classpath(String path) {
		return new SwtLwjglFileHandle(internalPath, path, FileType.Classpath);
	}

	@Override
	public FileHandle internal(String path) {
		return new SwtLwjglFileHandle(internalPath, path, FileType.Internal);
	}

	@Override
	public FileHandle external(String path) {
		return new SwtLwjglFileHandle(internalPath, path, FileType.External);
	}

	@Override
	public FileHandle absolute(String path) {
		return new SwtLwjglFileHandle(internalPath, path, FileType.Absolute);
	}

	@Override
	public FileHandle local(String path) {
		return new SwtLwjglFileHandle(internalPath, path, FileType.Local);
	}

	@Override
	public String getExternalStoragePath() {
		throw new UnsupportedOperationException("External files unsupported");
	}

	@Override
	public boolean isExternalStorageAvailable() {
		return false;
	}

	@Override
	public String getLocalStoragePath() {
		throw new UnsupportedOperationException("Local files unsupported");
	}

	@Override
	public boolean isLocalStorageAvailable() {
		return false;
	}
}
