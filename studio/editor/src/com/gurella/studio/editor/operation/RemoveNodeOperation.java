package com.gurella.studio.editor.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.subscriptions.application.ApplicationDebugUpdateListener;
import com.gurella.studio.editor.subscription.EditorSceneActivityListener;
import com.gurella.studio.editor.utils.SceneChangedEvent;

public class RemoveNodeOperation extends AbstractOperation {
	final int editorId;
	final Scene scene;
	final SceneNode parentNode;
	final SceneNode node;
	final int index;

	public RemoveNodeOperation(int editorId, Scene scene, SceneNode parentNode, SceneNode node) {
		super("Remove node");
		this.editorId = editorId;
		this.scene = scene;
		this.parentNode = parentNode;
		this.node = node;
		this.index = node.getIndex();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		if (parentNode == null) {
			scene.removeNode(node);
		} else {
			parentNode.removeChild(node, false);
		}

		EventService.post(ApplicationDebugUpdateListener.class, l -> l.debugUpdate());
		EventService.post(editorId, EditorSceneActivityListener.class, l -> l.nodeRemoved(scene, parentNode, node));
		EventService.post(editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		if (parentNode == null) {
			scene.addNode(node);
		} else {
			parentNode.addChild(node);
		}
		EventService.post(ApplicationDebugUpdateListener.class, l -> l.debugUpdate());
		EventService.post(editorId, EditorSceneActivityListener.class, l -> l.nodeAdded(scene, parentNode, node));

		node.setIndex(index);
		EventService.post(editorId, EditorSceneActivityListener.class, l -> l.nodeIndexChanged(node, index));
		EventService.post(editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		return execute(monitor, adaptable);
	}
}
