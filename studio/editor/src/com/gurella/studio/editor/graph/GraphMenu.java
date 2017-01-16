package com.gurella.studio.editor.graph;

import static com.gurella.engine.asset.AssetType.prefab;
import static org.eclipse.swt.SWT.POP_UP;
import static org.eclipse.swt.SWT.PUSH;
import static org.eclipse.swt.SWT.SEPARATOR;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.managedobject.ManagedObject;
import com.gurella.engine.managedobject.PrefabReference;
import com.gurella.engine.metatype.CopyContext;
import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneElement;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
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
import com.gurella.engine.serialization.json.JsonOutput;
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
import com.gurella.studio.editor.operation.ConvertToPrefabOperation;
import com.gurella.studio.editor.operation.ReparentNodeOperation;
import com.gurella.studio.editor.utils.FileDialogUtils;

class GraphMenu {
	private final SceneGraphView view;

	GraphMenu(SceneGraphView view) {
		this.view = view;
	}

	void show(SceneElement selection) {
		Menu menu = new Menu(view.getShell(), POP_UP);
		new MenuPopulator(view, selection).populate(menu);
		menu.setLocation(view.getDisplay().getCursorLocation());
		menu.setVisible(true);
	}

	private static class MenuPopulator {
		private final SceneGraphView view;
		private final Clipboard clipboard;
		private final int editorId;
		private final SceneEditorContext context;
		private final SceneElement selection;

		MenuPopulator(SceneGraphView view, SceneElement selection) {
			this.view = view;
			this.clipboard = view.clipboard;
			this.context = view.context;
			this.editorId = context.editorId;
			this.selection = selection;
		}

		private void populate(Menu menu) {
			boolean selected = selection != null;
			boolean nodeSelected = selection instanceof SceneNode;
			final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
			boolean elementInClipboard = clipboard.getContents(transfer) instanceof ElementSelection;
			SceneNode node = selection instanceof SceneNode ? (SceneNode) selection : null;

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
			item.setText("Rename");
			item.addListener(SWT.Selection, e -> view.rename(node));
			item.setEnabled(nodeSelected);
			addSeparator(menu);

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
			item.addListener(SWT.Selection, e -> convertToPrefab());
			item.setEnabled(nodeSelected);

			item = new MenuItem(menu, SWT.PUSH);
			item.setText("Convert to root");
			item.addListener(SWT.Selection, e -> convertToRoot());
			item.setEnabled(nodeSelected && node.getParentNode() != null);
			addSeparator(menu);

			createNodeSubMenu(menu, "Add node", null, true);
			createNodeSubMenu(menu, "Add child node", node, nodeSelected);
			addSeparator(menu);

			createComponentsSubMenu(menu);
		}

		private void convertToRoot() {
			int editorId = context.editorId;
			SceneNode node = (SceneNode) selection;
			int newIndex = node.getScene().nodes.size();
			String errorMsg = "Error while repositioning node";
			view.historyService.executeOperation(new ReparentNodeOperation(editorId, node, null, newIndex), errorMsg);
		}

		private void convertToPrefab() {
			try {
				IProject project = context.project;
				IPath projectPath = project.getLocation();
				IPath assetsRootPath = projectPath.append("assets");
				SceneNode node = (SceneNode) selection;
				IFolder folder = project.getFolder(assetsRootPath);
				Optional<String> fileName = FileDialogUtils.selectNewFileName(folder, node.getName(), prefab);
				if (!fileName.isPresent()) {
					return;
				}

				IPath projectAssetPath = new Path(fileName.get()).makeRelativeTo(projectPath);
				SceneNode prefab = CopyContext.copyObject(node);
				JsonOutput output = new JsonOutput();
				SceneNode template = Optional.ofNullable(prefab.getPrefab()).map(p -> (SceneNode) p.get()).orElse(null);
				String source = output.serialize(projectAssetPath.toString(), ManagedObject.class, template, prefab);
				String pretty = new JsonReader().parse(source).prettyPrint(OutputType.minimal, 120);
				InputStream is = new ByteArrayInputStream(pretty.getBytes("UTF-8"));
				IFile file = project.getFile(projectAssetPath);
				IPath gdxAssetPath = new Path(fileName.get()).makeRelativeTo(assetsRootPath);
				if (file.exists()) {
					PrefabReference oldPrefabReference = prefab.getPrefab();
					if (oldPrefabReference != null
							&& oldPrefabReference.getFileName().equals(gdxAssetPath.toString())) {
						// TODO overriding existing prefab
					}
					file.setContents(is, true, true, context.getProgressMonitor());
				} else {
					file.create(is, true, context.getProgressMonitor());
				}
				AssetService.put(prefab, gdxAssetPath.toString());
				String errMsg = "Error while converting to prefab";
				view.historyService.executeOperation(new ConvertToPrefabOperation(editorId, node, prefab), errMsg);
			} catch (Exception e) {
				GurellaStudioPlugin.showError(e, "Error while converting to prefab.");
			}
		}

		protected void createNodeSubMenu(Menu menu, String label, SceneNode parent, boolean enabled) {
			MenuItem subItem = new MenuItem(menu, SWT.CASCADE);
			subItem.setText(label);
			Menu subMenu = new Menu(menu);
			subMenu.setEnabled(enabled);
			subItem.setMenu(subMenu);

			MenuItem item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Sprite");
			item.addListener(SWT.Selection, e -> addComponentNode("Sprite", TextureComponent.class, parent));
			item.setEnabled(enabled);

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Model");
			item.addListener(SWT.Selection, e -> addComponentNode("Model", ModelComponent.class, parent));
			item.setEnabled(enabled);

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Skybox");
			item.addListener(SWT.Selection, e -> addComponentNode("Skybox", SkyboxComponent.class, parent));
			item.setEnabled(enabled);

			addShapesSubmenu(subMenu, parent, enabled);
			addSeparator(subMenu);

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Ortographic camera");
			item.addListener(SWT.Selection,
					e -> addComponentNode("Ortographic camera", OrtographicCameraComponent.class, parent));
			item.setEnabled(enabled);

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Perspective camera");
			item.addListener(SWT.Selection,
					e -> addComponentNode("Perspective camera", PerspectiveCameraComponent.class, parent));
			item.setEnabled(enabled);
			addSeparator(subMenu);

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Directional light");
			item.addListener(SWT.Selection,
					e -> addComponentNode("Directional light", DirectionalLightComponent.class, parent));
			item.setEnabled(enabled);

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Point light");
			item.addListener(SWT.Selection, e -> addComponentNode("Point light", PointLightComponent.class, parent));
			item.setEnabled(enabled);

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Spot light");
			item.addListener(SWT.Selection, e -> addComponentNode("Spot light", SpotLightComponent.class, parent));
			item.setEnabled(enabled);
			addSeparator(subMenu);

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Audio listener");
			item.addListener(SWT.Selection,
					e -> addComponentNode("Audio listener", AudioListenerComponent.class, parent));
			item.setEnabled(enabled);

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Audio source");
			item.addListener(SWT.Selection, e -> addComponentNode("Audio source", AudioSourceComponent.class, parent));
			item.setEnabled(enabled);
			addSeparator(subMenu);

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Tag");
			item.addListener(SWT.Selection, e -> addComponentNode("Tag", TagComponent.class, parent));
			item.setEnabled(enabled);
		}

		private void addShapesSubmenu(Menu menu, SceneNode parent, boolean enabled) {
			MenuItem subItem = new MenuItem(menu, SWT.CASCADE);
			subItem.setText("Shape");
			Menu subMenu = new Menu(menu);
			subItem.setEnabled(enabled);
			subItem.setMenu(subMenu);

			MenuItem item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Add sphere");
			item.addListener(SWT.Selection, e -> addShapeNode("Sphere", new SphereShapeModel(), parent));

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Add box");
			item.addListener(SWT.Selection, e -> addShapeNode("Box", new BoxShapeModel(), parent));

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Add cylinder");
			item.addListener(SWT.Selection, e -> addShapeNode("Cylinder", new CylinderShapeModel(), parent));

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Add cone");
			item.addListener(SWT.Selection, e -> addShapeNode("Cone", new ConeShapeModel(), parent));

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Add capsule");
			item.addListener(SWT.Selection, e -> addShapeNode("Capsule", new CapsuleShapeModel(), parent));

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Add rectangle");
			item.addListener(SWT.Selection, e -> addShapeNode("Rectangle", new RectangleShapeModel(), parent));

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Add composite");
			CompositeShapeModel shapeModel = new CompositeShapeModel();
			shapeModel.addShape(new BoxShapeModel());
			shapeModel.addShape(new CylinderShapeModel(), 0, 1, 0);
			CompositeShapeModel composite = new CompositeShapeModel();
			composite.addShape(new CylinderShapeModel(), 0, 1, 0);
			shapeModel.addShape(composite, 0, 1, 0);
			item.addListener(SWT.Selection, e -> addShapeNode("Composite", shapeModel, parent));
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

		private void addNewComponentMenuItem(Menu menu, final Class<? extends SceneNodeComponent> componentType) {
			MenuItem item = new MenuItem(menu, PUSH);
			item.setText(MetaTypes.getMetaType(componentType).getName());
			item.addListener(SWT.Selection, (e) -> addComponent(Reflection.newInstance(componentType)));
			item.setEnabled(selection instanceof SceneNode);
		}

		private <T extends SceneNodeComponent & Poolable> void addComponentNode(String name, Class<T> componentType,
				SceneNode parent) {
			Scene scene = context.getScene();
			SceneNode node = parent == null ? scene.newNode(name) : parent.newChild(name);
			node.newComponent(TransformComponent.class);
			node.newComponent(componentType);
			AddNodeOperation operation = new AddNodeOperation(editorId, scene, parent, node);
			view.historyService.executeOperation(operation, "Error while adding node");
		}

		private void addShapeNode(String name, ShapeModel shapeModel, SceneNode parent) {
			Scene scene = context.getScene();
			SceneNode node = parent == null ? scene.newNode(name) : parent.newChild(name);
			node.newComponent(TransformComponent.class);
			ShapeComponent shapeComponent = node.newComponent(ShapeComponent.class);
			shapeComponent.setShape(shapeModel);
			AddNodeOperation operation = new AddNodeOperation(editorId, scene, parent, node);
			view.historyService.executeOperation(operation, "Error while adding node");
		}

		private void addComponent(SceneNodeComponent component) {
			SceneNode node = (SceneNode) selection;
			AddComponentOperation operation = new AddComponentOperation(editorId, node, component);
			view.historyService.executeOperation(operation, "Error while adding component");
		}

		private void addNode(SceneNode parent) {
			InputDialog dlg = new InputDialog(view.getShell(), "Add Node", "Enter node name", "Node",
					i -> i.length() < 3 ? "Too short" : null);

			if (dlg.open() != Window.OK) {
				return;
			}

			SceneNode node = new SceneNode();
			node.setName(dlg.getValue());
			node.newComponent(TransformComponent.class);

			AddNodeOperation operation = new AddNodeOperation(editorId, context.getScene(), parent, node);
			view.historyService.executeOperation(operation, "Error while adding node");
		}
	}
}
