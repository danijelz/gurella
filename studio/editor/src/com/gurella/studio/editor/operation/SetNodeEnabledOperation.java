package com.gurella.studio.editor.operation;

import static com.gurella.studio.gdx.GdxContext.post;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.gurella.engine.scene.SceneNode;
import com.gurella.studio.editor.subscription.NodeEnabledChangeListener;
import com.gurella.studio.editor.utils.SceneChangedEvent;

public class SetNodeEnabledOperation extends AbstractOperation {
	final int editorId;
	final SceneNode node;
	final boolean oldValue;
	final boolean newValue;

	public SetNodeEnabledOperation(int editorId, SceneNode node, boolean oldValue, boolean newValue) {
		super("Enabled");
		this.editorId = editorId;
		this.node = node;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		node.setEnabled(newValue);
		notifyNodeEnabledChanged();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		node.setEnabled(oldValue);
		notifyNodeEnabledChanged();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		return execute(monitor, adaptable);
	}

	private void notifyNodeEnabledChanged() {
		post(editorId, editorId, NodeEnabledChangeListener.class, l -> l.nodeEnabledChanged(node));
		post(editorId, editorId, SceneChangedEvent.instance);
	}
}