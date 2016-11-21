package com.gurella.studio.editor.graph;

import static com.gurella.engine.utils.Values.cast;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.swt.dnd.Clipboard;

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
	private final int editorId;
	private final SceneEditorContext context;
	private final Clipboard clipboard;

	public PasteElementHandler(SceneGraphView view) {
		this.view = view;
		this.editorId = view.editorId;
		this.context = view.editorContext;
		this.clipboard = view.clipboard;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		view.getFirstSelectedNode().ifPresent(this::paste);
		return null;
	}

	private void paste(SceneNode2 destination) {
		Object contents = clipboard.getContents(LocalSelectionTransfer.getTransfer());
		if (contents instanceof CutElementSelection) {
			moveElement(((CutElementSelection) contents).getElement(), destination);
		} else if (contents instanceof CopyElementSelection) {
			copyElement(((CopyElementSelection) contents).getElement(), destination);
		}
	}

	private void moveElement(SceneElement2 source, SceneNode2 destination) {
		if (source == destination) {
			return;
		}

		if (source instanceof SceneNodeComponent2) {
			SceneNodeComponent2 component = (SceneNodeComponent2) source;
			if (destination.hasComponent(component.getClass(), true)) {
				return;
			}

			String errorMsg = "Error while repositioning element";
			int newIndex = destination.components.size();
			context.executeOperation(new ReparentComponentOperation(editorId, component, destination, newIndex),
					errorMsg);
		} else {
			if (source.getParent() == destination) {
				return;
			}

			SceneNode2 node = (SceneNode2) source;
			String errorMsg = "Error while repositioning node";
			int newIndex = destination.childNodes.size();
			context.executeOperation(new ReparentNodeOperation(editorId, node, destination, newIndex), errorMsg);
		}

		clipboard.clearContents();
	}

	private void copyElement(SceneElement2 source, SceneNode2 destination) {
		if (source instanceof SceneNodeComponent2 && destination.getComponent(cast(source.getClass()), true) != null) {
			return;
		}

		SceneElement2 copy = new CopyContext().copy(source);
		String errorMsg = "Error while copying element";
		context.executeOperation(new CopyElementOperation(editorId, destination, context.getScene(), copy), errorMsg);
	}
}