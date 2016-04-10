package com.gurella.studio.editor.assets;

import java.util.Arrays;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.TreeItem;

public class AssetsTreeChangedListener implements IResourceChangeListener {
	private AssetsExplorerView assetsExplorerView;
	private IResource rootResource;

	public AssetsTreeChangedListener(AssetsExplorerView assetsExplorerView) {
		this.assetsExplorerView = assetsExplorerView;
		rootResource = assetsExplorerView.rootResource;
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

		resourceChanged(assetsExplorerView.tree.getItem(0), assetsDelta);
	}

	private void resourceChanged(TreeItem item, IResourceDelta delta) {
		for (IResourceDelta childDelta : delta.getAffectedChildren()) {
			IResource resource = (IResource) item.getData();
			IPath rootResourcePath = resource.getFullPath();
			IPath changedResourcePath = childDelta.getFullPath();
			if (childDelta.getKind() == IResourceDelta.ADDED || childDelta.getKind() == IResourceDelta.CHANGED || childDelta.getKind() == IResourceDelta.NO_CHANGE) {
				IPath relativePath = changedResourcePath.removeFirstSegments(rootResourcePath.segmentCount());
				TreeItem childItem = findChildItem(item, relativePath.segment(0));
				if (childItem == null) {
					childItem = assetsExplorerView.createItem(item, childDelta.getResource());
				}
				resourceChanged(childItem, childDelta);
			} else if (childDelta.getKind() == IResourceDelta.REMOVED) {
				//TODO 
			}
		}
	}

	private static TreeItem findChildItem(TreeItem item, String name) {
		return Arrays.stream(item.getItems())
				.filter(treeItem -> ((IResource) treeItem.getData()).getName().equals(name)).findFirst().orElse(null);
	}
}
