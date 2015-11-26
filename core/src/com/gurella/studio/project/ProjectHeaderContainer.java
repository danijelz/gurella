package com.gurella.studio.project;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.event.EventBus;
import com.gurella.engine.event.Listener1Event;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.signal.Listener1;
import com.gurella.studio.GdxStudioEvents;
import com.gurella.studio.Project;
import com.gurella.studio.SelectProjectDialog;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class ProjectHeaderContainer extends VisTable implements Listener1<String> {
	private VisTextButton projectButton = new VisTextButton("Project");
	private VisTextButton saveButton = new VisTextButton("Save");
	private VisSelectBox<SceneItem> sceneSelector = new VisSelectBox<SceneItem>();
	
	private Project project;

	public ProjectHeaderContainer() {
		setBackground("border");
		sceneSelector.addListener(new SelectionChangeListener());
		
		projectButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				EventBus.GLOBAL.notify(GdxStudioEvents.SELECT_PROJECT_EVENT);
			}
		});
		
		saveButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				EventBus.GLOBAL.notify(GdxStudioEvents.SAVE_PROJECT_EVENT);
			}
		});
		
		add(projectButton).pad(10, 10, 10, 10).left();
		add(saveButton).pad(10, 10, 10, 10).left();
		add(new VisTextButton("Application")).pad(10, 10, 10, 10).left();
		add(new VisTextButton("Scenes")).pad(10, 0, 10, 10).left();
		add(new VisLabel("Selected scene:")).pad(10, 0, 10, 10).left();
		add(sceneSelector).left().pad(10, 0, 10, 10).expandX();
		
		EventBus.GLOBAL.addListener(GdxStudioEvents.SELECT_PROJECT_EVENT, this);
	}

	public void present(Project project) {
		this.project = project;
		
		Array<SceneItem> items = new Array<SceneItem>();
		items.add(new SceneItem());
		for(Scene scene : project.getApplication().getScenes().values()) {
			SceneItem item = new SceneItem();
			item.scene = scene;
			items.add(item);
		}
		sceneSelector.setItems(items);
	}
	
	@Override
	public void handle(String eventType) {
		if(GdxStudioEvents.SELECT_PROJECT_EVENT.equals(eventType)) {
			SelectProjectDialog selectProjectDialog = new SelectProjectDialog();
			selectProjectDialog.show(getStage());
		}
	}
	
	private static class SceneItem {
		private Scene scene;
		
		@Override
		public String toString() {
			return scene == null ? "" : scene.getId();
		}
	}
	
	private class SelectionChangeListener extends ChangeListener {
		SceneItem lastSelected;
		
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			SceneItem selected = sceneSelector.getSelected();
			if(selected != lastSelected) {
				lastSelected = selected;
				EventBus.GLOBAL.notify(new SceneSelectionChangedEvent(selected.scene));
			}
		}
	}
	
	public static class SceneSelectionChangedEvent extends Listener1Event<Scene> {
		private SceneSelectionChangedEvent(Scene value) {
			super(value);
		}
	}
}
