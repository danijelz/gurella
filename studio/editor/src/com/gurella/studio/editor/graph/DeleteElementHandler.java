package com.gurella.studio.editor.graph;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class DeleteElementHandler extends AbstractHandler {
	private final SceneGraphView view;

	public DeleteElementHandler(SceneGraphView view) {
		this.view = view;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		view.delete();
		return null;
	}
}
