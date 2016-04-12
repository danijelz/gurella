package com.gurella.studio.editor.scene;

import static org.eclipse.swt.SWT.PUSH;
import static org.eclipse.swt.SWT.SEPARATOR;

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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.base.model.Models;
import com.gurella.engine.scene.NodeContainer;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.audio.AudioListenerComponent;
import com.gurella.engine.scene.audio.AudioSourceComponent;
import com.gurella.engine.scene.bullet.BulletPhysicsRigidBodyComponent;
import com.gurella.engine.scene.camera.OrtographicCameraComponent;
import com.gurella.engine.scene.camera.PerspectiveCameraComponent;
import com.gurella.engine.scene.light.DirectionalLightComponent;
import com.gurella.engine.scene.light.PointLightComponent;
import com.gurella.engine.scene.movement.TransformComponent;
import com.gurella.engine.scene.renderable.AtlasRegionComponent;
import com.gurella.engine.scene.renderable.ModelComponent;
import com.gurella.engine.scene.renderable.SolidComponent;
import com.gurella.engine.scene.renderable.TextureComponent;
import com.gurella.engine.scene.renderable.TextureRegionComponent;
import com.gurella.engine.scene.tag.TagComponent;
import com.gurella.engine.test.TestInputComponent;
import com.gurella.engine.test.TestPropertyEditorsComponnent;
import com.gurella.engine.utils.Reflection;
import com.gurella.studio.editor.GurellaEditor;
import com.gurella.studio.editor.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneChangedMessage;
import com.gurella.studio.editor.SceneLoadedMessage;
import com.gurella.studio.editor.inspector.ComponentInspectableContainer;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.inspector.InspectorView.Inspectable;
import com.gurella.studio.editor.inspector.NodeInspectableContainer;

public class SceneHierarchyView extends SceneEditorView {
	private Tree graph;
	private Menu menu;

	public SceneHierarchyView(GurellaEditor editor, int style) {
		super(editor, "Scene", GurellaStudioPlugin.createImage("icons/outline_co.png"), style);
		setLayout(new GridLayout());
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		graph = toolkit.createTree(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		graph.setHeaderVisible(false);
		graph.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		graph.addListener(SWT.Selection, (e) -> graphSelectionChanged());
		graph.addListener(SWT.KeyUp, e -> handleKeyUp(e));

		menu = new Menu(graph);
		menu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent e) {
				TreeItem[] selection = graph.getSelection();
				boolean enabled = selection.length > 0 ? selection[0].getData() instanceof SceneNode2 : true;
				for (MenuItem item : menu.getItems()) {
					item.setEnabled(enabled);
				}
			}
		});

		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Add Node");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog dlg = new InputDialog(getDisplay().getActiveShell(), "Add Node", "Enter node name", "Node",
						(newText) -> newText.length() < 3 ? "Too short" : null);

				if (dlg.open() == Window.OK) {
					TreeItem[] selection = graph.getSelection();
					if (selection.length > 0) {
						TreeItem seectedItem = selection[0];
						Object data = seectedItem.getData();
						SceneNode2 node = (SceneNode2) data;
						SceneNode2 child = node.newChild(dlg.getValue());
						TreeItem nodeItem = new TreeItem(seectedItem, 0);
						nodeItem.setData(child);
						nodeItem.setText(child.getName());
						nodeItem.setImage(GurellaStudioPlugin.createImage("icons/ice_cube.png"));
					}
					postMessage(SceneChangedMessage.instance);
				}
			}
		});
		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Add Root Node");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog dlg = new InputDialog(getDisplay().getActiveShell(), "Add Node", "Enter node name", "Node",
						(newText) -> newText.length() < 3 ? "Too short" : null);

				if (dlg.open() == Window.OK) {
					SceneNode2 node = getScene().newNode(dlg.getValue());
					TreeItem nodeItem = new TreeItem(graph, 0);
					nodeItem.setData(node);
					nodeItem.setText(node.getName());
					nodeItem.setImage(GurellaStudioPlugin.createImage("icons/ice_cube.png"));
					postMessage(SceneChangedMessage.instance);
				}
			}
		});
		item = new MenuItem(menu, SWT.PUSH);
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
					postMessage(SceneChangedMessage.instance);
				}
			}
		});

		MenuItem subItem = new MenuItem(menu, SWT.CASCADE);
		subItem.setText("Add Component");
		Menu subMenu = new Menu(menu);
		subItem.setMenu(subMenu);
		addMenuItem(subMenu, TransformComponent.class);
		new MenuItem(subMenu, SEPARATOR);
		addMenuItem(subMenu, BulletPhysicsRigidBodyComponent.class);
		new MenuItem(subMenu, SEPARATOR);
		addMenuItem(subMenu, OrtographicCameraComponent.class);
		addMenuItem(subMenu, PerspectiveCameraComponent.class);
		new MenuItem(subMenu, SEPARATOR);
		addMenuItem(subMenu, PointLightComponent.class);
		addMenuItem(subMenu, DirectionalLightComponent.class);
		new MenuItem(subMenu, SEPARATOR);
		addMenuItem(subMenu, AudioListenerComponent.class);
		addMenuItem(subMenu, AudioSourceComponent.class);
		new MenuItem(subMenu, SEPARATOR);
		addMenuItem(subMenu, TagComponent.class);
		new MenuItem(subMenu, SEPARATOR);
		// addItem("Layer", LayerComponent.class);
		addMenuItem(subMenu, TextureComponent.class);
		addMenuItem(subMenu, TextureRegionComponent.class);
		addMenuItem(subMenu, AtlasRegionComponent.class);
		new MenuItem(subMenu, SEPARATOR);
		addMenuItem(subMenu, ModelComponent.class);
		addMenuItem(subMenu, SolidComponent.class);
		new MenuItem(subMenu, SEPARATOR);
		addMenuItem(subMenu, TestPropertyEditorsComponnent.class);
		addMenuItem(subMenu, TestInputComponent.class);
		new MenuItem(subMenu, SEPARATOR);

		graph.setMenu(menu);

		Scene scene = editor.getScene();
		if (scene != null) {
			present(scene);
		}
	}

	private void addMenuItem(Menu menu, final Class<? extends SceneNodeComponent2> componentType) {
		MenuItem item1 = new MenuItem(menu, PUSH);
		item1.setText(Models.getModel(componentType).getName());
		item1.addListener(SWT.Selection, (e) -> addComponent(Reflection.newInstance(componentType)));
	}

	private void addComponent(SceneNodeComponent2 component) {
		TreeItem[] selection = graph.getSelection();
		if (selection.length > 0) {
			TreeItem seectedItem = selection[0];
			SceneNode2 node = (SceneNode2) seectedItem.getData();
			node.addComponent(component);
			postMessage(new ComponentAddedMessage(component));
		}
	}

	private void graphSelectionChanged() {
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

	private void handleKeyUp(Event e) {
		if (e.keyCode == SWT.DEL) {
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
				postMessage(SceneChangedMessage.instance);
			}
		}
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
			nodeItem.setImage(GurellaStudioPlugin.createImage("icons/ice_cube.png"));
			nodeItem.setData(node);
			addComponents(nodeItem, node);
			addNodes(nodeItem, node);
		}
	}

	private static void addComponents(TreeItem parentItem, SceneNode2 node) {
		for (SceneNodeComponent2 component : node.components) {
			createComponentItem(parentItem, component);
		}
	}

	private static void createComponentItem(TreeItem parentItem, SceneNodeComponent2 component) {
		TreeItem componentItem = new TreeItem(parentItem, 0);
		if (component instanceof TransformComponent) {
			componentItem.setImage(GurellaStudioPlugin.createImage("icons/transform.png"));
		} else {
			componentItem.setImage(GurellaStudioPlugin.createImage("icons/16-cube-green_16x16.png"));
		}
		componentItem.setText(Models.getModel(component).getName());
		componentItem.setData(component);
	}

	@Override
	public void layout(boolean changed, boolean all) {
		super.layout(changed, all);
		graph.layout(true, true);
		System.out.println("layout");
	}

	@Override
	public void handleMessage(Object source, Object message) {
		if (message instanceof NodeNameChangedMessage) {
			SceneNode2 node = ((NodeNameChangedMessage) message).node;
			for (TreeItem item : graph.getItems()) {
				TreeItem found = findItem(item, node);
				if (found != null) {
					found.setText(node.getName());
				}
			}
		} else if (message instanceof ComponentAddedMessage) {
			ComponentAddedMessage componentAddedMessage = (ComponentAddedMessage) message;
			SceneNodeComponent2 component = componentAddedMessage.component;
			SceneNode2 node = component.getNode();
			for (TreeItem item : graph.getItems()) {
				TreeItem found = findItem(item, node);
				if (found != null) {
					createComponentItem(found, component);
				}
			}
		} else if (message instanceof SceneLoadedMessage) {
			present(((SceneLoadedMessage) message).scene);
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
		public InspectableContainer<SceneNode2> createContainer(InspectorView parent, SceneNode2 target) {
			return new NodeInspectableContainer(parent, target);
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
		public InspectableContainer<SceneNodeComponent2> createContainer(InspectorView parent,
				SceneNodeComponent2 target) {
			return new ComponentInspectableContainer(parent, target);
		}
	}
}
