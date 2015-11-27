package com.gurella.studio.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graph.SceneSystem;
import com.gurella.engine.graph.bullet.BulletPhysicsProcessor;
import com.gurella.engine.graph.input.InputSystem;
import com.gurella.engine.graph.renderable.RenderSystem;
import com.gurella.engine.resource.ResourceReference;
import com.gurella.engine.resource.SharedResourceReference;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.signal.Listener1;
import com.gurella.studio.project.ProjectHeaderContainer.SceneSelectionChangedEvent;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class SystemsContainer extends VisTable {
	private VisTable header = new VisTable();
	private VisLabel headerLabel = new VisLabel("Systems");
	private VisTextButton menuButton = new VisTextButton("...");
	private VisList<SceneItem> systemsList = new VisList<SceneItem>();
	private Actor spacer = new Actor();

	private Scene scene;
	private ObjectSet<Class<? extends SceneSystem>> usedSystemTypes = new ObjectSet<Class<? extends SceneSystem>>();

	public SystemsContainer() {
		setBackground("border");
		menuButton.addListener(new MenuClickListener());
		menuButton.setDisabled(true);

		header.setBackground("button-down");
		header.setColor(0.5f, 0.5f, 0.5f, 0.5f);
		header.add(headerLabel).top().left().fillX().expandX();
		header.add(menuButton).top().left();
		add(header).top().left().fillX().expandX();
		EventService.addListener(SceneSelectionChangedEvent.class, new SceneSelectionChangedListener());
	}

	private void clearScene() {
		menuButton.setDisabled(true);
		clearChildren();
		add(header).top().left().fillX().expandX();
		row();
		add(spacer).top().left().fill().expand();
	}

	public void presentScene(Scene scene) {
		menuButton.setDisabled(false);
		this.scene = scene;
		buildUi();
	}

	private void buildUi() {
		clearChildren();
		add(header).top().left().fillX().expandX();

		usedSystemTypes.clear();
		IntArray initialSystems = scene.getInitialSystems();
		Array<SceneItem> items = new Array<SceneItem>();
		for (int i = 0; i < initialSystems.size; i++) {
			int systemId = initialSystems.get(i);
			ResourceReference<? extends SceneSystem> reference = scene.getReference(systemId);
			items.add(new SceneItem(reference));
			usedSystemTypes.add(reference.getResourceFactory().getResourceType());
		}
		systemsList.setItems(items);
		row();
		add(systemsList).top().left().fill().expand();
	}

	private <T extends SceneSystem> void addSystem(Class<T> systemType) {
		ModelResourceFactory<T> systemFactory = new ModelResourceFactory<T>(systemType);
		int systemId = scene.getNextId();
		SharedResourceReference<T> systemReference = new SharedResourceReference<T>(systemId, null, false, false,
				systemFactory);
		scene.add(systemReference);
		scene.addInitialSystem(systemId);
		buildUi();
	}

	private class SceneSelectionChangedListener implements Listener1<Scene> {
		@Override
		public void handle(Scene scene) {
			if (scene == null) {
				clearScene();
			} else {
				presentScene(scene);
			}
		}
	}

	private class MenuClickListener extends ChangeListener {
		private PopupMenu menu;
		private ObjectMap<Class<? extends SceneSystem>, AddSystemMenuItem> itemsByComponentType = new ObjectMap<Class<? extends SceneSystem>, AddSystemMenuItem>();

		public MenuClickListener() {
			createMenu();
		}

		private PopupMenu createMenu() {
			menu = new PopupMenu();
			addItem("Input", InputSystem.class);
			menu.addSeparator();
			addItem("Render", RenderSystem.class);
			menu.addSeparator();
			addItem("BulletPhysics", BulletPhysicsProcessor.class);
			menu.addSeparator();
			return menu;
		}

		private void addItem(String name, Class<? extends SceneSystem> systemType) {
			AddSystemMenuItem item = new AddSystemMenuItem(name, systemType);
			itemsByComponentType.put(systemType, item);
			menu.addItem(item);
		}

		@Override
		public void changed(ChangeEvent event, Actor actor) {
			enableItems();
			menu.showMenu(getStage(), Gdx.input.getX() - menu.getWidth(), getStage().getHeight() - Gdx.input.getY());
		}

		private void enableItems() {
			for (AddSystemMenuItem item : itemsByComponentType.values()) {
				item.setDisabled(usedSystemTypes.contains(item.systemType));
			}
		}
	}

	private class AddSystemMenuItem extends MenuItem {
		private final Class<? extends SceneSystem> systemType;

		public AddSystemMenuItem(String name, Class<? extends SceneSystem> systemType) {
			super(name);
			this.systemType = systemType;
			addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					addSystem(AddSystemMenuItem.this.systemType);
				}
			});
		}
	}

	private static class SceneItem {
		ResourceReference<? extends SceneSystem> reference;

		public SceneItem(ResourceReference<? extends SceneSystem> reference) {
			this.reference = reference;
		}

		@Override
		public String toString() {
			return reference.getResourceFactory().getResourceType().getSimpleName();
		}
	}
}
