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

import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.operation.ReindexComponentOperation;

class ComponentDropTargetListener extends DropTargetAdapter {
	private final Tree graph;
	private final SceneEditorContext context;

	ComponentDropTargetListener(Tree graph, SceneEditorContext context) {
		this.graph = graph;
		this.context = context;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		if ((event.operations & DND.DROP_MOVE) == 0 || getTransferComponent() == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		event.detail = DND.DROP_MOVE;
	}

	private static SceneNodeComponent2 getTransferComponent() {
		ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
		if (selection instanceof MoveComponentSelection) {
			return ((MoveComponentSelection) selection).getComponent();
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
			event.detail = DND.DROP_MOVE;
		} else if (point.y >= bounds.y + bounds.height / 2) {
			event.feedback |= DND.FEEDBACK_INSERT_AFTER;
			event.detail = DND.DROP_MOVE;
		}
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
		SceneNode2 node = component.getNode();
		int oldIndex = node.getComponentIndex(component);

		if (point.y < bounds.y + bounds.height / 2) {
			int newIndex = node.getComponentIndex(eventComponent);
			newIndex = oldIndex < newIndex ? newIndex - 1 : newIndex;
			reindexComponent(component, oldIndex, newIndex);
		} else if (point.y >= bounds.y + bounds.height / 2) {
			int newIndex = node.getComponentIndex(eventComponent);
			newIndex = oldIndex < newIndex ? newIndex : newIndex + 1;
			reindexComponent(component, oldIndex, newIndex);
		}
	}

	private void reindexComponent(SceneNodeComponent2 component, int oldIndex, int newIndex) {
		int editorId = context.editorId;
		String errorMsg = "Error while repositioning component";
		context.executeOperation(new ReindexComponentOperation(editorId, component, oldIndex, newIndex), errorMsg);
	}
}
