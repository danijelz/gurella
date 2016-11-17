package com.gurella.studio.editor.graph;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.SceneElement2;
import com.gurella.studio.editor.event.SceneChangedEvent;
import com.gurella.studio.editor.subscription.SceneElementIndexListener;

public class ReindexSceneElementOperation extends AbstractOperation {
	final int editorId;
	final SceneElement2 element;
	final int oldIndex;
	final int newIndex;

	public ReindexSceneElementOperation(int editorId, SceneElement2 element, int oldIndex, int newIndex) {
		super("Set index");
		this.editorId = editorId;
		this.element = element;
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
		element.setIndex(index);
		EventService.post(editorId, SceneChangedEvent.instance);
		EventService.post(editorId, SceneElementIndexListener.class, l -> l.indexChanged(element, index));
	}
}
