package com.gurella.studio.editor.assets;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class AssetsViewerComparator extends ViewerComparator {
	@Override
	public int category(Object element) {
		if (element instanceof IFolder) {
			return 0;
		} else if (element instanceof IFile) {
			return 1;
		} else {
			throw new IllegalArgumentException("Unsupported element:" + element);
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		int cat1 = category(e1);
		int cat2 = category(e2);

		if (cat1 == cat2) {
			IResource resource1 = (IResource) e1;
			IResource resource2 = (IResource) e2;
			int result = resource1.getName().compareToIgnoreCase(resource2.getName());
			return result == 0 ? resource2.getName().compareTo(resource1.getName()) : result;
		} else {
			return cat1 - cat2;
		}
	}
}
