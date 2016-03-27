package com.gurella.studio.editor.scene;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.gurella.engine.scene.NodeContainer;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;

public class SceneGraphComponent extends SceneEditorView {
	private Tree graph;

	public SceneGraphComponent(SceneEditorMainContainer mainContainer, int style) {
		super(mainContainer, "Hierarchy", null, style);
		setLayout(new GridLayout());
		graph = new Tree(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		graph.setHeaderVisible(true);
		graph.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	@Override
	public void present(Scene scene) {
		graph.removeAll();
		if (scene != null) {
			addNodes(null, scene);
		}
	}

	private void addNodes(TreeItem parentItem, NodeContainer nodeContainer) {
		for (SceneNode2 node : nodeContainer.getNodes()) {
			TreeItem item = parentItem == null ? new TreeItem(graph, 0) : new TreeItem(parentItem, 0);
			item.setText(node.getName());
			addNodes(item, node);
		}
	}
}
