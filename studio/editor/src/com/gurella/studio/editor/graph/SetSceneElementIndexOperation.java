package com.gurella.studio.editor.graph;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.SceneElement2;
import com.gurella.studio.editor.event.SceneChangedEvent;

public class SetSceneElementIndexOperation extends AbstractOperation {
	final int editorId;
	final SceneElement2 element;
	final int oldIndex;
	final int newIndex;

	TreeItem item;

	public SetSceneElementIndexOperation(int editorId, SceneElement2 element, int oldIndex, int newIndex,
			TreeItem item) {
		super("Set index");
		this.editorId = editorId;
		this.element = element;
		this.oldIndex = oldIndex;
		this.newIndex = newIndex;
		this.item = item;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		setIndex(newIndex);
		EventService.post(editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		setIndex(oldIndex < newIndex ? oldIndex : oldIndex + 1);
		EventService.post(editorId, SceneChangedEvent.instance);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		return execute(monitor, adaptable);
	}

	private void setIndex(int index) {
		element.setIndex(index);
		Tree tree = item.getParent();
		TreeItem parentItem = item.getParentItem();
		TreeItem newItem = parentItem == null ? new TreeItem(tree, SWT.NONE, index)
				: new TreeItem(parentItem, SWT.NONE, index);
		newItem.setImage(item.getImage());
		newItem.setText(item.getText());
		newItem.setData(item.getData());
		item.dispose();
		item = newItem;
		tree.redraw();
		tree.layout(true, true);
	}
}
