package com.gurella.studio.common;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;

public class AssetsFolderLocator {
	public static String assetsFolderName = "assets";

	private AssetsFolderLocator() {
	}

	public static IFolder getAssetsFolder(IJavaProject javaProject) {
		return getAssetsFolder(javaProject.getProject());
	}

	public static IFolder getAssetsFolder(IResource resource) {
		return resource.getProject().getFolder(assetsFolderName);
	}

	public static IPath getAssetsRelativePath(IResource resource) {
		IFolder assetsFolder = getAssetsFolder(resource);
		return resource.getLocation().makeRelativeTo(assetsFolder.getLocation());
	}
	
	public static IPath getAssetsRelativePath(String location) {
		Path path = new Path(location);
		if(location.contains(assetsFolderName)) {
			ResourcesPlugin.getWorkspace();
		} else {
			return new Path(location);
		}
		
		IFolder assetsFolder = getAssetsFolder(resource);
		return resource.getLocation().makeRelativeTo(assetsFolder.getLocation());
	}
}
