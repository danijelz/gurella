package com.gurella.studio.editor.scene.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.studio.editor.scene.event.ComponentAddedEvent;
import com.gurella.studio.editor.scene.event.ComponentRemovedEvent;
import com.gurella.studio.editor.scene.event.SceneChangedEvent;

public class RemoveComponentOperation extends AbstractOperation {
	final int editorId;
	final SceneNode2 node;
	final SceneNodeComponent2 component;

	public RemoveComponentOperation(int editorId, SceneNode2 node, SceneNodeComponent2 newValue) {
		super("Remove component");
		this.editorId = editorId;
		this.node = node;
		this.component = newValue;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		node.removeComponent(component);
		EventService.post(editorId, new ComponentRemovedEvent(node, component));
		EventService.post(editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		node.addComponent(component);
		EventService.post(editorId, new ComponentAddedEvent(node, component));
		EventService.post(editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		return execute(monitor, adaptable);
	}
}
