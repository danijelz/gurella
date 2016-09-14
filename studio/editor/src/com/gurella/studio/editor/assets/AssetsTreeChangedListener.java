package com.gurella.studio.editor.assets;

import java.util.Arrays;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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

	private static int getChildItemIndex(TreeItem item, IResource parentResource, IResource childResource) {
		try {
			return getChildItemIndexSafely(item, parentResource, childResource);
		} catch (Exception e) {
			GurellaStudioPlugin.log(e, "Error updating assets tree");
			return 0;
		}
	}

	private static int getChildItemIndexSafely(TreeItem item, IResource parentResource, IResource childResource)
			throws CoreException {
		int index = 0;
		IContainer container = (IContainer) parentResource;
		IResource[] members = container.members();

		for (IResource member : members) {
			if (findChildItem(item, member) != null && compareResource(member, childResource) < 0) {
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

	private static int compareResource(IResource r1, IResource r2) {
		if (r1 instanceof IContainer && r2 instanceof IContainer) {
			return compareNames(r1, r2);
		} else if (r1 instanceof IContainer) {
			return -1;
		} else if (r2 instanceof IContainer) {
			return 1;
		} else {
			return compareNames(r1, r2);
		}
	}

	private static int compareNames(IResource resource1, IResource resource2) {
		int result = resource1.getName().compareToIgnoreCase(resource2.getName());
		return result == 0 ? resource2.getName().compareTo(resource1.getName()) : result;
	}
}
