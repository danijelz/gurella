package com.gurella.studio.editor.graph;

import static org.eclipse.swt.SWT.POP_UP;
import static org.eclipse.swt.SWT.PUSH;
import static org.eclipse.swt.SWT.SEPARATOR;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.gurella.engine.base.model.Models;
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
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.operation.AddComponentOperation;
import com.gurella.studio.editor.operation.AddNodeOperation;
import com.gurella.studio.editor.operation.RemoveComponentOperation;
import com.gurella.studio.editor.operation.RemoveNodeOperation;

class SceneGraphPopupMenu {
	private final Tree graph;
	private final SceneEditorContext context;
	private final int editorId;

	public SceneGraphPopupMenu(SceneGraphView view) {
		graph = view.graph;
		context = view.editorContext;
		editorId = context.editorId;
	}

	void show(SceneElement2 selection) {
		Menu menu = createMenu(selection);
		menu.setLocation(graph.getDisplay().getCursorLocation());
		menu.setVisible(true);
	}

	private Menu createMenu(SceneElement2 selection) {
		Menu menu = new Menu(graph.getShell(), POP_UP);

		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Add Node");
		item.addListener(SWT.Selection, e -> addNode(true));

		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Add Root Node");
		item.addListener(SWT.Selection, e -> addNode(false));

		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Delete");
		item.addListener(SWT.Selection, e -> removeSelectedElement(selection));

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

		createComponentsSubMenu(menu);

		return menu;
	}

	private void createComponentsSubMenu(Menu menu) {
		MenuItem subItem = new MenuItem(menu, SWT.CASCADE);
		subItem.setText("Add Component");
		Menu subMenu = new Menu(menu);
		subItem.setMenu(subMenu);
		addMenuItem(subMenu, TransformComponent.class);
		addSeparator(subMenu);
		addMenuItem(subMenu, BulletRigidBodyComponent.class);
		addSeparator(subMenu);
		addMenuItem(subMenu, OrtographicCameraComponent.class);
		addMenuItem(subMenu, PerspectiveCameraComponent.class);
		addSeparator(subMenu);
		addMenuItem(subMenu, PointLightComponent.class);
		addMenuItem(subMenu, DirectionalLightComponent.class);
		addSeparator(subMenu);
		addMenuItem(subMenu, AudioListenerComponent.class);
		addMenuItem(subMenu, AudioSourceComponent.class);
		addSeparator(subMenu);
		addMenuItem(subMenu, TagComponent.class);
		addSeparator(subMenu);
		addMenuItem(subMenu, TextureComponent.class);
		addMenuItem(subMenu, TextureRegionComponent.class);
		addMenuItem(subMenu, AtlasRegionComponent.class);
		addMenuItem(subMenu, SkyboxComponent.class);
		addSeparator(subMenu);
		addMenuItem(subMenu, ModelComponent.class);
		addMenuItem(subMenu, ShapeComponent.class);
		addSeparator(subMenu);
		addMenuItem(subMenu, TestPropertyEditorsComponent.class);
		addMenuItem(subMenu, TestEditorComponent.class);
		addMenuItem(subMenu, TestInputComponent.class);
		addSeparator(subMenu);
	}

	private static MenuItem addSeparator(Menu menu) {
		return new MenuItem(menu, SEPARATOR);
	}

	private void addMenuItem(Menu menu, final Class<? extends SceneNodeComponent2> componentType) {
		MenuItem item1 = new MenuItem(menu, PUSH);
		item1.setText(Models.getModel(componentType).getName());
		item1.addListener(SWT.Selection, (e) -> addComponent(Reflection.newInstance(componentType)));
	}

	private void addShapeNode(String name, ShapeModel shapeModel) {
		Scene scene = context.getScene();
		SceneNode2 node = scene.newNode(name);
		node.newComponent(TransformComponent.class);
		ShapeComponent shapeComponent = node.newComponent(ShapeComponent.class);
		shapeComponent.setShape(shapeModel);
		AddNodeOperation operation = new AddNodeOperation(editorId, scene, null, node);
		context.executeOperation(operation, "Error while adding node");
	}

	private void addComponent(SceneNodeComponent2 component) {
		TreeItem[] selection = graph.getSelection();
		if (selection.length > 0) {
			TreeItem seectedItem = selection[0];
			SceneNode2 node = (SceneNode2) seectedItem.getData();
			AddComponentOperation operation = new AddComponentOperation(editorId, node, component);
			context.executeOperation(operation, "Error while adding component");
		}
	}

	private void removeSelectedElement(SceneElement2 selection) {
		if (selection instanceof SceneNode2) {
			SceneNode2 node = (SceneNode2) selection;
			SceneNode2 parentNode = node.getParentNode();
			RemoveNodeOperation operation = new RemoveNodeOperation(editorId, context.getScene(), parentNode, node);
			context.executeOperation(operation, "Error while removing node");
		} else if (selection instanceof SceneNodeComponent2) {
			SceneNodeComponent2 component = (SceneNodeComponent2) selection;
			SceneNode2 node = component.getNode();
			RemoveComponentOperation operation = new RemoveComponentOperation(editorId, node, component);
			context.executeOperation(operation, "Error while removing component");
		}
	}

	private void addNode(boolean child) {
		InputDialog dlg = new InputDialog(graph.getShell(), "Add Node", "Enter node name", "Node",
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

		AddNodeOperation operation = new AddNodeOperation(editorId, context.getScene(), parentNode, node);
		context.executeOperation(operation, "Error while adding node");
	}
}
