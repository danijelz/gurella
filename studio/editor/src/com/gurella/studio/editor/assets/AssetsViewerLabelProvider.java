package com.gurella.studio.editor.assets;

import static com.gurella.engine.asset.descriptor.DefaultAssetDescriptors.appConfig;
import static com.gurella.engine.asset.descriptor.DefaultAssetDescriptors.assetProps;
import static com.gurella.engine.asset.descriptor.DefaultAssetDescriptors.bitmapFont;
import static com.gurella.engine.asset.descriptor.DefaultAssetDescriptors.material;
import static com.gurella.engine.asset.descriptor.DefaultAssetDescriptors.model;
import static com.gurella.engine.asset.descriptor.DefaultAssetDescriptors.pixmap;
import static com.gurella.engine.asset.descriptor.DefaultAssetDescriptors.polygonRegion;
import static com.gurella.engine.asset.descriptor.DefaultAssetDescriptors.prefab;
import static com.gurella.engine.asset.descriptor.DefaultAssetDescriptors.scene;
import static com.gurella.engine.asset.descriptor.DefaultAssetDescriptors.sound;
import static com.gurella.engine.asset.descriptor.DefaultAssetDescriptors.texture;
import static com.gurella.engine.asset.descriptor.DefaultAssetDescriptors.textureAtlas;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

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
			} else if (texture.isValidExtension(extension) || pixmap.isValidExtension(extension)) {
				return GurellaStudioPlugin.getImage("icons/picture.png");
			} else if (sound.isValidExtension(extension)) {
				return GurellaStudioPlugin.getImage("icons/music.png");
			} else if (textureAtlas.isValidExtension(extension)) {
				return GurellaStudioPlugin.getImage("icons/textureAtlas.gif");
			} else if (polygonRegion.isValidExtension(extension)) {
				return GurellaStudioPlugin.getImage("icons/textureAtlas.gif");
			} else if (bitmapFont.isValidExtension(extension)) {
				return GurellaStudioPlugin.getImage("icons/font.png");
			} else if (model.isValidExtension(extension)) {
				return GurellaStudioPlugin.getImage("icons/16-cube-green_16x16.png");
			} else if (prefab.isValidExtension(extension)) {
				return GurellaStudioPlugin.getImage("icons/ice_cube.png");
			} else if (material.isValidExtension(extension)) {
				return GurellaStudioPlugin.getImage("icons/material.png");
			} else if (scene.isValidExtension(extension) || appConfig.isValidExtension(extension)) {
				return GurellaStudioPlugin.getImage("icons/logo16.png");
			} else if (assetProps.isValidExtension(extension)) {
				return GurellaStudioPlugin.getImage("icons/showproperties_obj.gif");
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
