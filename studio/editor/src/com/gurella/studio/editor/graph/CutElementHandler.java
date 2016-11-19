package com.gurella.studio.editor.graph;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.Transfer;

import com.gurella.engine.scene.SceneElement2;

class CutElementHandler extends AbstractHandler {
	private final SceneGraphView view;

	public CutElementHandler(SceneGraphView view) {
		this.view = view;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Optional<SceneElement2> selected = view.getFirstSelectedElement();
		if (selected.isPresent()) {
			LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
			SceneElement2 element = selected.get();
			ISelection selection = new CutElementSelection(element);
			transfer.setSelection(selection);
			view.clipboard.setContents(new Object[] { selection }, new Transfer[] { transfer });
		}
		return null;
	}
}