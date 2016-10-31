package com.gurella.studio.editor.camera;

import static com.gurella.studio.editor.subscription.EditorCameraSwitch.CameraType.camera2d;
import static com.gurella.studio.editor.subscription.EditorCameraSwitch.CameraType.camera3d;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.gurella.engine.event.EventService;
import com.gurella.studio.editor.menu.ContextMenuActions;
import com.gurella.studio.editor.subscription.EditorContextMenuContributor;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;

public class CameraMenuContributor implements EditorPreCloseListener, EditorContextMenuContributor {
	private static final String cameraMenuGroupName = "Camera";
	private static final String moveToMenuGroupName = "Move to";

	private final int editorId;
	private final CameraManager manager;

	public CameraMenuContributor(int editorId, CameraManager manager) {
		this.editorId = editorId;
		this.manager = manager;
		EventService.subscribe(editorId, this);
	}

	@Override
	public void contribute(ContextMenuActions actions) {
		actions.addGroup(cameraMenuGroupName, -800);
		boolean is2d = manager.is2d();
		actions.addCheckAction(cameraMenuGroupName, "2d", 100, !is2d, is2d, () -> manager.switchCamera(camera2d));
		boolean is3d = manager.is3d();
		actions.addCheckAction(cameraMenuGroupName, "3d", 200, !is3d, is3d, () -> manager.switchCamera(camera3d));

		actions.addGroup(moveToMenuGroupName, -700);
		if (is2d) {
			actions.addAction(moveToMenuGroupName, "Origin", 100, () -> moveTo(0, 0, 0, 0, 0, -1));
			actions.addAction(moveToMenuGroupName, "Restore rotation", 100, () -> setRotation(0));
		} else {
			actions.addAction(moveToMenuGroupName, "Front", 100, () -> moveTo(0, 0, 3, 0, 0, -1));
			actions.addAction(moveToMenuGroupName, "Back", 200, () -> moveTo(0, 0, -3, 0, 0, 1));
			actions.addAction(moveToMenuGroupName, "Top", 300, () -> moveTo(0, 3, 0, 0, -1, 0));
			actions.addAction(moveToMenuGroupName, "Bottom", 400, () -> moveTo(0, -3, 0, 0, -1, 0));
			actions.addAction(moveToMenuGroupName, "Right", 500, () -> moveTo(3, 0, 0, -1, 0, 0));
			actions.addAction(moveToMenuGroupName, "Left", 600, () -> moveTo(-3, 0, 0, 1, 0, 0));
		}
	}

	private void moveTo(float px, float py, float pz, float lx, float ly, float lz) {
		Camera camera = manager.getActiveCamera();
		camera.position.set(px, py, pz);
		camera.direction.set(lx, ly, lz);
		camera.up.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}
	
	private void setRotation(float rotation) {
		OrthographicCamera camera = (OrthographicCamera) manager.getActiveCamera();
		camera.direction.set(0, 0, -1);
		camera.up.set(0, 1, 0);
		camera.rotate(rotation);
		camera.update(true);
	}

	@Override
	public void onEditorPreClose() {
		EventService.unsubscribe(editorId, this);
	}
}
