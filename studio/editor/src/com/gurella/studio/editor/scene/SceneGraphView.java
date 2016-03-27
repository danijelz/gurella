package com.gurella.studio.editor.scene;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.gurella.engine.scene.NodeContainer;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;

public class SceneGraphView extends SceneEditorView {
	private Tree graph;
	private Menu menu;

	public SceneGraphView(SceneEditorMainContainer mainContainer, int style) {
		super(mainContainer, "Hierarchy", null, style);
		setLayout(new GridLayout());
		graph = new Tree(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		graph.setHeaderVisible(true);
		graph.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		menu = new Menu(graph);
		MenuItem item = new MenuItem(menu, 0);
		item.setText("Add Node");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] selection = graph.getSelection();
				if (selection.length == 0) {
					SceneNode2 node = getScene().newNode("Node");
					TreeItem nodeItem = new TreeItem(graph, 0);
					nodeItem.setData(node);
					nodeItem.setText(node.getName());
				} else {
					TreeItem seectedItem = selection[0];
					SceneNode2 node = (SceneNode2) seectedItem.getData();
					SceneNode2 child = node.newChild("Node");
					TreeItem nodeItem = new TreeItem(seectedItem, 0);
					nodeItem.setData(child);
					nodeItem.setText(child.getName());
				}
			}
		});
		graph.setMenu(menu);
	}

	private Scene getScene() {
		return (Scene) graph.getData();
	}

	@Override
	public void present(Scene scene) {
		graph.removeAll();
		graph.setData(scene);
		menu.setEnabled(scene != null);
		if (scene != null) {
			addNodes(null, scene);
		}
	}

	private void addNodes(TreeItem parentItem, NodeContainer nodeContainer) {
		for (SceneNode2 node : nodeContainer.getNodes()) {
			TreeItem nodeItem = parentItem == null ? new TreeItem(graph, 0) : new TreeItem(parentItem, 0);
			nodeItem.setText(node.getName());
			nodeItem.setData(node);
			addNodes(nodeItem, node);
		}
	}
}
