package com.gurella.studio.editor.assets;

import java.util.Optional;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

class ResourceDragSourceListener implements DragSourceListener {
	private static final LocalSelectionTransfer localTransfer = LocalSelectionTransfer.getTransfer();

	private final AssetsView view;

	ResourceDragSourceListener(AssetsView view) {
		this.view = view;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		Optional<IResource> selected = view.getFirstSelectedResource();
		if (!selected.isPresent()) {
			event.doit = false;
			return;
		}

		IResource resource = selected.get();
		localTransfer.setSelection(new AssetSelection(resource));
		event.data = resource;
		event.doit = true;
		event.image = null;
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		event.data = new IResource[] { view.getFirstSelectedResource().get() };
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		localTransfer.setSelection(null);
	}
}
