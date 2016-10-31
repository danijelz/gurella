package com.gurella.studio.editor.tool;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.input.InputService;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.studio.editor.subscription.EditorActiveCameraProvider;
import com.gurella.studio.editor.subscription.EditorCameraSelectionListener;
import com.gurella.studio.editor.subscription.EditorFocusListener;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;

public class ToolManager extends InputAdapter
		implements EditorPreCloseListener, EditorFocusListener, EditorCameraSelectionListener {
	private final int editorId;

	private EditorFocusData focusData = new EditorFocusData(null, null);

	ScaleTool scaleTool = new ScaleTool();
	SelectionTool selected;

	private Camera camera;

	private final Vector3 temp = new Vector3();

	public ToolManager(int editorId) {
		this.editorId = editorId;
		EventService.subscribe(editorId, this);
		InputService.addInputProcessor(this);
		EventService.post(editorId, EditorActiveCameraProvider.class, l -> camera = l.getActiveCamera());
	}

	@Override
	public void focusChanged(EditorFocusData focusData) {
		this.focusData = focusData;
		selected = null;
	}

	@Override
	public void cameraChanged(Camera camera) {
		this.camera = camera;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.S && focusData.focusedComponent instanceof RenderableComponent) {
			initTranslation();
			scaleTool.init(temp, camera);
			return true;
		} else if (keycode == Keys.ESCAPE) {
			selected = null;
			return true;
		} else {
			return false;
		}
	}

	private void initTranslation() {
		RenderableComponent renderableComponent = (RenderableComponent) focusData.focusedComponent;
		selected = scaleTool;
		TransformComponent transform = renderableComponent.getTransformComponent();
		if (transform == null) {
			temp.set(0, 0, 0);
		} else {
			transform.getWorldTranslation(temp);
		}
	}

	public void render(GenericBatch batch) {
		if (selected != null) {
			initTranslation();
			scaleTool.render(temp, camera, batch);
		}
	}

	@Override
	public void onEditorPreClose() {
		InputService.removeInputProcessor(this);
		EventService.unsubscribe(editorId, this);
		scaleTool.dispose();
	}
}
