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
import com.gurella.studio.editor.event.SceneChangedEvent;
import com.gurella.studio.editor.subscription.ComponentIndexListener;
import com.gurella.studio.editor.subscription.EditorSceneActivityListener;

public class ReparentComponentOperation extends AbstractOperation {
	final int editorId;
	final SceneNodeComponent2 component;
	final SceneNode2 oldParent;
	final int oldIndex;
	final SceneNode2 newParent;
	final int newIndex;

	public ReparentComponentOperation(int editorId, SceneNodeComponent2 component, SceneNode2 newParent, int newIndex) {
		super("Move component");
		this.editorId = editorId;
		this.component = component;
		this.oldParent = component.getNode();
		this.oldIndex = component.getIndex();
		this.newParent = newParent;
		this.newIndex = newIndex;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		setIndex(oldParent, newParent, newIndex);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		setIndex(newParent, oldParent, oldIndex);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		return execute(monitor, adaptable);
	}

	private void setIndex(SceneNode2 oldParent, SceneNode2 newParent, int index) {
		newParent.addComponent(component);
		EventService.post(ApplicationDebugUpdateListener.class, l -> l.debugUpdate());
		EventService.post(editorId, EditorSceneActivityListener.class, l -> l.componentRemoved(oldParent, component));
		EventService.post(editorId, EditorSceneActivityListener.class, l -> l.componentAdded(newParent, component));

		component.setIndex(index);
		EventService.post(editorId, ComponentIndexListener.class, l -> l.componentIndexChanged(component, index));
		EventService.post(editorId, SceneChangedEvent.instance);
	}
}
