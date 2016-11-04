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

public class TranslateOperation extends TransformOperation {
	private Vector3 initial = new Vector3();
	private Vector3 action = new Vector3();

	public TranslateOperation(int editorId, TransformComponent component) {
		super("Translate", editorId, component);
		component.getTranslation(initial);
		System.out.println("initial: " + initial);
	}

	@Override
	void rollback() {
		System.out.println("rollback: " + initial);
		component.setTranslation(initial);
		System.out.println("rollback: " + component.getTranslation(initial));
	}

	@Override
	void commit() {
		component.getTranslation(action);
		if(initial.equals(action)) {
			return;
		}
		component.setTranslation(initial);
		SceneEditorRegistry.getContext(editorId).executeOperation(this, "Error while applying translation.");
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		component.setTranslation(action);
		EventService.post(editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		component.setTranslation(action);
		EventService.post(editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		component.setTranslation(initial);
		EventService.post(editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}
}
