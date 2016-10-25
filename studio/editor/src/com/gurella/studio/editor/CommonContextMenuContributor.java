package com.gurella.studio.editor;

import java.util.function.BiConsumer;

import org.eclipse.swt.SWT;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;
import com.gurella.studio.editor.assets.AssetsView;
import com.gurella.studio.editor.graph.SceneGraphView;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.subscription.EditorContextMenuContributor;

class CommonContextMenuContributor implements EditorContextMenuContributor, ApplicationShutdownListener {
	private static final String cameraGroupName = "Camera";
	private static final String moveToGroupName = "Move to";
	private static final String viewGroupName = "View";

	private final SceneEditor editor;
	private final SceneEditorApplicationListener applicationListener;

	CommonContextMenuContributor(SceneEditor editor) {
		this.editor = editor;
		this.applicationListener = editor.applicationListener;
		EventService.subscribe(editor.id, this);
	}

	@Override
	public void contribute(ContextMenuActions actions) {
		actions.addGroup(cameraGroupName, 1);
		actions.addCheckAction(cameraGroupName, "2d", 1, applicationListener.is2d(), () -> applicationListener.set2d());
		actions.addCheckAction(cameraGroupName, "3d", 2, applicationListener.is3d(), () -> applicationListener.set3d());

		actions.addGroup(moveToGroupName, 2);
		actions.addAction(moveToGroupName, "Front", 1, () -> toFront());
		actions.addAction(moveToGroupName, "Back", 2, () -> toBack());
		actions.addAction(moveToGroupName, "Top", 3, () -> toTop());
		actions.addAction(moveToGroupName, "Bottom", 4, () -> toBottom());
		actions.addAction(moveToGroupName, "Right", 5, () -> toRight());
		actions.addAction(moveToGroupName, "Left", 6, () -> toLeft());

		ViewRegistry views = editor.viewRegistry;
		actions.addGroup(viewGroupName, 3);
		boolean open = views.isOpen(SceneGraphView.class);
		actions.addCheckAction(viewGroupName, "Scene", 1, open, !open, () -> showView(SceneGraphView::new));
		open = views.isOpen(InspectorView.class);
		actions.addCheckAction(viewGroupName, "Inspector", 2, open, !open, () -> showView(InspectorView::new));
		open = views.isOpen(AssetsView.class);
		actions.addCheckAction(viewGroupName, "Assets", 3, open, !open, () -> showView(AssetsView::new));
	}

	private void toFront() {
		Camera camera = applicationListener.getCamera();
		camera.position.set(0, 0, 3);
		camera.direction.set(0, 0, -1);
		camera.up.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toBack() {
		Camera camera = applicationListener.getCamera();
		camera.position.set(0, 0, -3);
		camera.direction.set(0, 0, 1);
		camera.up.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toTop() {
		Camera camera = applicationListener.getCamera();
		camera.position.set(0, 3, 0);
		camera.direction.set(0, -1, 0);
		camera.up.set(0, 0, -1);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toBottom() {
		Camera camera = applicationListener.getCamera();
		camera.position.set(0, -3, 0);
		camera.direction.set(0, -1, 0);
		camera.up.set(0, 0, 1);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toRight() {
		Camera camera = applicationListener.getCamera();
		camera.position.set(3, 0, 0);
		camera.direction.set(-1, 0, 0);
		camera.up.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toLeft() {
		Camera camera = applicationListener.getCamera();
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
	public void shutdown() {
		if (Gdx.app.getApplicationListener() == applicationListener) {
			EventService.unsubscribe(editor.id, this);
		}
	}
}
