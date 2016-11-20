package com.gurella.studio.editor.graph;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.gurella.engine.scene.SceneElement2;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.operation.RemoveComponentOperation;
import com.gurella.studio.editor.operation.RemoveNodeOperation;

public class DeleteElementHandler extends AbstractHandler {
	private final SceneGraphView view;
	private final int editorId;
	private final SceneEditorContext context;

	public DeleteElementHandler(SceneGraphView view) {
		this.view = view;
		this.editorId = view.editorId;
		this.context = view.editorContext;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		view.getFirstSelectedElement().ifPresent(this::removeElement);
		return null;
	}

	private void removeElement(SceneElement2 element) {
		if (element instanceof SceneNode2) {
			SceneNode2 node = (SceneNode2) element;
			SceneNode2 parentNode = node.getParentNode();
			RemoveNodeOperation operation = new RemoveNodeOperation(editorId, element.getScene(), parentNode, node);
			context.executeOperation(operation, "Error while removing node");
		} else if (element instanceof SceneNodeComponent2) {
			SceneNodeComponent2 component = (SceneNodeComponent2) element;
			SceneNode2 node = component.getNode();
			RemoveComponentOperation operation = new RemoveComponentOperation(editorId, node, component);
			context.executeOperation(operation, "Error while removing component");
		}
	}
}
