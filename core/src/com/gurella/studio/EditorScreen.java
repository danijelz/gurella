package com.gurella.studio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.Listener1Event;
import com.gurella.engine.signal.Listener1;
import com.gurella.studio.inspector.InspectorContainer;
import com.gurella.studio.nodes.SceneNodesContainer;
import com.gurella.studio.project.ProjectFooterContainer;
import com.gurella.studio.project.ProjectHeaderContainer;
import com.gurella.studio.sceneview.SceneView;
import com.gurella.studio.systems.SystemsContainer;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisTable;

public class EditorScreen extends ScreenAdapter {
	private Stage stage;
	private ScreenViewport viewport;

	private VisTable rootComponent;

	private ProjectHeaderContainer headerContainer = new ProjectHeaderContainer();
	private SceneView center;
	private ProjectFooterContainer footer = new ProjectFooterContainer();

	private VisSplitPane left;
	private VisSplitPane right;

	private SystemsContainer systemsContainer = new SystemsContainer();
	private SceneNodesContainer nodesContainer = new SceneNodesContainer();
	private InspectorContainer inspectorContainer = new InspectorContainer();

	private Project project;

	public EditorScreen() {
		viewport = new ScreenViewport();
		stage = new Stage(viewport, GdxEditor.batch);
		stage.setViewport(viewport);

		rootComponent = new VisTable();
		rootComponent.setFillParent(true);
		stage.addActor(rootComponent);

		rootComponent.add(headerContainer).prefHeight(40).expandX().fillX();

		rootComponent.row();

		center = new SceneView();
		left = new VisSplitPane(createLeftContent(), center, false);
		left.setMinSplitAmount(0.2f);
		right = new VisSplitPane(left, new VisScrollPane(inspectorContainer), false);
		right.setMinSplitAmount(0.2f);

		rootComponent.add(right).expand().fill();

		rootComponent.row();

		rootComponent.add(footer).prefHeight(130).expandX().fillX();

		EventService.addListener(PresentProjectEvent.class, new PresentProjectListener());
		EventService.addListener(GdxStudioEvents.SAVE_PROJECT_EVENT, new SaveProjectListener());

		EventService.notify(GdxStudioEvents.SELECT_PROJECT_EVENT);
	}

	private void present(Project selectedProject) {
		this.project = selectedProject;
		headerContainer.present(project);
	}

	private void save() {
		inspectorContainer.save();
		project.save();
	}

	private VisScrollPane createLeftContent() {
		VisTable leftContent = new VisTable();
		leftContent.add(systemsContainer).top().left().expand().fill();
		leftContent.row();
		leftContent.add(nodesContainer).top().left().expand().fill();
		leftContent.row();
		return new VisScrollPane(leftContent);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		viewport.update(width, height, true);
	}

	@Override
	public void show() {
		super.show();
		Gdx.input.setInputProcessor(stage);
	}

	public static class PresentProjectEvent extends Listener1Event<Project> {
		public PresentProjectEvent(Project value) {
			super(value);
		}
	}

	private class PresentProjectListener implements Listener1<Project> {
		@Override
		public void handle(Project selectedProject) {
			present(selectedProject);
		}
	}

	private class SaveProjectListener implements Listener1<String> {
		@Override
		public void handle(String event) {
			save();
		}
	}
}
