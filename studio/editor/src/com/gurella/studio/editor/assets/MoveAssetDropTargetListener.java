package com.gurella.studio.editor.assets;

import static com.gurella.studio.GurellaStudioPlugin.log;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.TreeItem;

import com.gurella.studio.editor.utils.Try;

class MoveAssetDropTargetListener extends DropTargetAdapter {
	private final IResource sceneResource;

	MoveAssetDropTargetListener(IResource sceneResource) {
		this.sceneResource = sceneResource;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		if ((event.operations & DND.DROP_MOVE) == 0 || getTransferingResource() == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		event.detail = DND.DROP_MOVE;
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
		if (resource == null || resource == data || sceneResource.equals(resource)) {
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
		if (resource == null || resource == folder || sceneResource.equals(resource)) {
			event.detail = DND.DROP_NONE;
			return;
		}

		//TODO update references in scenes -> refractoring
		Try.successful(resource).peek(r -> r.move(folder.getFullPath().append(resource.getName()), true, null))
				.onFailure(e -> log(e, "Error while moving resource."));
	}
}
