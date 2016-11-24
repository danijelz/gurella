package com.gurella.studio.editor.graph;

import org.eclipse.jface.viewers.ITreeContentProvider;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneElement2;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;

class GraphViewerContentProvider implements ITreeContentProvider {
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Scene) {
			Scene scene = (Scene) inputElement;
			Array<SceneElement2> elements = new Array<>();
			scene.nodes.appendTo(elements);
			return elements.toArray(SceneNode2.class);
		} else {
			return new Object[0];
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof SceneNode2) {
			Array<SceneElement2> elements = new Array<>();
			SceneNode2 node = (SceneNode2) parentElement;
			node.components.appendTo(elements);
			node.childNodes.appendTo(elements);
			return elements.toArray(SceneElement2.class);
		} else {
			return new Object[0];
		}
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof SceneNode2) {
			return ((SceneNode2) element).getParentNode();
		} else if (element instanceof SceneNodeComponent2) {
			return ((SceneNodeComponent2) element).getNode();
		} else {
			return null;
		}
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof SceneNode2) {
			SceneNode2 node = (SceneNode2) element;
			return node.components.size() > 0 || node.childNodes.size() > 0;
		} else {
			return false;
		}
	}
}
