package com.gurella.studio.editor.assets;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.TreeItem;

class MoveAssetDropTargetListener extends DropTargetAdapter {
	private final AssetsView view;
	private final IFile sceneFile;

	MoveAssetDropTargetListener(AssetsView view) {
		this.view = view;
		this.sceneFile = view.context.sceneFile;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		if ((event.operations & DND.DROP_MOVE) == 0 || getTransferingResource() == null) {
			event.detail = DND.DROP_NONE;
		} else {
			event.detail = DND.DROP_MOVE;
		}
	}

	private static IResource getTransferingResource() {
		ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
		if (!(selection instanceof AssetSelection)) {
			return null;
		}

		IResource resource = ((AssetSelection) selection).getAssetResource();
		return resource instanceof IFile || resource instanceof IFolder ? resource : null;
	}

	@Override
	public void dragOver(DropTargetEvent event) {
		event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;

		if (event.item == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		TreeItem item = (TreeItem) event.item;
		Object data = item.getData();
		if (!(data instanceof IFolder)) {
			event.detail = DND.DROP_NONE;
			return;
		}

		IResource resource = getTransferingResource();
		if (resource == null || resource == data || sceneFile.equals(resource)) {
			event.detail = DND.DROP_NONE;
			return;
		}

		event.detail = DND.DROP_MOVE;
		event.feedback |= DND.FEEDBACK_SELECT;
	}

	@Override
	public void dropAccept(DropTargetEvent event) {
		event.detail = DND.DROP_MOVE;
	}

	@Override
	public void drop(DropTargetEvent event) {
		if (event.item == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		TreeItem item = (TreeItem) event.item;
		Object data = item.getData();
		if (!(data instanceof IFolder)) {
			event.detail = DND.DROP_NONE;
			return;
		}

		IFolder folder = (IFolder) data;
		IResource resource = getTransferingResource();
		if (resource == null || resource == folder || sceneFile.equals(resource)) {
			event.detail = DND.DROP_NONE;
			return;
		}

		view.move(resource, folder);
	}
}
