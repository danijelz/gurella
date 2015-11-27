package com.gurella.studio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.gurella.engine.event.EventService;
import com.gurella.studio.EditorScreen.PresentProjectEvent;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.widget.Separator;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;

public class SelectProjectDialog extends VisDialog {
	private VisList<ProjectFileHandle> projectsList = new VisList<ProjectFileHandle>();
	private VisTextButton addProjectButton;
	private VisTextButton removeProjectButton;

	private VisTextButton confirmButton;
	private VisTextButton cancleButton;

	public SelectProjectDialog() {
		super("Projects");
		setModal(true);
		setResizable(true);

		getContentTable().add(new Separator()).colspan(2).expandX().fillX();
		getContentTable().row();

		projectsList.addListener(new ProjectSelectionChangedListener());
		projectsList.setColor(1, 1, 1, 1);
		VisScrollPane scrollPane = new VisScrollPane(projectsList);
		getContentTable().add(scrollPane).expand().fill();

		addProjectButton = new VisTextButton("New project");
		removeProjectButton = new VisTextButton("Delete project");

		VisTable buttons = new VisTable();
		buttons.add(addProjectButton).top().left().expandX().fillX().pad(0, 10, 5, 10);
		buttons.row();
		buttons.add(removeProjectButton).top().left().expand().fillX().pad(5, 10, 5, 10);
		// buttons.setWidth(100);
		getContentTable().add(buttons).top().left().fill().expandY();

		confirmButton = new VisTextButton("Confirm");
		confirmButton.setDisabled(true);
		confirmButton.addListener(new ConfirmListener());
		button(confirmButton);

		cancleButton = new VisTextButton("Cancle");
		confirmButton.addListener(new CancleListener());
		button(cancleButton);

		projectsList.setItems(getProjects());
	}

	@Override
	public float getPrefWidth() {
		return 700;
	}

	@Override
	public float getPrefHeight() {
		return 600;
	}

	private static Array<ProjectFileHandle> getProjects() {
		FileHandle homeDirectory = Gdx.files.absolute(System.getProperty("user.home") + "/.gdxStudio");
		Array<ProjectFileHandle> projects = new Array<ProjectFileHandle>();

		for (FileHandle project : homeDirectory.list("json")) {
			projects.add(new ProjectFileHandle(project));
		}

		return projects;
	}

	private class AddProjectListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			FileChooser fileChooser = new FileChooser(Mode.OPEN);
			fileChooser.setSelectionMode(SelectionMode.FILES);
			fileChooser.setListener(new FileChooserAdapter() {
				@Override
				public void selected(FileHandle file) {
					// textField.setText(file.file().getAbsolutePath());
				}
			});
			// fileChooser.
		}
	}

	private static class ProjectFileHandle {
		private FileHandle fileHandle;
		private String name;

		public ProjectFileHandle(FileHandle fileHandle) {
			this.fileHandle = fileHandle;
			name = fileHandle.nameWithoutExtension();
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private class ConfirmListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			ProjectFileHandle selected = projectsList.getSelected();
			if (selected == null) {
				DialogUtils.showErrorDialog(getStage(), "No project selected.");
			}
			Project project = new Json().fromJson(Project.class, selected.fileHandle);
			project.projectFileHandle = selected.fileHandle;
			EventService.notify(new PresentProjectEvent(project));
			hide();
		}
	}

	private class CancleListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			hide();
		}
	}

	private class ProjectSelectionChangedListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			ProjectFileHandle selected = projectsList.getSelected();
			if (selected == null) {
				removeProjectButton.setDisabled(true);
				confirmButton.setDisabled(true);
			} else {
				removeProjectButton.setDisabled(false);
				confirmButton.setDisabled(false);
			}
		}
	}
}
