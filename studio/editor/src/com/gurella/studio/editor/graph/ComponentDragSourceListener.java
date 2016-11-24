package com.gurella.studio.editor.graph;

import java.util.Optional;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;

import com.gurella.engine.scene.SceneNodeComponent2;

class ComponentDragSourceListener extends DragSourceAdapter {
	private static final LocalSelectionTransfer localTransfer = LocalSelectionTransfer.getTransfer();

	private final SceneGraphView view;

	ComponentDragSourceListener(SceneGraphView view) {
		this.view = view;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		Optional<SceneNodeComponent2> selected = view.getFirstSelectedComponent();
		if (selected.isPresent()) {
			SceneNodeComponent2 component = selected.get();
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
