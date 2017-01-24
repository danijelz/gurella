package com.gurella.studio.editor.operation;

import static com.gurella.studio.gdx.GdxContext.post;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.subscriptions.application.ApplicationDebugUpdateListener;
import com.gurella.studio.editor.subscription.EditorSceneActivityListener;
import com.gurella.studio.editor.utils.SceneChangedEvent;
import com.gurella.studio.gdx.GdxContext;

public class RemoveComponentOperation extends AbstractOperation {
	final int editorId;
	final SceneNode node;
	final SceneNodeComponent component;
	final int index;

	public RemoveComponentOperation(int editorId, SceneNode node, SceneNodeComponent component) {
		super("Remove component");
		this.editorId = editorId;
		this.node = node;
		this.component = component;
		this.index = component.getIndex();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		node.removeComponent(component, false);
		GdxContext.removeFromBundle(editorId, node, component.ensureUuid(), component);
		post(editorId, ApplicationDebugUpdateListener.class, l -> l.debugUpdate());
		post(editorId, editorId, EditorSceneActivityListener.class, l -> l.componentRemoved(node, component));
		post(editorId, editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		node.addComponent(component);
		GdxContext.addToBundle(editorId, node, component.ensureUuid(), component);

		post(editorId, ApplicationDebugUpdateListener.class, l -> l.debugUpdate());
		post(editorId, editorId, EditorSceneActivityListener.class, l -> l.componentAdded(node, component));

		component.setIndex(index);
		post(editorId, editorId, EditorSceneActivityListener.class, l -> l.componentIndexChanged(component, index));
		post(editorId, editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		return execute(monitor, adaptable);
	}
}
