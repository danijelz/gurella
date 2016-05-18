package com.gurella.studio.editor.assets;

import java.util.Arrays;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.TreeItem;

import com.gurella.studio.GurellaStudioPlugin;

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
					int index = getChildItemIndex(item, resource, childDelta.getResource());
					childItem = assetsExplorer.createItem(item, childDelta.getResource(), index);
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

	private int getChildItemIndex(TreeItem item, IResource parentResource, IResource childResource) {
		try {
			return getChildItemIndexSafely(item, parentResource, childResource);
		} catch (Exception e) {
			String message = "Error updating assets tree";
			IStatus status = GurellaStudioPlugin.log(e, message);
			ErrorDialog.openError(assetsExplorer.getShell(), message, e.getLocalizedMessage(), status);
			return 0;
		}
	}

	private static int getChildItemIndexSafely(TreeItem item, IResource parentResource, IResource childResource)
			throws CoreException {
		int index = 0;
		IContainer container = (IContainer) parentResource;
		IResource[] members = container.members();
		
		for (IResource resource : members) {
			if (childResource.equals(resource)) {
				return index;
			} else if (findChildItem(item, resource) != null) {
				index++;
			}
		}

		return index;
	}

	private static TreeItem findChildItem(TreeItem item, IResource resource) {
		return Arrays.stream(item.getItems()).filter(treeItem -> ((IResource) treeItem.getData()).equals(resource))
				.findFirst().orElse(null);
	}

	private static TreeItem findChildItem(TreeItem item, String name) {
		return Arrays.stream(item.getItems())
				.filter(treeItem -> ((IResource) treeItem.getData()).getName().equals(name)).findFirst().orElse(null);
	}
}
