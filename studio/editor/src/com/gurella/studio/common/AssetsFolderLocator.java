package com.gurella.studio.common;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;

public class AssetsFolderLocator {
	public static String assetsFolderName = "assets";

	AssetsFolderLocator() {
	}

	public static IFolder getAssetsFolder(IJavaProject javaProject) {
		return getAssetsFolder(javaProject.getProject());
	}

	public static IFolder getAssetsFolder(IProject project) {
		return project.getFolder(assetsFolderName);
	}
}
