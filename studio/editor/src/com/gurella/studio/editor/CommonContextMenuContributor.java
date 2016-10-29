package com.gurella.studio.editor;

import org.eclipse.swt.SWT;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.gurella.engine.event.EventService;
import com.gurella.studio.editor.assets.AssetsView;
import com.gurella.studio.editor.control.DockableView;
import com.gurella.studio.editor.graph.SceneGraphView;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.subscription.EditorActiveCameraProvider;
import com.gurella.studio.editor.subscription.EditorCameraChangedListener;
import com.gurella.studio.editor.subscription.EditorCameraSwitch;
import com.gurella.studio.editor.subscription.EditorCameraSwitch.CameraType;
import com.gurella.studio.editor.subscription.EditorContextMenuContributor;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;

class CommonContextMenuContributor
		implements EditorContextMenuContributor, EditorCameraChangedListener, EditorPreCloseListener {
	private static final String cameraGroupName = "Camera";
	private static final String moveToGroupName = "Move to";
	private static final String viewGroupName = "View";

	private final int editorId;
	private final SceneEditor editor;
	private final SceneEditorUndoContext undoContext;
	private final ViewRegistry viewRegistry;

	private Camera camera;

	CommonContextMenuContributor(SceneEditor editor) {
		editorId = editor.id;
		this.editor = editor;
		this.undoContext = editor.undoContext;
		this.viewRegistry = editor.viewRegistry;

		EventService.subscribe(editor.id, this);
		EventService.post(editorId, EditorActiveCameraProvider.class, l -> camera = l.getActiveCamera());
	}

	@Override
	public void contribute(ContextMenuActions actions) {
		actions.addAction("Undo", -1000, undoContext.canUndo(), undoContext::undo);
		actions.addAction("Redo", -900, undoContext.canRedo(), undoContext::redo);

		actions.addGroup(cameraGroupName, -800);
		boolean is2dCamera = camera instanceof OrthographicCamera;
		actions.addCheckAction(cameraGroupName, "2d", 100, is2dCamera, () -> switchCamera(CameraType.camera2d));
		boolean is3dCamera = camera instanceof PerspectiveCamera;
		actions.addCheckAction(cameraGroupName, "3d", 200, is3dCamera, () -> switchCamera(CameraType.camera2d));

		actions.addGroup(moveToGroupName, -700);
		actions.addAction(moveToGroupName, "Front", 100, () -> toFront());
		actions.addAction(moveToGroupName, "Back", 200, () -> toBack());
		actions.addAction(moveToGroupName, "Top", 300, () -> toTop());
		actions.addAction(moveToGroupName, "Bottom", 400, () -> toBottom());
		actions.addAction(moveToGroupName, "Right", 500, () -> toRight());
		actions.addAction(moveToGroupName, "Left", 600, () -> toLeft());

		ViewRegistry views = editor.viewRegistry;
		actions.addGroup(viewGroupName, -600);
		boolean open = views.isOpen(SceneGraphView.class);
		actions.addCheckAction(viewGroupName, "Scene", 100, !open, open, () -> openView(SceneGraphView.class));
		open = views.isOpen(InspectorView.class);
		actions.addCheckAction(viewGroupName, "Inspector", 200, !open, open, () -> openView(InspectorView.class));
		open = views.isOpen(AssetsView.class);
		actions.addCheckAction(viewGroupName, "Assets", 300, !open, open, () -> openView(AssetsView.class));
	}

	private void switchCamera(CameraType cameraType) {
		EventService.post(editorId, EditorCameraSwitch.class, l -> l.switchCamera(cameraType));
	}

	private void toFront() {
		EventService.post(editorId, EditorActiveCameraProvider.class, l -> camera = l.getActiveCamera());
		camera.position.set(0, 0, 3);
		camera.direction.set(0, 0, -1);
		camera.up.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toBack() {
		camera.position.set(0, 0, -3);
		camera.direction.set(0, 0, 1);
		camera.up.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toTop() {
		camera.position.set(0, 3, 0);
		camera.direction.set(0, -1, 0);
		camera.up.set(0, 0, -1);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toBottom() {
		camera.position.set(0, -3, 0);
		camera.direction.set(0, -1, 0);
		camera.up.set(0, 0, 1);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toRight() {
		camera.position.set(3, 0, 0);
		camera.direction.set(-1, 0, 0);
		camera.up.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toLeft() {
		camera.position.set(-3, 0, 0);
		camera.direction.set(1, 0, 0);
		camera.up.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void openView(Class<? extends DockableView> type) {
		viewRegistry.openView(type, SWT.LEFT);
	}

	@Override
	public void cameraChanged(Camera camera) {
		this.camera = camera;
	}

	@Override
	public void onEditorPreClose() {
		EventService.unsubscribe(editor.id, this);
	}
}
