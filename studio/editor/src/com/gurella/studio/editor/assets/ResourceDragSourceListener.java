package com.gurella.studio.editor.assets;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

class ResourceDragSourceListener implements DragSourceListener {
	private final Tree tree;

	ResourceDragSourceListener(Tree tree) {
		this.tree = tree;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		TreeItem[] selection = tree.getSelection();
		if (selection.length != 1) {
			event.doit = false;
			return;
		}

		Object data = selection[0].getData();
		if (!(data instanceof IResource)) {
			event.doit = false;
			return;
		}

		LocalSelectionTransfer.getTransfer().setSelection(new AssetSelection((IResource) data));
		event.data = data;
		event.doit = true;
		event.image = null;
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		TreeItem[] selection = tree.getSelection();
		IResource resource = (IResource) selection[0].getData();
		event.data = new IResource[] { resource };
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		LocalSelectionTransfer.getTransfer().setSelection(null);
	}
}
