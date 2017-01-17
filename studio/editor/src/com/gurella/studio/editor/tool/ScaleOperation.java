package com.gurella.studio.editor.tool;

import static com.gurella.engine.event.EventService.post;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.event.EventService;
import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.metatype.Property;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.studio.editor.history.HistoryService;
import com.gurella.studio.editor.subscription.PropertyChangeListener;
import com.gurella.studio.editor.utils.SceneChangedEvent;

public class ScaleOperation extends TransformOperation {
	private Property<?> property;
	private Vector3 initial = new Vector3();
	private Vector3 action = new Vector3();

	public ScaleOperation(int editorId, TransformComponent component) {
		super("Scale", editorId, component);
		property = MetaTypes.getMetaType(transform).getProperty("scale");
		component.getScale(initial);
	}

	@Override
	void rollback() {
		transform.setScale(initial);
	}

	@Override
	void commit(HistoryService historyService) {
		transform.getScale(action);
		if (initial.equals(action)) {
			return;
		}

		transform.setScale(initial);
		historyService.executeOperation(this, "Error while applying scale.");
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		transform.setScale(action);
		EventService.post(editorId, SceneChangedEvent.instance);
		post(editorId, PropertyChangeListener.class, l -> l.propertyChanged(transform, property, action));
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		transform.setScale(action);
		EventService.post(editorId, SceneChangedEvent.instance);
		post(editorId, PropertyChangeListener.class, l -> l.propertyChanged(transform, property, action));
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		transform.setScale(initial);
		EventService.post(editorId, SceneChangedEvent.instance);
		post(editorId, PropertyChangeListener.class, l -> l.propertyChanged(transform, property, initial));
		return Status.OK_STATUS;
	}
}
