package com.gurella.studio.editor.assets;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.StructuredSelection;

public class AssetSelection extends StructuredSelection {
	AssetSelection(IFile assetFile) {
		super(assetFile);
	}

	public IFile getAssetFile() {
		return (IFile) getFirstElement();
	}
}
