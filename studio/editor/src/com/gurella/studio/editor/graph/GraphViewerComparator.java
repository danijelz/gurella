package com.gurella.studio.editor.graph;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;

class GraphViewerComparator extends ViewerComparator {
	@Override
	public int category(Object element) {
		if (element instanceof SceneNodeComponent) {
			return 0;
		} else if (element instanceof SceneNode) {
			return 1;
		} else {
			throw new IllegalArgumentException("Unsupported element:" + element);
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		int cat1 = category(e1);
		int cat2 = category(e2);

		if (cat1 != cat2) {
			return cat1 - cat2;
		} else if (e1 instanceof SceneNodeComponent) {
			return ((SceneNodeComponent) e1).getIndex() - ((SceneNodeComponent) e2).getIndex();
		} else if (e1 instanceof SceneNode) {
			return ((SceneNode) e1).getIndex() - ((SceneNode) e2).getIndex();
		} else {
			throw new IllegalArgumentException("Unsupported element:" + e1);
		}
	}
}
