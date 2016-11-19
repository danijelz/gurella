package com.gurella.studio.editor.graph;

import static com.gurella.engine.utils.Values.cast;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.util.LocalSelectionTransfer;

import com.gurella.engine.base.model.CopyContext;
import com.gurella.engine.scene.SceneElement2;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.operation.CopyElementOperation;
import com.gurella.studio.editor.operation.ReparentComponentOperation;
import com.gurella.studio.editor.operation.ReparentNodeOperation;

class PasteElementHandler extends AbstractHandler {
	private final SceneGraphView view;

	public PasteElementHandler(SceneGraphView view) {
		this.view = view;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Optional<SceneNode2> selected = view.getFirstSelectedElement().filter(e -> e instanceof SceneNode2)
				.map(e -> (SceneNode2) e);
		if (!selected.isPresent()) {
			return null;
		}

		SceneNode2 destination = selected.get();
		Object contents = view.clipboard.getContents(LocalSelectionTransfer.getTransfer());
		if (contents instanceof CutElementSelection) {
			moveElement(((CutElementSelection) contents).getElement(), destination);
		} else if (contents instanceof CopyElementSelection) {
			copyElement(((CopyElementSelection) contents).getElement(), destination);
		}

		return null;
	}

	private void moveElement(SceneElement2 source, SceneNode2 destination) {
		if (source == destination) {
			return;
		}

		if (source instanceof SceneNodeComponent2) {
			SceneNodeComponent2 component = (SceneNodeComponent2) source;
			if (destination.getComponent(component.getClass(), true) != null) {
				return;
			}

			int editorId = view.editorId;
			SceneEditorContext context = view.editorContext;
			String errorMsg = "Error while repositioning element";
			int newIndex = destination.components.size();
			context.executeOperation(new ReparentComponentOperation(editorId, component, destination, newIndex),
					errorMsg);
		} else {
			if (source.getParent() == destination) {
				return;
			}

			SceneNode2 node = (SceneNode2) source;
			int editorId = view.editorId;
			SceneEditorContext context = view.editorContext;
			String errorMsg = "Error while repositioning node";
			int newIndex = destination.childNodes.size();
			context.executeOperation(new ReparentNodeOperation(editorId, node, destination, newIndex), errorMsg);
		}

		view.clipboard.clearContents();
	}

	private void copyElement(SceneElement2 source, SceneNode2 destination) {
		if (source instanceof SceneNodeComponent2 && destination.getComponent(cast(source.getClass()), true) != null) {
			return;
		}

		SceneElement2 copy = new CopyContext().copy(source);
		int editorId = view.editorId;
		SceneEditorContext context = view.editorContext;
		String errorMsg = "Error while copying element";
		context.executeOperation(new CopyElementOperation(editorId, copy, destination, context.getScene()), errorMsg);
	}
}