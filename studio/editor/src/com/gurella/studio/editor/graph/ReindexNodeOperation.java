package com.gurella.studio.editor.graph;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.subscriptions.application.ApplicationDebugUpdateListener;
import com.gurella.studio.editor.event.SceneChangedEvent;
import com.gurella.studio.editor.subscription.NodeIndexListener;

public class ReindexNodeOperation extends AbstractOperation {
	final int editorId;
	final SceneNode2 node;
	final int oldIndex;
	final int newIndex;

	public ReindexNodeOperation(int editorId, SceneNode2 node, int oldIndex, int newIndex) {
		super("Set index");
		this.editorId = editorId;
		this.node = node;
		this.oldIndex = oldIndex;
		this.newIndex = newIndex;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		setIndex(newIndex);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		setIndex(oldIndex);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		return execute(monitor, adaptable);
	}

	private void setIndex(int index) {
		SceneNode2 parentNode = node.getParentNode();
		if (parentNode == null) {
			node.getScene().setNodeIndex(index, node);
		} else {
			parentNode.setChildNodeIndex(index, node);
		}
		EventService.post(ApplicationDebugUpdateListener.class, l -> l.debugUpdate());
		EventService.post(editorId, SceneChangedEvent.instance);
		EventService.post(editorId, NodeIndexListener.class, l -> l.nodeIndexChanged(node, index));
	}
}
