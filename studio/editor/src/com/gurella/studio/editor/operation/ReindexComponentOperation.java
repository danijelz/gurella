package com.gurella.studio.editor.operation;

import static com.gurella.studio.gdx.GdxContext.post;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.studio.editor.subscription.EditorSceneActivityListener;
import com.gurella.studio.editor.utils.SceneChangedEvent;

public class ReindexComponentOperation extends AbstractOperation {
	final int editorId;
	final SceneNodeComponent component;
	final int oldIndex;
	final int newIndex;

	public ReindexComponentOperation(int editorId, SceneNodeComponent component, int newIndex) {
		super("Set index");
		this.editorId = editorId;
		this.component = component;
		this.oldIndex = component.getIndex();
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
		component.setIndex(index);
		post(editorId, editorId, SceneChangedEvent.instance);
		post(editorId, editorId, EditorSceneActivityListener.class, l -> l.componentIndexChanged(component, index));
	}
}
