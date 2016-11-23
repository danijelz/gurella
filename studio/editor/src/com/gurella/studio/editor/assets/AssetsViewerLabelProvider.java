package com.gurella.studio.editor.assets;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.gurella.engine.asset.AssetType;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;

class AssetsViewerLabelProvider extends BaseLabelProvider implements ILabelProvider {
	@Override
	public Image getImage(Object element) {
		if (element instanceof IFolder) {
			return getPlatformImage(ISharedImages.IMG_OBJ_FOLDER);
		} else if (element instanceof IFile) {
			IFile file = (IFile) element;
			String extension = file.getFileExtension();
			if (Values.isBlank(extension)) {
				return getPlatformImage(ISharedImages.IMG_OBJ_FILE);
			} else if (AssetType.texture.containsExtension(extension)
					|| AssetType.pixmap.containsExtension(extension)) {
				return GurellaStudioPlugin.getImage("icons/picture.png");
			} else if (AssetType.sound.containsExtension(extension)) {
				return GurellaStudioPlugin.getImage("icons/music.png");
			} else if (AssetType.textureAtlas.containsExtension(extension)) {
				return GurellaStudioPlugin.getImage("icons/textureAtlas.gif");
			} else if (AssetType.polygonRegion.containsExtension(extension)) {
				return GurellaStudioPlugin.getImage("icons/textureAtlas.gif");
			} else if (AssetType.bitmapFont.containsExtension(extension)) {
				return GurellaStudioPlugin.getImage("icons/font.png");
			} else if (AssetType.model.containsExtension(extension)) {
				return GurellaStudioPlugin.getImage("icons/16-cube-green_16x16.png");
			} else if (AssetType.prefab.containsExtension(extension)) {
				return GurellaStudioPlugin.getImage("icons/ice_cube.png");
			} else if (AssetType.material.containsExtension(extension)) {
				return GurellaStudioPlugin.getImage("icons/material.png");
			} else {
				return getPlatformImage(ISharedImages.IMG_OBJ_FILE);
			}
		} else {
			return null;
		}
	}

	private static Image getPlatformImage(String symbolicName) {
		return PlatformUI.getWorkbench().getSharedImages().getImage(symbolicName);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IResource) {
			return ((IResource) element).getName();
		} else {
			return null;
		}
	}
}
