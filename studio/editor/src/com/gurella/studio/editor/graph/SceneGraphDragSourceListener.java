package com.gurella.studio.editor.graph;

import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.Tree;

class SceneGraphDragSourceListener implements DragSourceListener {
	private ComponentDragSourceListener componentDragSourceListener;
	private NodeDragSourceListener nodeDragSourceListener;
	private DragSourceListener active;

	SceneGraphDragSourceListener(Tree graph) {
		componentDragSourceListener = new ComponentDragSourceListener(graph);
		nodeDragSourceListener = new NodeDragSourceListener(graph);
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		componentDragSourceListener.dragStart(event);
		if (event.doit) {
			active = componentDragSourceListener;
			return;
		}

		nodeDragSourceListener.dragStart(event);
		if (event.doit) {
			active = nodeDragSourceListener;
		}
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		if (active != null) {
			active.dragSetData(event);
		}
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		if (active != null) {
			active.dragFinished(event);
			active = null;
		}
	}
}
