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

import com.gurella.engine.scene.SceneElement;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.operation.ReindexComponentOperation;
import com.gurella.studio.editor.operation.ReparentComponentOperation;

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
		} else {
			event.detail = DND.DROP_MOVE;
		}
	}

	private static SceneNodeComponent getTransferComponent() {
		ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
		if (selection instanceof ComponentSelection) {
			return ((ComponentSelection) selection).getComponent();
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
		if (!(data instanceof SceneNodeComponent) && !(data instanceof SceneNode)) {
			event.detail = DND.DROP_NONE;
			return;
		}

		SceneElement eventElement = (SceneElement) data;
		SceneNodeComponent component = getTransferComponent();
		if (component == eventElement) {
			event.detail = DND.DROP_NONE;
			return;
		}

		SceneNode eventNode;
		if (eventElement instanceof SceneNode) {
			eventNode = (SceneNode) eventElement;
			if (component.getNode() == eventNode) {
				event.detail = DND.DROP_NONE;
				return;
			}
		} else {
			SceneNodeComponent eventComponent = (SceneNodeComponent) data;
			eventNode = eventComponent.getNode();
		}

		Point point = event.display.map(null, graph, event.x, event.y);
		Rectangle bounds = item.getBounds();

		if (component.getNode() == eventNode) {
			if (point.y < bounds.y + bounds.height / 2) {
				event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
				event.detail = DND.DROP_MOVE;
			} else if (point.y >= bounds.y + bounds.height / 2) {
				event.feedback |= DND.FEEDBACK_INSERT_AFTER;
				event.detail = DND.DROP_MOVE;
			}
		} else if (eventNode.hasComponent(component.getClass(), true)) {
			event.detail = DND.DROP_NONE;
			return;
		} else if (data instanceof SceneNodeComponent) {
			if (point.y < bounds.y + bounds.height / 2) {
				event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
				event.detail = DND.DROP_MOVE;
			} else if (point.y >= bounds.y + bounds.height / 2) {
				event.feedback |= DND.FEEDBACK_INSERT_AFTER;
				event.detail = DND.DROP_MOVE;
			}
		} else {
			event.feedback |= DND.FEEDBACK_SELECT;
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
		if (!(data instanceof SceneNodeComponent) && !(data instanceof SceneNode)) {
			event.detail = DND.DROP_NONE;
			return;
		}

		SceneElement eventElement = (SceneElement) data;
		SceneNodeComponent component = getTransferComponent();
		if (component == eventElement) {
			event.detail = DND.DROP_NONE;
			return;
		}

		SceneNode eventNode;
		if (eventElement instanceof SceneNode) {
			eventNode = (SceneNode) eventElement;
			if (component.getNode() == eventNode) {
				event.detail = DND.DROP_NONE;
				return;
			}
		} else {
			SceneNodeComponent eventComponent = (SceneNodeComponent) data;
			eventNode = eventComponent.getNode();
		}

		int oldIndex = component.getIndex();
		Point point = event.display.map(null, graph, event.x, event.y);
		Rectangle bounds = item.getBounds();

		if (component.getNode() == eventNode) {
			SceneNodeComponent eventComponent = (SceneNodeComponent) data;

			if (point.y < bounds.y + bounds.height / 2) {
				int newIndex = eventComponent.getIndex();
				newIndex = oldIndex < newIndex ? newIndex - 1 : newIndex;
				reindexComponent(component, newIndex);
			} else if (point.y >= bounds.y + bounds.height / 2) {
				int newIndex = eventComponent.getIndex();
				newIndex = oldIndex < newIndex ? newIndex : newIndex + 1;
				reindexComponent(component, newIndex);
			}
		} else if (eventNode.hasComponent(component.getClass(), true)) {
			event.detail = DND.DROP_NONE;
		} else if (data instanceof SceneNodeComponent) {
			SceneNodeComponent eventComponent = (SceneNodeComponent) data;
			if (point.y < bounds.y + bounds.height / 2) {
				int newIndex = eventComponent.getIndex();
				reparentComponent(component, eventNode, newIndex);
			} else if (point.y >= bounds.y + bounds.height / 2) {
				int newIndex = eventComponent.getIndex() + 1;
				reparentComponent(component, eventNode, newIndex);
			}
		} else {
			int newIndex = eventNode.components.size();
			reparentComponent(component, eventNode, newIndex);
		}
	}

	private void reindexComponent(SceneNodeComponent component, int newIndex) {
		int editorId = context.editorId;
		String errorMsg = "Error while repositioning component";
		context.executeOperation(new ReindexComponentOperation(editorId, component, newIndex), errorMsg);
	}

	private void reparentComponent(SceneNodeComponent component, SceneNode newParent, int newIndex) {
		int editorId = context.editorId;
		String errorMsg = "Error while repositioning element";
		context.executeOperation(new ReparentComponentOperation(editorId, component, newParent, newIndex), errorMsg);
	}
}
