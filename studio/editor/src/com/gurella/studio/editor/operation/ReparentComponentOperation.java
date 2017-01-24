package com.gurella.studio.editor.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.subscriptions.application.ApplicationDebugUpdateListener;
import com.gurella.studio.editor.subscription.EditorSceneActivityListener;
import com.gurella.studio.editor.utils.SceneChangedEvent;
import com.gurella.studio.gdx.GdxContext;

public class ReparentComponentOperation extends AbstractOperation {
	final int editorId;
	final SceneNodeComponent component;
	final SceneNode oldParent;
	final int oldIndex;
	final SceneNode newParent;
	final int newIndex;

	public ReparentComponentOperation(int editorId, SceneNodeComponent component, SceneNode newParent, int newIndex) {
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

	private void setIndex(SceneNode oldParent, SceneNode newParent, int index) {
		newParent.addComponent(component);
		GdxContext.run(editorId, () -> EventService.post(ApplicationDebugUpdateListener.class, l -> l.debugUpdate()));
		EventService.post(editorId, EditorSceneActivityListener.class, l -> l.componentRemoved(oldParent, component));
		EventService.post(editorId, EditorSceneActivityListener.class, l -> l.componentAdded(newParent, component));

		component.setIndex(index);
		EventService.post(editorId, EditorSceneActivityListener.class, l -> l.componentIndexChanged(component, index));
		EventService.post(editorId, SceneChangedEvent.instance);
	}
}
