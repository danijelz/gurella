package com.gurella.studio.editor.camera;

import static com.gurella.studio.editor.camera.CameraType.camera2d;
import static com.gurella.studio.editor.camera.CameraType.camera3d;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.gurella.engine.event.EventService;
import com.gurella.engine.plugin.Workbench;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.menu.ContextMenuActions;
import com.gurella.studio.editor.menu.EditorContextMenuContributor;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.utils.UiUtils;

public class CameraMenuContributor implements EditorCloseListener, EditorContextMenuContributor {
	private static final String cameraMenuSectionName = "Camera";
	private static final String cameraMenuGroupName = "&Camera";
	private static final String moveToMenuGroupName = "&Navigate";

	private final int editorId;
	private final CameraManager manager;

	private final Matrix4 lookAt = new Matrix4();
	private final Quaternion rotation = new Quaternion();

	public CameraMenuContributor(int editorId, CameraManager manager) {
		this.editorId = editorId;
		this.manager = manager;
		EventService.subscribe(editorId, this);
		Workbench.activate(editorId, this);
	}

	@Override
	public void contribute(float x, float y, ContextMenuActions actions) {
		actions.addSection(cameraMenuSectionName, 100);

		actions.addGroup(cameraMenuSectionName, cameraMenuGroupName, -800);
		boolean is2d = manager.is2d();
		actions.addCheckAction(cameraMenuGroupName, "&2d\t2", 100, !is2d, is2d, () -> manager.switchCamera(camera2d));
		boolean is3d = manager.is3d();
		actions.addCheckAction(cameraMenuGroupName, "&3d\t3", 200, !is3d, is3d, () -> manager.switchCamera(camera3d));

		actions.addGroup(cameraMenuSectionName, moveToMenuGroupName, -700);
		if (is2d) {
			actions.addAction(moveToMenuGroupName, "&Origin", 100, () -> moveTo(0, 0, 0, 0, 0, -1, 0, 1, 0));
			actions.addAction(moveToMenuGroupName, "Restore &rotation", 100, () -> setRotation(0));
			actions.addAction(moveToMenuGroupName, "Restore &zoom", 100, () -> setZoom(1));
			actions.addAction(moveToMenuGroupName, "Ro&tation", 100, () -> selectRotation());
			actions.addAction(moveToMenuGroupName, "Zoo&m", 100, () -> selectZoom());
		} else {
			actions.addAction(moveToMenuGroupName, "&Front", 100, () -> moveTo(0, 0, 3, 0, 0, -1, 0, 1, 0));
			actions.addAction(moveToMenuGroupName, "&Back", 200, () -> moveTo(0, 0, -3, 0, 0, 1, 0, 1, 0));
			actions.addAction(moveToMenuGroupName, "&Top", 300, () -> moveTo(0, 3, 0, 0, -1, 0, 0, 0, -1));
			actions.addAction(moveToMenuGroupName, "Bo&ttom", 400, () -> moveTo(0, -3, 0, 0, -1, 0, 0, 0, 1));
			actions.addAction(moveToMenuGroupName, "&Right", 500, () -> moveTo(3, 0, 0, -1, 0, 0, 0, 1, 0));
			actions.addAction(moveToMenuGroupName, "&Left", 600, () -> moveTo(-3, 0, 0, 1, 0, 0, 0, 1, 0));
		}
	}

	private void moveTo(float px, float py, float pz, float lx, float ly, float lz, float ux, float uy, float uz) {
		Camera camera = manager.getCamera();
		camera.position.set(px, py, pz);
		camera.direction.set(lx, ly, lz);
		camera.up.set(ux, uy, uz);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void selectRotation() {
		Shell shell = UiUtils.getDisplay().getActiveShell();
		Camera camera = manager.getCamera();
		lookAt.setToLookAt(camera.direction, camera.up);
		lookAt.getRotation(rotation);
		String rotationZ = String.valueOf(rotation.getRoll());
		String message = "Please enter rotation in degrees";
		InputDialog dlg = new InputDialog(shell, "Rotation", message, rotationZ, s -> validateRotation(s));
		if (dlg.open() == Window.OK) {
			setRotation(Float.parseFloat(dlg.getValue()));
		}
	}

	private static String validateRotation(String newText) {
		if (Values.isBlank(newText)) {
			return "Value must not be empty.";
		}

		try {
			Float.parseFloat(newText);
			return null;
		} catch (Exception e) {
			return "Rotation must be float value.";
		}
	}

	private void setRotation(float rotation) {
		OrthographicCamera camera = (OrthographicCamera) manager.getCamera();
		camera.direction.set(0, 0, -1);
		camera.up.set(0, 1, 0);
		camera.rotate(rotation);
		camera.update(true);
	}

	private void selectZoom() {
		Shell shell = UiUtils.getDisplay().getActiveShell();
		String zoom = String.valueOf(((OrthographicCamera) manager.getCamera()).zoom);
		String message = "Please enter new zoom value";
		InputDialog dlg = new InputDialog(shell, "Zoom", message, zoom, s -> validateZoom(s));
		if (dlg.open() == Window.OK) {
			setZoom(Float.parseFloat(dlg.getValue()));
		}
	}

	private static String validateZoom(String newText) {
		if (Values.isBlank(newText)) {
			return "Value must not be empty.";
		}

		try {
			Float.parseFloat(newText);
			return null;
		} catch (Exception e) {
			return "Zoom must be float value.";
		}
	}

	private void setZoom(float zoom) {
		OrthographicCamera camera = (OrthographicCamera) manager.getCamera();
		camera.zoom = zoom;
		camera.update();
	}

	@Override
	public void onEditorClose() {
		Workbench.deactivate(editorId, this);
		EventService.unsubscribe(editorId, this);
	}
}
