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
import com.gurella.studio.editor.subscription.NodeParentListener;

public class ReparentNodeOperation extends AbstractOperation {
	final int editorId;
	final SceneNode2 node;
	final SceneNode2 oldParent;
	final SceneNode2 newParent;

	public ReparentNodeOperation(int editorId, SceneNode2 node, SceneNode2 oldParent, SceneNode2 newParent) {
		super("Set node parent");
		this.editorId = editorId;
		this.node = node;
		this.oldParent = oldParent;
		this.newParent = newParent;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		setParent(newParent, oldParent);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		setParent(oldParent, newParent);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		return execute(monitor, adaptable);
	}

	private void setParent(SceneNode2 oldParent, SceneNode2 newParent) {
		oldParent.removeChild(node);
		newParent.addChild(node);
		EventService.post(ApplicationDebugUpdateListener.class, l -> l.debugUpdate());
		EventService.post(editorId, SceneChangedEvent.instance);
		EventService.post(editorId, NodeParentListener.class, l -> l.nodeParentChanged(node, newParent));
	}
}
