package com.gurella.studio.editor.assets;

import java.util.Arrays;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ITreeContentProvider;

import com.gurella.studio.editor.utils.Try;

class AssetsViewerContentProvider implements ITreeContentProvider {
	private static final IResource[] emptyChildren = new IResource[0];

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IFolder) {
			IContainer container = (IContainer) parentElement;
			return Try.ofFailable(() -> container.members()).map(r -> filterResources(r)).orElse(emptyChildren);
		} else {
			return emptyChildren;
		}
	}

	private static IResource[] filterResources(IResource[] all) {
		return Arrays.stream(all).filter(r -> r instanceof IFile || r instanceof IFolder)
				.toArray(i -> new IResource[i]);
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof IResource) {
			return ((IResource) element).getParent();
		} else {
			return null;
		}
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}
}
