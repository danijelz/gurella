package com.gurella.studio.editor.tool;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.studio.editor.SceneEditorRegistry;
import com.gurella.studio.editor.event.SceneChangedEvent;

public class RotateOperation extends TransformOperation {
	private Vector3 initial = new Vector3();
	private Vector3 action = new Vector3();

	public RotateOperation(int editorId, TransformComponent component) {
		super("Rotate", editorId, component);
		component.getEulerRotation(initial);
	}

	@Override
	void rollback() {
		component.setEulerRotation(initial);
	}

	@Override
	void commit() {
		component.getEulerRotation(action);
		if(initial.equals(action)) {
			return;
		}
		component.setEulerRotation(initial);
		SceneEditorRegistry.getContext(editorId).executeOperation(this, "Error while applying scale.");
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		component.setEulerRotation(action);
		EventService.post(editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		component.setEulerRotation(action);
		EventService.post(editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		component.setEulerRotation(initial);
		EventService.post(editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}
}
