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

import com.gurella.engine.scene.SceneNode;
import com.gurella.studio.editor.history.HistoryContributor;
import com.gurella.studio.editor.history.HistoryService;
import com.gurella.studio.editor.operation.ReindexNodeOperation;
import com.gurella.studio.editor.operation.ReparentNodeOperation;

class NodeDropTargetListener extends DropTargetAdapter implements HistoryContributor {
	private final Tree graph;
	private final int editorId;

	private HistoryService historyService;

	NodeDropTargetListener(Tree graph, int editorId) {
		this.graph = graph;
		this.editorId = editorId;
	}

	@Override
	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		// TODO if getTransferedNode() == null add to root
		if ((event.operations & DND.DROP_MOVE) == 0 || getTransferedNode() == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		event.detail = DND.DROP_MOVE;
	}

	private static SceneNode getTransferedNode() {
		ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
		if (selection instanceof NodeSelection) {
			return ((NodeSelection) selection).getNode();
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
		if (!(data instanceof SceneNode)) {
			event.detail = DND.DROP_NONE;
			return;
		}

		SceneNode eventNode = (SceneNode) data;
		SceneNode node = getTransferedNode();
		if (node == eventNode) {
			event.detail = DND.DROP_NONE;
			return;
		}

		Point point = event.display.map(null, graph, event.x, event.y);
		Rectangle bounds = item.getBounds();

		if (point.y < bounds.y + bounds.height / 3) {
			event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
			event.detail = DND.DROP_MOVE;
		} else if (point.y > bounds.y + 2 * bounds.height / 3) {
			event.feedback |= DND.FEEDBACK_INSERT_AFTER;
			event.detail = DND.DROP_MOVE;
		} else if (eventNode == node.getParentNode()) {
			event.detail = DND.DROP_NONE;
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
		if (!(data instanceof SceneNode)) {
			event.detail = DND.DROP_NONE;
			return;
		}

		SceneNode eventNode = (SceneNode) data;
		SceneNode node = getTransferedNode();
		if (node == eventNode) {
			event.detail = DND.DROP_NONE;
			return;
		}

		Point point = event.display.map(null, graph, event.x, event.y);
		Rectangle bounds = item.getBounds();
		int oldIndex = node.getIndex();

		if (point.y < bounds.y + bounds.height / 3) {
			if (node.getParent() == eventNode.getParent()) {
				int newIndex = eventNode.getIndex();
				newIndex = oldIndex < newIndex ? newIndex - 1 : newIndex;
				reindexNode(node, newIndex);
			} else {
				SceneNode parent = eventNode.getParentNode();
				int newIndex = eventNode.getIndex();
				newIndex = oldIndex < newIndex ? newIndex - 1 : newIndex;
				reparentNode(node, parent, newIndex);
			}
		} else if (point.y > bounds.y + 2 * bounds.height / 3) {
			if (node.getParent() == eventNode.getParent()) {
				int newIndex = eventNode.getIndex();
				newIndex = oldIndex < newIndex ? newIndex : newIndex + 1;
				reindexNode(node, newIndex);
			} else {
				SceneNode parent = eventNode.getParentNode();
				int newIndex = eventNode.getIndex();
				newIndex = oldIndex < newIndex ? newIndex : newIndex + 1;
				reparentNode(node, parent, newIndex);
			}
		} else if (eventNode != node.getParentNode()) {
			reparentNode(node, eventNode, eventNode.childNodes.size());
		}
	}

	private void reindexNode(SceneNode node, int newIndex) {
		String errorMsg = "Error while repositioning node";
		historyService.executeOperation(new ReindexNodeOperation(editorId, node, newIndex), errorMsg);
	}

	private void reparentNode(SceneNode node, SceneNode newParent, int newIndex) {
		String errorMsg = "Error while repositioning node";
		historyService.executeOperation(new ReparentNodeOperation(editorId, node, newParent, newIndex), errorMsg);
	}
}
