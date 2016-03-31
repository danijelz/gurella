package com.gurella.studio.editor.scene;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.gurella.engine.scene.NodeContainer;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.studio.editor.GurellaEditor;
import com.gurella.studio.editor.scene.InspectorView.Inspectable;
import com.gurella.studio.editor.scene.InspectorView.PropertiesContainer;

public class SceneHierarchyView extends SceneEditorView {
	private Tree graph;
	private Menu menu;

	public SceneHierarchyView(GurellaEditor editor, int style) {
		super(editor, "Hierarchy", null, style);
		setLayout(new GridLayout());
		editor.getToolkit().adapt(this);
		graph = editor.getToolkit().createTree(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		graph.setHeaderVisible(true);
		graph.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		graph.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				TreeItem[] selection = graph.getSelection();
				if (selection.length > 0) {
					Object data = selection[0].getData();
					if (data instanceof SceneNode2) {
						postMessage(new SelectionMessage(new NodeInspectable((SceneNode2) data)));
					} else {
						postMessage(new SelectionMessage(new ComponentInspectable((SceneNodeComponent2) data)));
					}
				}
			}
		});

		menu = new Menu(graph);
		menu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent e) {
				TreeItem[] selection = graph.getSelection();
				boolean enabled = selection.length > 0 ? selection[0].getData() instanceof SceneNode2 : true;
				for(MenuItem item : menu.getItems()) {
					item.setEnabled(enabled);
				}
			}
		});
		MenuItem item = new MenuItem(menu, 0);
		item.setText("Add Node");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog dlg = new InputDialog(getDisplay().getActiveShell(), "Add Node", "Enter node name", "Node",
						new IInputValidator() {
							@Override
							public String isValid(String newText) {
								if (newText.length() < 3) {
									return "Too short";
								} else {
									return null;
								}
							}
						});

				if (dlg.open() == Window.OK) {
					TreeItem[] selection = graph.getSelection();
					if (selection.length == 0) {
						SceneNode2 node = getScene().newNode(dlg.getValue());
						TreeItem nodeItem = new TreeItem(graph, 0);
						nodeItem.setData(node);
						nodeItem.setText(node.getName());
					} else {
						TreeItem seectedItem = selection[0];
						Object data = seectedItem.getData();
						SceneNode2 node = (SceneNode2) data;
						SceneNode2 child = node.newChild(dlg.getValue());
						TreeItem nodeItem = new TreeItem(seectedItem, 0);
						nodeItem.setData(child);
						nodeItem.setText(child.getName());
					}
					setDirty();
				}
			}
		});
		item = new MenuItem(menu, 1);
		item.setText("Remove Node");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] selection = graph.getSelection();
				if (selection.length > 0) {
					TreeItem seectedItem = selection[0];
					SceneNode2 node = (SceneNode2) seectedItem.getData();
					SceneNode2 parentNode = node.getParentNode();
					if (parentNode == null) {
						getScene().removeNode(node);
					} else {
						parentNode.removeChild(node);
					}
					seectedItem.dispose();
					setDirty();
				}
			}
		});
		graph.setMenu(menu);
	}

	private Scene getScene() {
		return (Scene) graph.getData();
	}

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
			addComponents(nodeItem, node);
			addNodes(nodeItem, node);
		}
	}

	private static void addComponents(TreeItem parentItem, SceneNode2 node) {
		for (SceneNodeComponent2 component : node.components) {
			TreeItem nodeItem = new TreeItem(parentItem, 0);
			nodeItem.setText(component.getClass().getSimpleName());
			nodeItem.setData(component);
		}
	}

	@Override
	public void layout(boolean changed, boolean all) {
		super.layout(changed, all);
		graph.layout(true, true);
		System.out.println("layout");
	}

	@Override
	public void handleMessage(SceneEditorView source, Object message, Object... additionalData) {
		if (message instanceof NodeNameChangedMessage) {
			SceneNode2 node = ((NodeNameChangedMessage) message).node;
			for (TreeItem item : graph.getItems()) {
				TreeItem found = findItem(item, node);
				if (found != null) {
					found.setText(node.getName());
				}
			}
		}
	}

	private TreeItem findItem(TreeItem item, SceneNode2 node) {
		if (item.getData() == node) {
			return item;
		}

		for (TreeItem child : item.getItems()) {
			TreeItem found = findItem(child, node);
			if (found != null) {
				return found;
			}
		}

		return null;
	}

	private static class NodeInspectable implements Inspectable<SceneNode2> {
		SceneNode2 target;

		public NodeInspectable(SceneNode2 target) {
			this.target = target;
		}

		@Override
		public SceneNode2 getTarget() {
			return target;
		}

		@Override
		public PropertiesContainer<SceneNode2> createPropertiesContainer(InspectorView parent, SceneNode2 target) {
			return new NodePropertiesContainer(parent, target);
		}
	}

	private static class ComponentInspectable implements Inspectable<SceneNodeComponent2> {
		SceneNodeComponent2 target;

		public ComponentInspectable(SceneNodeComponent2 target) {
			this.target = target;
		}

		@Override
		public SceneNodeComponent2 getTarget() {
			return target;
		}

		@Override
		public PropertiesContainer<SceneNodeComponent2> createPropertiesContainer(InspectorView parent,
				SceneNodeComponent2 target) {
			return new ComponentPropertiesContainer(parent, target);
		}
	}
}
