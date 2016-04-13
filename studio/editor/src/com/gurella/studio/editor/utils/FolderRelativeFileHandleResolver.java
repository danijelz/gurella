package com.gurella.studio.editor.utils;

import org.eclipse.core.resources.IFolder;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public class FolderRelativeFileHandleResolver implements FileHandleResolver {
	private IFolder rootFolder;

	public FolderRelativeFileHandleResolver(IFolder assetsRootFolder) {
		this.rootFolder = assetsRootFolder;
	}

	@Override
	public FileHandle resolve(String fileName) {
		return new FileHandle(rootFolder.getLocation().addTrailingSeparator().toString() + fileName);
	}
}
