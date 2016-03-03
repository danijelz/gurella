package com.gurella.studio.nodes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.event.EventService;
import com.gurella.engine.resource.ResourceReference;
import com.gurella.engine.resource.SharedResourceReference;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceId;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.audio.AudioListenerComponent;
import com.gurella.engine.scene.audio.AudioSourceComponent;
import com.gurella.engine.scene.bullet.BulletPhysicsRigidBodyComponent;
import com.gurella.engine.scene.camera.OrtographicCameraComponent;
import com.gurella.engine.scene.camera.PerspectiveCameraComponent;
import com.gurella.engine.scene.layer.LayerComponent;
import com.gurella.engine.scene.light.DirectionalLightComponent;
import com.gurella.engine.scene.light.PointLightComponent;
import com.gurella.engine.scene.movement.TransformComponent;
import com.gurella.engine.scene.renderable.AtlasRegionComponent;
import com.gurella.engine.scene.renderable.ModelComponent;
import com.gurella.engine.scene.renderable.SolidComponent;
import com.gurella.engine.scene.renderable.TextureComponent;
import com.gurella.engine.scene.renderable.TextureRegionComponent;
import com.gurella.engine.scene.tag.TagComponent;
import com.gurella.engine.utils.Values;
import com.gurella.studio.inspector.InspectorPropertiesContainer;
import com.gurella.studio.nodes.SceneNodeTreeNode.NodeNameChangedEvent;
import com.gurella.studio.resource.ColapsableResourcePropertiesContainer;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldListener;

public class SceneNodePropertiesContainer extends VisTable implements InspectorPropertiesContainer {
	private VisTextButton menuButton = new VisTextButton("...");
	private Actor spacer = new Actor();

	private VisTextField nameField = new VisTextField();
	private VisCheckBox enabled = new VisCheckBox("enabled");

	private ResourceReference<? extends SceneNode> reference;
	private ModelResourceFactory<? extends SceneNode> factory;

	private Array<ColapsableResourcePropertiesContainer> componentContainers = new Array<ColapsableResourcePropertiesContainer>();
	IntSet usedComponentTypes = new IntSet();

	public SceneNodePropertiesContainer(ResourceReference<? extends SceneNode> reference) {
		this.reference = reference;
		this.factory = (ModelResourceFactory<? extends SceneNode>) reference.getResourceFactory();

		buildUi();
		nameField.setText(Values.isBlank(reference.getName()) ? "Node" : reference.getName());
		enabled.setChecked(factory.<Boolean> getPropertyValue("enabled", Boolean.TRUE).booleanValue());
		setBackground("border");
		menuButton.addListener(new MenuClickListener());
	}

	private void buildUi() {
		add(new VisLabel("Name: ")).top().left();
		add(nameField).top().left();
		add(enabled).top().left().expandX();
		add(menuButton).top().left();
		row();

		usedComponentTypes.clear();
		Array<ResourceReference<SceneNodeComponent>> components = factory.getPropertyValue("components");
		if (components != null) {
			for (Object componentValue : components) {
				ModelResourceFactory<SceneNodeComponent> componentFactory = getComponentFactory(componentValue);
				usedComponentTypes.add(SceneNodeComponent.getBaseComponentType(componentFactory.getResourceType()));

				ColapsableResourcePropertiesContainer componentContainer = new ColapsableResourcePropertiesContainer(
						componentFactory);
				componentContainers.add(componentContainer);
				add(componentContainer).colspan(4).top().left().fillX().expandX();
				row();
			}
		}

		add(spacer).top().left().fill().colspan(3).expand();

		nameField.setTextFieldListener(new TextFieldListener() {
			@Override
			public void keyTyped(VisTextField textField, char c) {
				factory.setPropertyValue("name", nameField.getText());
				EventService.notify(new NodeNameChangedEvent(factory));
			}
		});
	}

	private ModelResourceFactory<SceneNodeComponent> getComponentFactory(Object componentValue) {
		if (componentValue instanceof ResourceId) {
			ResourceId resourceId = (ResourceId) componentValue;
			return (ModelResourceFactory<SceneNodeComponent>) reference.getOwningContext()
					.<SceneNodeComponent> getReference(resourceId.getId()).getResourceFactory();
		} else if (componentValue instanceof ResourceReference) {
			@SuppressWarnings("unchecked")
			ResourceReference<SceneNodeComponent> componentReference = (ResourceReference<SceneNodeComponent>) componentValue;
			return (ModelResourceFactory<SceneNodeComponent>) componentReference.getResourceFactory();
		} else {
			@SuppressWarnings("unchecked")
			ModelResourceFactory<SceneNodeComponent> componentFactory = (ModelResourceFactory<SceneNodeComponent>) componentValue;
			return componentFactory;
		}
	}

	@Override
	public void save() {
		reference.setName(nameField.getText());
		factory.setPropertyValue("enabled", Boolean.valueOf(enabled.isChecked()));
		for (int i = 0; i < componentContainers.size; i++) {
			ColapsableResourcePropertiesContainer componentContainer = componentContainers.get(i);
			componentContainer.save();
		}
	}

	private <T extends SceneNodeComponent> void addComponent(Class<T> componentType) {
		Scene scene = null;//(Scene) reference.getOwningContext();
		ModelResourceFactory<T> componentFactory = new ModelResourceFactory<T>(componentType);
		//int componentId = scene.getNextId();
		//scene.add(new SharedResourceReference<T>(componentId, null, false, false, componentFactory));

		Array<ResourceId> components = factory.getPropertyValue("components");
		if (components == null) {
			components = new Array<ResourceId>();
		}

		//components.add(new ResourceId(componentId));
		factory.setPropertyValue("components", components);
		componentContainers.clear();
		clearChildren();
		buildUi();
	}

	private class MenuClickListener extends ChangeListener {
		private PopupMenu menu;
		private ObjectMap<Class<? extends SceneNodeComponent>, AddComponentMenuItem> itemsByComponentType = new ObjectMap<Class<? extends SceneNodeComponent>, AddComponentMenuItem>();

		public MenuClickListener() {
			createMenu();
		}

		private PopupMenu createMenu() {
			menu = new PopupMenu();
//			addItem("Transform", TransformComponent.class);
//			menu.addSeparator();
//			addItem("3D Collider", BulletPhysicsRigidBodyComponent.class);
//			menu.addSeparator();
//			addItem("2D Camera", OrtographicCameraComponent.class);
//			addItem("3D Camera", PerspectiveCameraComponent.class);
//			menu.addSeparator();
//			addItem("Point Light", PointLightComponent.class);
//			addItem("Directional Light", DirectionalLightComponent.class);
//			menu.addSeparator();
//			addItem("Audio Listener", AudioListenerComponent.class);
//			addItem("Audio Source", AudioSourceComponent.class);
//			menu.addSeparator();
//			addItem("Tag", TagComponent.class);
//			menu.addSeparator();
//			//addItem("Layer", LayerComponent.class);
//			menu.addSeparator();
//			addItem("Texture", TextureComponent.class);
//			addItem("Texture Region", TextureRegionComponent.class);
//			addItem("Atlas Region", AtlasRegionComponent.class);
//			addItem("Model", ModelComponent.class);
//			addItem("Solid", SolidComponent.class);
			menu.addSeparator();
			addItem("Test", TestComponnent.class);
			menu.addSeparator();
			addItem("Test Input", TestInputComponent.class);
			return menu;
		}

		private void addItem(String name, Class<? extends SceneNodeComponent> componentType) {
			AddComponentMenuItem item = new AddComponentMenuItem(name, componentType);
			itemsByComponentType.put(componentType, item);
			menu.addItem(item);
		}

		@Override
		public void changed(ChangeEvent event, Actor actor) {
			enableItems();
			menu.showMenu(getStage(), Gdx.input.getX() - menu.getWidth(), getStage().getHeight() - Gdx.input.getY());
		}

		private void enableItems() {
			for (AddComponentMenuItem item : itemsByComponentType.values()) {
				item.setDisabled(
						usedComponentTypes.contains(SceneNodeComponent.getBaseComponentType(item.componentType)));
			}
		}
	}

	private class AddComponentMenuItem extends MenuItem {
		private final Class<? extends SceneNodeComponent> componentType;

		public AddComponentMenuItem(String name, Class<? extends SceneNodeComponent> componentType) {
			super(name);
			this.componentType = componentType;
			addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					addComponent(AddComponentMenuItem.this.componentType);
				}
			});
		}
	}

	public static class TestComponnent extends SceneNodeComponent {
		public Vector3 testVector;
		public String[] testStringArray;
		public int[] testIntArray;
		public Integer[] testIntegerArray;
		public Vector3[] testVectorArray;
		public Texture texture;
		public Texture texture1;
		public Texture texture2;
		public Texture texture3;
	}
}
