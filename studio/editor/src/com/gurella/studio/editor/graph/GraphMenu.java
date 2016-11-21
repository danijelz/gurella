package com.gurella.studio.editor.graph;

import static com.gurella.engine.asset.AssetType.prefab;
import static java.util.stream.Collectors.joining;
import static org.eclipse.swt.SWT.POP_UP;
import static org.eclipse.swt.SWT.PUSH;
import static org.eclipse.swt.SWT.SEPARATOR;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.serialization.json.JsonOutput;
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
import com.gurella.engine.scene.light.SpotLightComponent;
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
import com.gurella.engine.test.TestArrayEditorComponent;
import com.gurella.engine.test.TestEditorComponent;
import com.gurella.engine.test.TestInputComponent;
import com.gurella.engine.test.TestPropertyEditorsComponent;
import com.gurella.engine.test.TestTypeSelectionComponnent;
import com.gurella.engine.utils.Reflection;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.operation.AddComponentOperation;
import com.gurella.studio.editor.operation.AddNodeOperation;
import com.gurella.studio.editor.utils.SaveFileDialog;

class GraphMenu {
	private final SceneGraphView view;

	GraphMenu(SceneGraphView view) {
		this.view = view;
	}

	void show(SceneElement2 selection) {
		Menu menu = new Menu(view.getShell(), POP_UP);
		new MenuPopulator(view, selection).populate(menu);
		menu.setLocation(view.getDisplay().getCursorLocation());
		menu.setVisible(true);
	}

	private static class MenuPopulator {
		private final SceneGraphView view;
		private final Clipboard clipboard;
		private final SceneEditorContext context;
		private final int editorId;
		private final SceneElement2 selection;

		MenuPopulator(SceneGraphView view, SceneElement2 selection) {
			this.view = view;
			this.clipboard = view.clipboard;
			this.context = view.editorContext;
			this.editorId = context.editorId;
			this.selection = selection;
		}

		private void populate(Menu menu) {
			boolean selected = selection != null;
			boolean nodeSelected = selection instanceof SceneNode2;
			final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
			boolean elementInClipboard = clipboard.getContents(transfer) instanceof ElementSelection;
			SceneNode2 node = selection instanceof SceneNode2 ? (SceneNode2) selection : null;

			MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText("Cut");
			item.addListener(SWT.Selection, e -> view.cut(selection));
			item.setEnabled(selected);

			item = new MenuItem(menu, SWT.PUSH);
			item.setText("Copy");
			item.addListener(SWT.Selection, e -> view.copy(selection));
			item.setEnabled(selected);

			item = new MenuItem(menu, SWT.PUSH);
			item.setText("Paste");
			item.addListener(SWT.Selection, e -> view.paste(node));
			item.setEnabled(nodeSelected && elementInClipboard);
			addSeparator(menu);

			item = new MenuItem(menu, SWT.PUSH);
			item.setText("Delete");
			item.addListener(SWT.Selection, e -> view.delete(selection));
			item.setEnabled(selected);

			item = new MenuItem(menu, SWT.PUSH);
			item.setText("Duplicate");
			item.addListener(SWT.Selection, e -> view.duplicate(node));
			item.setEnabled(nodeSelected);

			item = new MenuItem(menu, SWT.PUSH);
			item.setText("Empty node");
			item.addListener(SWT.Selection, e -> addNode(null));

			item = new MenuItem(menu, SWT.PUSH);
			item.setText("Empty child node");
			item.addListener(SWT.Selection, e -> addNode(node));
			item.setEnabled(nodeSelected);
			addSeparator(menu);

			item = new MenuItem(menu, SWT.PUSH);
			item.setText("Convert to prefab");
			item.addListener(SWT.Selection, e -> toPrefab());
			item.setEnabled(nodeSelected);
			addSeparator(menu);

			createCompositeNodeSubMenu(menu);
			addSeparator(menu);

			createComponentsSubMenu(menu);
		}

		private void toPrefab() {
			try {
				String extensions = Arrays.stream(prefab.extensions).map(e -> "*." + e).collect(joining(";"));
				IProject project = context.project;
				IPath projectPath = project.getLocation();
				IPath assetsRootPath = projectPath.append("assets");
				SceneNode2 node = (SceneNode2) selection;
				String fileName = SaveFileDialog.getPath(assetsRootPath, extensions, node.getName());
				if (fileName == null) {
					return;
				}
				JsonOutput output = new JsonOutput();
				SceneNode2 template = (SceneNode2) Optional.ofNullable(node.getPrefab()).map(p -> p.get()).orElse(null);
				String source = output.serialize(fileName, SceneNode2.class, template, node);
				IPath assetPath = new Path(fileName).makeRelativeTo(projectPath);
				IFile file = project.getFile(assetPath);
				InputStream is = new ByteArrayInputStream(source.getBytes("UTF-8"));
				file.create(is, true, new NullProgressMonitor());
			} catch (Exception e) {
				GurellaStudioPlugin.showError(e, "Error while converting to prefab.");
			}
		}

		protected void createCompositeNodeSubMenu(Menu menu) {
			MenuItem subItem = new MenuItem(menu, SWT.CASCADE);
			subItem.setText("Add node");
			Menu subMenu = new Menu(menu);
			subItem.setMenu(subMenu);

			MenuItem item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Add sphere");
			item.addListener(SWT.Selection, e -> addShapeNode("Sphere", new SphereShapeModel()));

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Add box");
			item.addListener(SWT.Selection, e -> addShapeNode("Box", new BoxShapeModel()));

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Add cylinder");
			item.addListener(SWT.Selection, e -> addShapeNode("Cylinder", new CylinderShapeModel()));

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Add cone");
			item.addListener(SWT.Selection, e -> addShapeNode("Cone", new ConeShapeModel()));

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Add capsule");
			item.addListener(SWT.Selection, e -> addShapeNode("Capsule", new CapsuleShapeModel()));

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Add rectangle");
			item.addListener(SWT.Selection, e -> addShapeNode("Rectangle", new RectangleShapeModel()));

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Add composite");
			CompositeShapeModel shapeModel = new CompositeShapeModel();
			shapeModel.addShape(new BoxShapeModel());
			shapeModel.addShape(new CylinderShapeModel(), 0, 1, 0);
			CompositeShapeModel composite = new CompositeShapeModel();
			composite.addShape(new CylinderShapeModel(), 0, 1, 0);
			shapeModel.addShape(composite, 0, 1, 0);
			item.addListener(SWT.Selection, e -> addShapeNode("Composite", shapeModel));
		}

		private void createComponentsSubMenu(Menu menu) {
			MenuItem subItem = new MenuItem(menu, SWT.CASCADE);
			subItem.setText("Add Component");
			Menu subMenu = new Menu(menu);
			subItem.setMenu(subMenu);
			addNewComponentMenuItem(subMenu, TransformComponent.class);
			addSeparator(subMenu);
			addNewComponentMenuItem(subMenu, BulletRigidBodyComponent.class);
			addSeparator(subMenu);
			addNewComponentMenuItem(subMenu, OrtographicCameraComponent.class);
			addNewComponentMenuItem(subMenu, PerspectiveCameraComponent.class);
			addSeparator(subMenu);
			addNewComponentMenuItem(subMenu, DirectionalLightComponent.class);
			addNewComponentMenuItem(subMenu, PointLightComponent.class);
			addNewComponentMenuItem(subMenu, SpotLightComponent.class);
			addSeparator(subMenu);
			addNewComponentMenuItem(subMenu, AudioListenerComponent.class);
			addNewComponentMenuItem(subMenu, AudioSourceComponent.class);
			addSeparator(subMenu);
			addNewComponentMenuItem(subMenu, TagComponent.class);
			addSeparator(subMenu);
			addNewComponentMenuItem(subMenu, TextureComponent.class);
			addNewComponentMenuItem(subMenu, TextureRegionComponent.class);
			addNewComponentMenuItem(subMenu, AtlasRegionComponent.class);
			addNewComponentMenuItem(subMenu, SkyboxComponent.class);
			addSeparator(subMenu);
			addNewComponentMenuItem(subMenu, ModelComponent.class);
			addNewComponentMenuItem(subMenu, ShapeComponent.class);
			addSeparator(subMenu);
			addNewComponentMenuItem(subMenu, TestPropertyEditorsComponent.class);
			addNewComponentMenuItem(subMenu, TestEditorComponent.class);
			addNewComponentMenuItem(subMenu, TestInputComponent.class);
			addNewComponentMenuItem(subMenu, TestTypeSelectionComponnent.class);
			addNewComponentMenuItem(subMenu, TestArrayEditorComponent.class);
			addSeparator(subMenu);
		}

		private static MenuItem addSeparator(Menu menu) {
			return new MenuItem(menu, SEPARATOR);
		}

		private void addNewComponentMenuItem(Menu menu, final Class<? extends SceneNodeComponent2> componentType) {
			MenuItem item = new MenuItem(menu, PUSH);
			item.setText(Models.getModel(componentType).getName());
			item.addListener(SWT.Selection, (e) -> addComponent(Reflection.newInstance(componentType)));
			item.setEnabled(selection instanceof SceneNode2);
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
			SceneNode2 node = (SceneNode2) selection;
			AddComponentOperation operation = new AddComponentOperation(editorId, node, component);
			context.executeOperation(operation, "Error while adding component");
		}

		private void addNode(SceneNode2 parent) {
			InputDialog dlg = new InputDialog(view.getShell(), "Add Node", "Enter node name", "Node",
					this::validateNewName);

			if (dlg.open() != Window.OK) {
				return;
			}

			SceneNode2 node = new SceneNode2();
			node.setName(dlg.getValue());
			node.newComponent(TransformComponent.class);

			AddNodeOperation operation = new AddNodeOperation(editorId, context.getScene(), parent, node);
			context.executeOperation(operation, "Error while adding node");
		}

		private String validateNewName(String input) {
			return input.length() < 3 ? "Too short" : null;
		}
	}
}
