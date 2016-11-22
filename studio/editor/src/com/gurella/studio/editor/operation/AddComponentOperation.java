package com.gurella.studio.editor.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.subscriptions.application.ApplicationDebugUpdateListener;
import com.gurella.studio.editor.subscription.EditorSceneActivityListener;
import com.gurella.studio.editor.utils.SceneChangedEvent;

public class AddComponentOperation extends AbstractOperation {
	final int editorId;
	final SceneNode2 node;
	final SceneNodeComponent2 component;

	public AddComponentOperation(int editorId, SceneNode2 node, SceneNodeComponent2 newValue) {
		super("Add component");
		this.editorId = editorId;
		this.node = node;
		this.component = newValue;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		node.addComponent(component);
		EventService.post(ApplicationDebugUpdateListener.class, l -> l.debugUpdate());
		EventService.post(editorId, EditorSceneActivityListener.class, l -> l.componentAdded(node, component));
		EventService.post(editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		node.removeComponent(component);
		EventService.post(ApplicationDebugUpdateListener.class, l -> l.debugUpdate());
		EventService.post(editorId, EditorSceneActivityListener.class, l -> l.componentRemoved(node, component));
		EventService.post(editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		return execute(monitor, adaptable);
	}
}
