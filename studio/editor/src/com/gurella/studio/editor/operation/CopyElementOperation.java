package com.gurella.studio.editor.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneElement2;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.subscriptions.application.ApplicationDebugUpdateListener;
import com.gurella.studio.editor.event.SceneChangedEvent;
import com.gurella.studio.editor.subscription.EditorSceneActivityListener;

public class CopyElementOperation extends AbstractOperation {
	final int editorId;
	final SceneElement2 element;
	final SceneNode2 parentNode;
	final Scene scene;

	public CopyElementOperation(int editorId, SceneNode2 parentNode, Scene scene, SceneElement2 newElement) {
		super("Copy element");
		this.editorId = editorId;
		this.element = newElement;
		this.parentNode = parentNode;
		this.scene = scene;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		if (element instanceof SceneNodeComponent2) {
			SceneNodeComponent2 component = (SceneNodeComponent2) element;
			parentNode.addComponent(component);
			EventService.post(editorId, ApplicationDebugUpdateListener.class, l -> l.debugUpdate());
			EventService.post(editorId, EditorSceneActivityListener.class,
					l -> l.componentAdded(parentNode, component));
		} else {
			SceneNode2 node = (SceneNode2) element;
			if (parentNode == null) {
				scene.addNode(node);
			} else {
				parentNode.addChild(node);
			}
			EventService.post(editorId, ApplicationDebugUpdateListener.class, l -> l.debugUpdate());
			EventService.post(editorId, EditorSceneActivityListener.class, l -> l.nodeAdded(scene, parentNode, node));
		}

		EventService.post(editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		if (element instanceof SceneNodeComponent2) {
			SceneNodeComponent2 component = (SceneNodeComponent2) element;
			parentNode.removeComponent(component);
			EventService.post(ApplicationDebugUpdateListener.class, l -> l.debugUpdate());
			EventService.post(editorId, EditorSceneActivityListener.class,
					l -> l.componentRemoved(parentNode, component));
		} else {
			SceneNode2 node = (SceneNode2) element;
			if (parentNode == null) {
				scene.removeNode(node);
			} else {
				parentNode.removeChild(node);
			}
			EventService.post(ApplicationDebugUpdateListener.class, l -> l.debugUpdate());
			EventService.post(editorId, EditorSceneActivityListener.class, l -> l.nodeRemoved(scene, parentNode, node));
		}

		EventService.post(editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		return execute(monitor, adaptable);
	}
}
