package com.gurella.studio.editor.assets;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.StructuredSelection;

public class AssetSelection extends StructuredSelection {
	AssetSelection(IResource assetFile) {
		super(assetFile);
	}

	public IResource getAssetResource() {
		return (IResource) getFirstElement();
	}
}
