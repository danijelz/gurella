package com.gurella.studio.editor.graph;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.utils.Values;

class GraphViewerFilter extends ViewerFilter {
	String filter;

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (Values.isBlank(filter)) {
			return true;
		}

		SceneNode2 node = element instanceof SceneNodeComponent2 ? (SceneNode2) parentElement : (SceneNode2) element;
		String name = node.getName();
		return Values.isNotBlank(name) && name.contains(filter);
	}
}
