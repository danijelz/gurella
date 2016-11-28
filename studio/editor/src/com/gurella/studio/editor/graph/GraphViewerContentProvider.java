package com.gurella.studio.editor.graph;

import org.eclipse.jface.viewers.ITreeContentProvider;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneElement;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;

class GraphViewerContentProvider implements ITreeContentProvider {
	private static final Object[] emptyElements = new Object[0];

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Scene) {
			Scene scene = (Scene) inputElement;
			Array<SceneElement> elements = new Array<>();
			scene.nodes.appendTo(elements);
			return elements.toArray(SceneNode.class);
		} else {
			return emptyElements;
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof SceneNode) {
			Array<SceneElement> elements = new Array<>();
			SceneNode node = (SceneNode) parentElement;
			node.components.appendTo(elements);
			node.childNodes.appendTo(elements);
			return elements.toArray(SceneElement.class);
		} else {
			return emptyElements;
		}
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof SceneNode) {
			return ((SceneNode) element).getParentNode();
		} else if (element instanceof SceneNodeComponent) {
			return ((SceneNodeComponent) element).getNode();
		} else {
			return null;
		}
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof SceneNode) {
			SceneNode node = (SceneNode) element;
			return node.components.size() > 0 || node.childNodes.size() > 0;
		} else {
			return false;
		}
	}
}
