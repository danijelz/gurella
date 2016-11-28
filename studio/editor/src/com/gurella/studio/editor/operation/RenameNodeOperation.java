package com.gurella.studio.editor.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.SceneNode;
import com.gurella.studio.editor.subscription.NodeNameChangeListener;
import com.gurella.studio.editor.utils.SceneChangedEvent;

public class RenameNodeOperation extends AbstractOperation {
	final int editorId;
	final SceneNode node;
	final String oldValue;
	final String newValue;

	public RenameNodeOperation(int editorId, SceneNode node, String newValue) {
		super("Name");
		this.editorId = editorId;
		this.node = node;
		this.oldValue = node.getName();
		this.newValue = newValue;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		node.setName(newValue);
		notifyNodeNameChanged();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		node.setName(oldValue);
		notifyNodeNameChanged();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		return execute(monitor, adaptable);
	}

	private void notifyNodeNameChanged() {
		EventService.post(editorId, NodeNameChangeListener.class, l -> l.nodeNameChanged(node));
		EventService.post(editorId, SceneChangedEvent.instance);
	}
}