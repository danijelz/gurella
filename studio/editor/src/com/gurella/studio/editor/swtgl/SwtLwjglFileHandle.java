package com.gurella.studio.editor.swtgl;

import java.io.File;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglFileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class SwtLwjglFileHandle extends FileHandle {
	private final String internalPath;

	public SwtLwjglFileHandle(String internalPath, String fileName, FileType type) {
		super(fileName, type);
		this.internalPath = internalPath;
	}

	public SwtLwjglFileHandle(String internalPath, File file, FileType type) {
		super(file, type);
		this.internalPath = internalPath;
	}

	@Override
	public FileHandle child(String name) {
		if (file.getPath().length() == 0) {
			return new LwjglFileHandle(new File(name), type);
		}
		return new LwjglFileHandle(new File(file, name), type);
	}

	@Override
	public FileHandle sibling(String name) {
		if (file.getPath().length() == 0) {
			throw new GdxRuntimeException("Cannot get the sibling of the root.");
		}
		return new LwjglFileHandle(new File(file.getParent(), name), type);
	}

	@Override
	public FileHandle parent() {
		File parent = file.getParentFile();
		if (parent == null) {
			if (type == FileType.Absolute) {
				parent = new File("/");
			} else {
				parent = new File("");
			}
		}
		return new LwjglFileHandle(parent, type);
	}

	@Override
	public File file() {
		if (type == FileType.External) {
			return new File(SwtLwjglFiles.externalPath, file.getPath());
		} else if (type == FileType.Local) {
			return new File(SwtLwjglFiles.localPath, file.getPath());
		} else if (type == FileType.Local) {
			return new File(internalPath, file.getPath());
		}
		return file;
	}
}
