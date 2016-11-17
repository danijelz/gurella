package com.gurella.studio.editor.graph;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.gurella.engine.scene.SceneNodeComponent2;

class ComponentDragSourceListener extends DragSourceAdapter {
	private static final LocalSelectionTransfer localTransfer = LocalSelectionTransfer.getTransfer();

	private final Tree graph;

	ComponentDragSourceListener(Tree graph) {
		this.graph = graph;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		TreeItem[] selection = graph.getSelection();
		if (selection.length == 1 && selection[0].getData() instanceof SceneNodeComponent2) {
			TreeItem item = selection[0];
			SceneNodeComponent2 component = (SceneNodeComponent2) item.getData();
			localTransfer.setSelection(new ComponentSelection(component));
			event.data = component;
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