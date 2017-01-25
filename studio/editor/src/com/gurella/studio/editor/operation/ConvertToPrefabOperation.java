package com.gurella.studio.editor.operation;

import static com.gurella.studio.gdx.GdxContext.post;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.gurella.engine.managedobject.Prefabs;
import com.gurella.engine.scene.SceneElement;
import com.gurella.studio.editor.utils.SceneChangedEvent;
import com.gurella.studio.gdx.GdxContext;

public class ConvertToPrefabOperation extends AbstractOperation {
	private final int editorId;
	private final SceneElement element;
	private final SceneElement prefab;
	private final String fileName;

	public ConvertToPrefabOperation(int editorId, SceneElement element, SceneElement prefab) {
		super("Convert to prefab");
		this.editorId = editorId;
		this.element = element;
		this.prefab = prefab;
		this.fileName = GdxContext.getFileName(editorId, prefab);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		Prefabs.convertToPrefab(element, prefab, fileName);
		GdxContext.clean(editorId);
		post(editorId, editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		Prefabs.dettachFromPrefab(element);
		GdxContext.clean(editorId);
		post(editorId, editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}
}
