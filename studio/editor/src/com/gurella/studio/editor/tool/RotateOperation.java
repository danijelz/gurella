package com.gurella.studio.editor.tool;

import static com.gurella.engine.event.EventService.post;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.studio.editor.SceneEditorRegistry;
import com.gurella.studio.editor.event.SceneChangedEvent;
import com.gurella.studio.editor.subscription.PropertyChangeListener;

public class RotateOperation extends TransformOperation {
	private Property<?> property;
	private Vector3 initial = new Vector3();
	private Vector3 action = new Vector3();

	public RotateOperation(int editorId, TransformComponent component) {
		super("Rotate", editorId, component);
		property = Models.getModel(transform).getProperty("rotation");
		component.getEulerRotation(initial);
	}

	@Override
	void rollback() {
		transform.setEulerRotation(initial);
	}

	@Override
	void commit() {
		transform.getEulerRotation(action);
		if (initial.equals(action)) {
			return;
		}
		transform.setEulerRotation(initial);
		SceneEditorRegistry.getContext(editorId).executeOperation(this, "Error while applying scale.");
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		transform.setEulerRotation(action);
		EventService.post(editorId, SceneChangedEvent.instance);
		post(editorId, PropertyChangeListener.class, l -> l.propertyChanged(transform, property, action));
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		transform.setEulerRotation(action);
		EventService.post(editorId, SceneChangedEvent.instance);
		post(editorId, PropertyChangeListener.class, l -> l.propertyChanged(transform, property, action));
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		transform.setEulerRotation(initial);
		EventService.post(editorId, SceneChangedEvent.instance);
		post(editorId, PropertyChangeListener.class, l -> l.propertyChanged(transform, property, initial));
		return Status.OK_STATUS;
	}
}
