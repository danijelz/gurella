package com.gurella.studio.editor;

import java.util.function.BiConsumer;

import org.eclipse.swt.SWT;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.event.EventService;
import com.gurella.studio.editor.assets.AssetsView;
import com.gurella.studio.editor.graph.SceneGraphView;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.subscription.EditorContextMenuContributor;

class CommonContextMenuContributor implements EditorContextMenuContributor, Disposable {
	private static final String cameraGroupName = "Camera";
	private static final String moveToGroupName = "Move to";
	private static final String viewGroupName = "View";

	private final SceneEditor editor;
	private final SceneEditorApplicationListener application;

	CommonContextMenuContributor(SceneEditor editor) {
		this.editor = editor;
		this.application = editor.applicationListener;
		EventService.subscribe(editor.id, this);
	}

	@Override
	public void contribute(ContextMenuActions actions) {
		actions.addGroup(cameraGroupName, 1);
		actions.addCheckAction(cameraGroupName, "2d", 1, application.is2d(), () -> application.set2d());
		actions.addCheckAction(cameraGroupName, "3d", 2, application.is3d(), () -> application.set3d());

		actions.addGroup(moveToGroupName, 2);
		actions.addAction(moveToGroupName, "Front", 1, () -> toFront());
		actions.addAction(moveToGroupName, "Back", 2, () -> toBack());
		actions.addAction(moveToGroupName, "Top", 3, () -> toTop());
		actions.addAction(moveToGroupName, "Bottom", 4, () -> toBottom());
		actions.addAction(moveToGroupName, "Right", 5, () -> toRight());
		actions.addAction(moveToGroupName, "Left", 6, () -> toLeft());

		actions.addGroup(viewGroupName, 3);
		boolean present = editor.isViewRegistered(SceneGraphView.class);
		actions.addCheckAction(viewGroupName, "Scene", 1, present, !present, () -> showView(SceneGraphView::new));
		present = editor.isViewRegistered(InspectorView.class);
		actions.addCheckAction(viewGroupName, "Inspector", 2, present, !present, () -> showView(InspectorView::new));
		present = editor.isViewRegistered(AssetsView.class);
		actions.addCheckAction(viewGroupName, "Assets", 3, present, !present, () -> showView(AssetsView::new));
	}

	private void toFront() {
		Camera camera = application.getCamera();
		camera.position.set(0, 0, 3);
		camera.direction.set(0, 0, -1);
		camera.up.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toBack() {
		Camera camera = application.getCamera();
		camera.position.set(0, 0, -3);
		camera.direction.set(0, 0, 1);
		camera.up.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toTop() {
		Camera camera = application.getCamera();
		camera.position.set(0, 3, 0);
		camera.direction.set(0, -1, 0);
		camera.up.set(0, 0, -1);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toBottom() {
		Camera camera = application.getCamera();
		camera.position.set(0, -3, 0);
		camera.direction.set(0, -1, 0);
		camera.up.set(0, 0, 1);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toRight() {
		Camera camera = application.getCamera();
		camera.position.set(3, 0, 0);
		camera.direction.set(-1, 0, 0);
		camera.up.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toLeft() {
		Camera camera = application.getCamera();
		camera.position.set(-3, 0, 0);
		camera.direction.set(1, 0, 0);
		camera.up.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void showView(BiConsumer<SceneEditor, Integer> constructor) {
		constructor.accept(editor, Integer.valueOf(SWT.LEFT));
	}

	@Override
	public void dispose() {
		EventService.unsubscribe(editor.id, this);
	}
}
