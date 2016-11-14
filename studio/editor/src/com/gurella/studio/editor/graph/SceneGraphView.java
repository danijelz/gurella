package com.gurella.studio.editor.graph;

import static org.eclipse.swt.SWT.PUSH;
import static org.eclipse.swt.SWT.SEPARATOR;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.base.model.Models;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.NodeContainer;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneElement2;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.audio.AudioListenerComponent;
import com.gurella.engine.scene.audio.AudioSourceComponent;
import com.gurella.engine.scene.bullet.rigidbody.BulletRigidBodyComponent;
import com.gurella.engine.scene.camera.OrtographicCameraComponent;
import com.gurella.engine.scene.camera.PerspectiveCameraComponent;
import com.gurella.engine.scene.light.DirectionalLightComponent;
import com.gurella.engine.scene.light.PointLightComponent;
import com.gurella.engine.scene.renderable.AtlasRegionComponent;
import com.gurella.engine.scene.renderable.ModelComponent;
import com.gurella.engine.scene.renderable.ShapeComponent;
import com.gurella.engine.scene.renderable.TextureComponent;
import com.gurella.engine.scene.renderable.TextureRegionComponent;
import com.gurella.engine.scene.renderable.shape.BoxShapeModel;
import com.gurella.engine.scene.renderable.shape.CapsuleShapeModel;
import com.gurella.engine.scene.renderable.shape.CompositeShapeModel;
import com.gurella.engine.scene.renderable.shape.ConeShapeModel;
import com.gurella.engine.scene.renderable.shape.CylinderShapeModel;
import com.gurella.engine.scene.renderable.shape.RectangleShapeModel;
import com.gurella.engine.scene.renderable.shape.ShapeModel;
import com.gurella.engine.scene.renderable.shape.SphereShapeModel;
import com.gurella.engine.scene.renderable.skybox.SkyboxComponent;
import com.gurella.engine.scene.tag.TagComponent;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.test.TestEditorComponent;
import com.gurella.engine.test.TestInputComponent;
import com.gurella.engine.test.TestPropertyEditorsComponent;
import com.gurella.engine.utils.Reflection;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditor;
import com.gurella.studio.editor.control.DockableView;
import com.gurella.studio.editor.event.SelectionEvent;
import com.gurella.studio.editor.inspector.component.ComponentInspectable;
import com.gurella.studio.editor.inspector.node.NodeInspectable;
import com.gurella.studio.editor.operation.AddComponentOperation;
import com.gurella.studio.editor.operation.AddNodeOperation;
import com.gurella.studio.editor.operation.RemoveComponentOperation;
import com.gurella.studio.editor.operation.RemoveNodeOperation;
import com.gurella.studio.editor.subscription.EditorSceneActivityListener;
import com.gurella.studio.editor.subscription.NodeNameChangeListener;
import com.gurella.studio.editor.subscription.SceneLoadedListener;

public class SceneGraphView extends DockableView
		implements EditorSceneActivityListener, NodeNameChangeListener, SceneLoadedListener {
	private static final Image image = GurellaStudioPlugin.getImage("icons/outline_co.png");

	private Tree graph;
	private Menu menu;

	public SceneGraphView(SceneEditor editor, int style) {
		super(editor, "Scene", image, style);

		addDisposeListener(e -> EventService.unsubscribe(editor.id, this));
		EventService.subscribe(editor.id, this);

		setLayout(new GridLayout());
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		graph = toolkit.createTree(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		graph.setHeaderVisible(false);
		graph.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		graph.addListener(SWT.Selection, e -> selectionChanged());
		graph.addListener(SWT.KeyUp, e -> handleKeyUp(e));

		createMenu();

		Scene scene = editorContext.getScene();
		if (scene != null) {
			sceneLoaded(scene);
		}
	}

	private void createMenu() {
		menu = new Menu(graph);
		menu.addListener(SWT.Show, e -> showMenu());

		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Add Node");
		item.addListener(SWT.Selection, e -> addNode(true));

		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Add Root Node");
		item.addListener(SWT.Selection, e -> addNode(false));

		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Remove Node");
		item.addListener(SWT.Selection, e -> removeSelectedElement());

		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Add sphere");
		item.addListener(SWT.Selection, e -> addShapeNode("Sphere", new SphereShapeModel()));

		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Add box");
		item.addListener(SWT.Selection, e -> addShapeNode("Box", new BoxShapeModel()));

		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Add cylinder");
		item.addListener(SWT.Selection, e -> addShapeNode("Cylinder", new CylinderShapeModel()));

		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Add cone");
		item.addListener(SWT.Selection, e -> addShapeNode("Cone", new ConeShapeModel()));

		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Add capsule");
		item.addListener(SWT.Selection, e -> addShapeNode("Capsule", new CapsuleShapeModel()));

		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Add rectangle");
		item.addListener(SWT.Selection, e -> addShapeNode("Rectangle", new RectangleShapeModel()));

		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Add composite");
		CompositeShapeModel shapeModel = new CompositeShapeModel();
		shapeModel.addShape(new BoxShapeModel());
		shapeModel.addShape(new CylinderShapeModel(), 0, 1, 0);
		CompositeShapeModel composite = new CompositeShapeModel();
		composite.addShape(new CylinderShapeModel(), 0, 1, 0);
		shapeModel.addShape(composite, 0, 1, 0);

		item.addListener(SWT.Selection, e -> addShapeNode("Composite", shapeModel));

		createComponentsSubMenu();
		graph.setMenu(menu);
	}

	@SuppressWarnings("unused")
	private void createComponentsSubMenu() {
		MenuItem subItem = new MenuItem(menu, SWT.CASCADE);
		subItem.setText("Add Component");
		Menu subMenu = new Menu(menu);
		subItem.setMenu(subMenu);
		addMenuItem(subMenu, TransformComponent.class);
		new MenuItem(subMenu, SEPARATOR);
		addMenuItem(subMenu, BulletRigidBodyComponent.class);
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
		addMenuItem(subMenu, TextureComponent.class);
		addMenuItem(subMenu, TextureRegionComponent.class);
		addMenuItem(subMenu, AtlasRegionComponent.class);
		addMenuItem(subMenu, SkyboxComponent.class);
		new MenuItem(subMenu, SEPARATOR);
		addMenuItem(subMenu, ModelComponent.class);
		addMenuItem(subMenu, ShapeComponent.class);
		new MenuItem(subMenu, SEPARATOR);
		addMenuItem(subMenu, TestPropertyEditorsComponent.class);
		addMenuItem(subMenu, TestEditorComponent.class);
		addMenuItem(subMenu, TestInputComponent.class);
		new MenuItem(subMenu, SEPARATOR);
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
			AddComponentOperation operation = new AddComponentOperation(getEditorId(), node, component);
			editorContext.executeOperation(operation, "Error while adding component");
		}
	}

	protected int getEditorId() {
		return editorContext.editorId;
	}

	private void selectionChanged() {
		TreeItem[] selection = graph.getSelection();
		if (selection.length > 0) {
			Object data = selection[0].getData();
			if (data instanceof SceneNode2) {
				SceneNode2 vode = (SceneNode2) data;
				EventService.post(getEditorId(), new SelectionEvent(new NodeInspectable(vode)));
			} else {
				SceneNodeComponent2 component = (SceneNodeComponent2) data;
				EventService.post(getEditorId(), new SelectionEvent(new ComponentInspectable(component)));
			}
		}
	}

	private void handleKeyUp(Event e) {
		if (e.keyCode == SWT.DEL) {
			removeSelectedElement();
		}
	}

	private Scene getScene() {
		return (Scene) graph.getData();
	}

	private void addNodes(TreeItem parentItem, NodeContainer nodeContainer) {
		for (SceneNode2 node : nodeContainer.getNodes()) {
			addNode(parentItem, node);
		}
	}

	protected TreeItem addNode(TreeItem parentItem, SceneNode2 node) {
		TreeItem nodeItem = parentItem == null ? new TreeItem(graph, 0) : new TreeItem(parentItem, 0);
		nodeItem.setText(node.getName());
		nodeItem.setImage(GurellaStudioPlugin.createImage("icons/ice_cube.png"));
		nodeItem.setData(node);
		addComponents(nodeItem, node);
		addNodes(nodeItem, node);
		return nodeItem;
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

	private TreeItem findItem(SceneElement2 element) {
		for (TreeItem item : graph.getItems()) {
			TreeItem found = findItem(item, element);
			if (found != null) {
				return found;
			}
		}

		return null;
	}

	private TreeItem findItem(TreeItem item, SceneElement2 element) {
		if (item.getData() == element) {
			return item;
		}

		for (TreeItem child : item.getItems()) {
			TreeItem found = findItem(child, element);
			if (found != null) {
				return found;
			}
		}

		return null;
	}

	public void showMenu() {
		TreeItem[] selection = graph.getSelection();
		boolean enabled = selection.length > 0 ? selection[0].getData() instanceof SceneNode2 : true;
		for (MenuItem item : menu.getItems()) {
			item.setEnabled(enabled);
		}
	}

	private void addShapeNode(String name, ShapeModel shapeModel) {
		SceneNode2 node = getScene().newNode(name);
		node.newComponent(TransformComponent.class);
		ShapeComponent shapeComponent = node.newComponent(ShapeComponent.class);
		shapeComponent.setShape(shapeModel);
		AddNodeOperation operation = new AddNodeOperation(getEditorId(), getScene(), null, node);
		editorContext.executeOperation(operation, "Error while adding node");
	}

	private void removeSelectedElement() {
		TreeItem[] selection = graph.getSelection();
		if (selection.length > 0) {
			TreeItem selectedItem = selection[0];
			Object data = selectedItem.getData();

			if (data instanceof SceneNode2) {
				SceneNode2 node = (SceneNode2) data;
				SceneNode2 parentNode = node.getParentNode();
				RemoveNodeOperation operation = new RemoveNodeOperation(getEditorId(), getScene(), parentNode, node);
				editorContext.executeOperation(operation, "Error while removing node");
			} else if (data instanceof SceneNodeComponent2) {
				SceneNodeComponent2 component = (SceneNodeComponent2) data;
				SceneNode2 node = component.getNode();
				RemoveComponentOperation operation = new RemoveComponentOperation(getEditorId(), node, component);
				editorContext.executeOperation(operation, "Error while removing component");
			}
		}
	}

	private void addNode(boolean child) {
		InputDialog dlg = new InputDialog(getDisplay().getActiveShell(), "Add Node", "Enter node name", "Node",
				newText -> newText.length() < 3 ? "Too short" : null);

		if (dlg.open() != Window.OK) {
			return;
		}

		SceneNode2 node = new SceneNode2();
		node.setName(dlg.getValue());
		node.newComponent(TransformComponent.class);

		SceneNode2 parentNode = null;
		if (child) {
			TreeItem[] selection = graph.getSelection();
			if (selection.length > 0) {
				TreeItem seectedItem = selection[0];
				parentNode = (SceneNode2) seectedItem.getData();
			}
		}

		AddNodeOperation operation = new AddNodeOperation(getEditorId(), getScene(), parentNode, node);
		editorContext.executeOperation(operation, "Error while adding node");
	}

	@Override
	public void nodeAdded(Scene scene, SceneNode2 parentNode, SceneNode2 node) {
		if (getScene() != scene) {
			return;
		}

		if (parentNode == null) {
			TreeItem nodeItem = addNode(null, node);
			graph.select(nodeItem);
			EventService.post(getEditorId(), new SelectionEvent(new NodeInspectable(node)));
		} else {
			TreeItem parentItem = findItem(parentNode);
			TreeItem nodeItem = addNode(parentItem, node);
			parentItem.setExpanded(true);
			graph.select(nodeItem);
			EventService.post(getEditorId(), new SelectionEvent(new NodeInspectable(node)));
		}
	}

	@Override
	public void nodeRemoved(Scene scene, SceneNode2 parentNode, SceneNode2 node) {
		TreeItem found = findItem(node);
		if (found != null) {
			found.dispose();
		}
	}

	@Override
	public void componentAdded(SceneNode2 node, SceneNodeComponent2 component) {
		TreeItem found = findItem(node);
		if (found != null) {
			createComponentItem(found, component);
		}
	}

	@Override
	public void componentRemoved(SceneNode2 node, SceneNodeComponent2 component) {
		TreeItem found = findItem(component);
		if (found != null) {
			found.dispose();
		}
	}

	@Override
	public void nodeNameChanged(SceneNode2 node) {
		TreeItem found = findItem(node);
		if (found != null) {
			found.setText(node.getName());
		}
	}

	@Override
	public void sceneLoaded(Scene scene) {
		graph.setData(scene);
		menu.setEnabled(true);
		int editorId = getEditorId();
		addDisposeListener(e -> EventService.unsubscribe(editorId, this));
		EventService.subscribe(editorId, this);
		addNodes(null, scene);
	}
}
