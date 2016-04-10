package com.gurella.studio.editor.assets;

import java.util.Arrays;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.TreeItem;

public class AssetsTreeChangedListener implements IResourceChangeListener {
	private AssetsExplorerView assetsExplorer;
	private IResource rootResource;

	public AssetsTreeChangedListener(AssetsExplorerView assetsExplorer) {
		this.assetsExplorer = assetsExplorer;
		rootResource = assetsExplorer.rootResource;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta mainDelta = event.getDelta();
		if (mainDelta == null) {
			return;
		}

		IResourceDelta assetsDelta = mainDelta.findMember(rootResource.getFullPath());
		if (assetsDelta == null) {
			return;
		}

		assetsExplorer.getDisplay().asyncExec(() -> resourceChanged(assetsExplorer.tree.getItem(0), assetsDelta));
	}

	private void resourceChanged(TreeItem item, IResourceDelta delta) {
		for (IResourceDelta childDelta : delta.getAffectedChildren()) {
			IResource resource = (IResource) item.getData();
			IPath rootResourcePath = resource.getFullPath();
			IPath changedResourcePath = childDelta.getFullPath();
			IPath relativePath = changedResourcePath.removeFirstSegments(rootResourcePath.segmentCount());
			int kind = childDelta.getKind();
			if (kind == IResourceDelta.ADDED || kind == IResourceDelta.CHANGED) {
				TreeItem childItem = findChildItem(item, relativePath.segment(0));
				if (childItem == null) {
					childItem = assetsExplorer.createItem(item, childDelta.getResource());
				}
				resourceChanged(childItem, childDelta);
			} else if (kind == IResourceDelta.REMOVED) {
				TreeItem childItem = findChildItem(item, relativePath.segment(0));
				if (childItem != null) {
					childItem.dispose();
				}
			}
		}
	}

	private static TreeItem findChildItem(TreeItem item, String name) {
		return Arrays.stream(item.getItems())
				.filter(treeItem -> ((IResource) treeItem.getData()).getName().equals(name)).findFirst().orElse(null);
	}
}
