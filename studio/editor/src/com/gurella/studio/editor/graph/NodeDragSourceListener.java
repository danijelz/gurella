package com.gurella.studio.editor.graph;

import java.util.Optional;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;

import com.gurella.engine.scene.SceneNode;

class NodeDragSourceListener extends DragSourceAdapter {
	private static final LocalSelectionTransfer localTransfer = LocalSelectionTransfer.getTransfer();

	private final SceneGraphView view;

	NodeDragSourceListener(SceneGraphView view) {
		this.view = view;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		Optional<SceneNode> selected = view.getSelectedNode();
		if (selected.isPresent()) {
			SceneNode node = selected.get();
			localTransfer.setSelection(new NodeSelection(node));
			event.data = node;
			event.doit = true;
		} else {
			event.doit = false;
		}
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		localTransfer.setSelection(null);
	}
}
