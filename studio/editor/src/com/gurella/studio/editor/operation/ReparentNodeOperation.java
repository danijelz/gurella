package com.gurella.studio.editor.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.subscriptions.application.ApplicationDebugUpdateListener;
import com.gurella.studio.editor.event.SceneChangedEvent;
import com.gurella.studio.editor.subscription.EditorSceneActivityListener;

public class ReparentNodeOperation extends AbstractOperation {
	final int editorId;
	final SceneNode2 node;
	final SceneNode2 oldParent;
	final int oldIndex;
	final SceneNode2 newParent;
	final int newIndex;
	final Scene scene;

	public ReparentNodeOperation(int editorId, SceneNode2 node, SceneNode2 newParent, int newIndex) {
		super("Set node parent");
		this.editorId = editorId;
		this.node = node;
		this.oldParent = node.getParentNode();
		this.oldIndex = node.getIndex();
		this.newParent = newParent;
		this.newIndex = newIndex;
		this.scene = node.getScene();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		setParent(newIndex, newParent);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		setParent(oldIndex, oldParent);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		return execute(monitor, adaptable);
	}

	private void setParent(int newIndex, SceneNode2 newParent) {
		if (newParent == null) {
			scene.addNode(node);
			EventService.post(ApplicationDebugUpdateListener.class, l -> l.debugUpdate());
		} else {
			newParent.addChild(node);
		}
		EventService.post(ApplicationDebugUpdateListener.class, l -> l.debugUpdate());
		EventService.post(editorId, EditorSceneActivityListener.class, l -> l.nodeParentChanged(node, newParent));

		node.setIndex(newIndex);
		EventService.post(editorId, SceneChangedEvent.instance);
		EventService.post(editorId, EditorSceneActivityListener.class, l -> l.nodeIndexChanged(node, newIndex));
	}
}
