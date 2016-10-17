package com.gurella.studio.editor.utils;

import org.eclipse.core.resources.IContainer;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public class ContainerRelativeFileHandleResolver implements FileHandleResolver {
	private IContainer rootFolder;

	public ContainerRelativeFileHandleResolver(IContainer rootFolder) {
		this.rootFolder = rootFolder;
	}

	@Override
	public FileHandle resolve(String fileName) {
		return new FileHandle(rootFolder.getLocation().addTrailingSeparator().toString() + fileName);
	}
}
