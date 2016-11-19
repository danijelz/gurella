package com.gurella.studio.editor.graph.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.subscriptions.application.ApplicationDebugUpdateListener;
import com.gurella.studio.editor.event.SceneChangedEvent;
import com.gurella.studio.editor.subscription.ComponentIndexListener;

public class ReindexComponentOperation extends AbstractOperation {
	final int editorId;
	final SceneNodeComponent2 component;
	final int oldIndex;
	final int newIndex;

	public ReindexComponentOperation(int editorId, SceneNodeComponent2 component, int oldIndex, int newIndex) {
		super("Set index");
		this.editorId = editorId;
		this.component = component;
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
		component.getNode().setComponentIndex(index, component);
		EventService.post(ApplicationDebugUpdateListener.class, l -> l.debugUpdate());
		EventService.post(editorId, SceneChangedEvent.instance);
		EventService.post(editorId, ComponentIndexListener.class, l -> l.componentIndexChanged(component, index));
	}
}
