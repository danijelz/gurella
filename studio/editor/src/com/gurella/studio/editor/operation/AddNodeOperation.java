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
import com.gurella.studio.editor.event.NodeAddedEvent;
import com.gurella.studio.editor.event.NodeRemovedEvent;
import com.gurella.studio.editor.event.SceneChangedEvent;

public class AddNodeOperation extends AbstractOperation {
	final int editorId;
	final Scene scene;
	final SceneNode2 parentNode;
	final SceneNode2 node;

	public AddNodeOperation(int editorId, Scene scene, SceneNode2 parentNode, SceneNode2 node) {
		super("Add node");
		this.editorId = editorId;
		this.scene = scene;
		this.parentNode = parentNode;
		this.node = node;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		if (parentNode == null) {
			scene.addNode(node);
		} else {
			parentNode.addChild(node);
		}

		EventService.post(editorId, new NodeAddedEvent(scene, parentNode, node));
		EventService.post(editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		if (parentNode == null) {
			scene.removeNode(node);
		} else {
			parentNode.removeChild(node);
		}

		EventService.post(editorId, new NodeRemovedEvent(scene, parentNode, node));
		EventService.post(editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		return execute(monitor, adaptable);
	}
}
