package com.gurella.studio.editor.graph;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.studio.editor.SceneEditorContext;

class SceneGraphDropTargetListener extends DropTargetAdapter {
	private static final LocalSelectionTransfer localTransfer = LocalSelectionTransfer.getTransfer();
	private final Tree graph;
	private final SceneEditorContext context;

	SceneGraphDropTargetListener(Tree graph, SceneEditorContext context) {
		this.graph = graph;
		this.context = context;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		if ((event.operations & DND.DROP_MOVE) == 0 || getTransferComponent() == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		if (event.detail == DND.DROP_DEFAULT) {
			event.detail = DND.DROP_MOVE;
		}
	}

	private static SceneNodeComponent2 getTransferComponent() {
		ISelection selection = localTransfer.getSelection();
		if (selection instanceof SceneGraphComponentSelection) {
			return ((SceneGraphComponentSelection) selection).getComponent();
		} else {
			return null;
		}
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
		if (!(data instanceof SceneNodeComponent2)) {
			event.detail = DND.DROP_NONE;
			return;
		}

		SceneNodeComponent2 eventComponent = (SceneNodeComponent2) data;
		SceneNodeComponent2 component = getTransferComponent();
		if (component == eventComponent) {
			event.detail = DND.DROP_NONE;
			return;
		}

		if (component.getNodeId() != eventComponent.getNodeId()) {
			event.detail = DND.DROP_NONE;
			return;
		}

		Point point = event.display.map(null, graph, event.x, event.y);
		Rectangle bounds = item.getBounds();

		if (point.y < bounds.y + bounds.height / 2) {
			event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
		} else if (point.y >= bounds.y + bounds.height / 2) {
			event.feedback |= DND.FEEDBACK_INSERT_AFTER;
		}
	}

	@Override
	public void drop(DropTargetEvent event) {
		if (event.item == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		TreeItem item = (TreeItem) event.item;
		Object data = item.getData();
		if (!(data instanceof SceneNodeComponent2)) {
			event.detail = DND.DROP_NONE;
			return;
		}

		SceneNodeComponent2 eventComponent = (SceneNodeComponent2) data;
		SceneNodeComponent2 component = getTransferComponent();
		if (component == eventComponent) {
			event.detail = DND.DROP_NONE;
			return;
		}

		if (component.getNodeId() != eventComponent.getNodeId()) {
			event.detail = DND.DROP_NONE;
			return;
		}

		Point point = event.display.map(null, graph, event.x, event.y);
		Rectangle bounds = item.getBounds();

		if (point.y < bounds.y + bounds.height / 2) {
			int oldIndex = component.getIndex();
			int newIndex = eventComponent.getIndex();
			ReindexSceneElementOperation operation = new ReindexSceneElementOperation(context.editorId, component,
					oldIndex, newIndex);
			context.executeOperation(operation, "Error while repositioning element");
		} else if (point.y >= bounds.y + bounds.height / 2) {
			int oldIndex = component.getIndex();
			int newIndex = eventComponent.getIndex() + 1;
			ReindexSceneElementOperation operation = new ReindexSceneElementOperation(context.editorId, component,
					oldIndex, newIndex);
			context.executeOperation(operation, "Error while repositioning element");
		}
	}
}
