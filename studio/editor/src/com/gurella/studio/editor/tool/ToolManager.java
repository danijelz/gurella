package com.gurella.studio.editor.tool;

import static com.badlogic.gdx.Input.Buttons.LEFT;

import java.util.Optional;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.math.ModelIntesector;
import com.gurella.engine.plugin.Workbench;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.utils.priority.Priority;
import com.gurella.studio.editor.camera.CameraProviderExtension;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.subscription.EditorFocusListener;
import com.gurella.studio.editor.subscription.ToolSelectionListener;

@Priority(Integer.MIN_VALUE)
public class ToolManager extends InputAdapter
		implements EditorCloseListener, EditorFocusListener, CameraProviderExtension {
	final int editorId;

	private final TranslateTool translateTool;
	private final RotateTool rotateTool;
	private final ScaleTool scaleTool;

	@SuppressWarnings("unused")
	private ToolMenuContributor menuContributor;

	private final Environment environment = new Environment();

	private final Vector3 intersection = new Vector3();
	private final ModelIntesector modelIntesector = new ModelIntesector();

	private Camera camera;
	private TransformComponent transform;

	private TransformTool selectedTool;
	private ToolHandle focusedHandle;

	public ToolManager(int editorId) {
		this.editorId = editorId;

		translateTool = new TranslateTool(editorId);
		rotateTool = new RotateTool(editorId);
		scaleTool = new ScaleTool(editorId);

		menuContributor = new ToolMenuContributor(editorId, this);

		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.65f, 0.65f, 0.65f, 1f));
		environment.set(new DepthTestAttribute());
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		EventService.subscribe(editorId, this);
		Workbench.activate(editorId, this);
	}

	@Override
	public void focusChanged(EditorFocusData focusData) {
		SceneNode node = focusData.focusedNode;
		transform = node == null ? null : node.getComponent(TransformComponent.class);
		translateTool.transform = transform;
		rotateTool.transform = transform;
		scaleTool.transform = transform;
	}

	@Override
	public void setCamera(Camera camera) {
		this.camera = camera;
		deactivate();
		translateTool.camera = camera;
		rotateTool.camera = camera;
		scaleTool.camera = camera;
	}

	private boolean isActive() {
		return selectedTool != null && selectedTool.isActive();
	}

	private void deactivate() {
		Optional.ofNullable(selectedTool).ifPresent(t -> t.deactivate());
	}

	@Override
	public boolean keyDown(int keycode) {
		return keycode == Keys.S || keycode == Keys.T || keycode == Keys.R || keycode == Keys.ESCAPE
				|| keycode == Keys.N;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (isActive() && (keycode == Keys.ESCAPE || keycode == Keys.N)) {
			deactivate();
			return true;
		}

		switch (keycode) {
		case Keys.S:
			selectTool(scaleTool);
			return true;
		case Keys.T:
			selectTool(translateTool);
			return true;
		case Keys.R:
			selectTool(rotateTool);
			return true;
		case Keys.ESCAPE:
		case Keys.N:
			selectTool((TransformTool) null);
			return true;
		default:
			return false;
		}
	}

	void selectTool(ToolType type) {
		switch (type) {
		case none:
			selectTool((TransformTool) null);
			break;
		case rotate:
			selectTool(rotateTool);
			break;
		case translate:
			selectTool(translateTool);
			break;
		case scale:
			selectTool(scaleTool);
			break;
		default:
			selectTool((TransformTool) null);
			break;
		}
	}

	ToolType getSelectedToolType() {
		return selectedTool == null ? ToolType.none : selectedTool.getType();
	}

	private void selectTool(TransformTool newSelection) {
		if (selectedTool == newSelection) {
			return;
		}

		deactivate();
		selectedTool = newSelection;
		ToolType type = selectedTool == null ? ToolType.none : selectedTool.getType();
		EventService.post(editorId, ToolSelectionListener.class, l -> l.toolSelected(type));
	}

	public void render(GenericBatch batch) {
		if (camera == null || selectedTool == null || transform == null) {
			return;
		}

		batch.setEnvironment(environment);
		selectedTool.render(batch);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		deactivate();

		if (pointer != 0 || button != LEFT || selectedTool == null || transform == null || camera == null) {
			return false;
		}

		return Optional.ofNullable(pickHandle(screenX, screenY)).map(h -> activate(h)).isPresent();
	}

	private ToolHandle activate(ToolHandle handle) {
		selectedTool.activate(handle);
		return handle;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (camera == null || !isActive()) {
			return false;
		}

		if (pointer != 0) {
			deactivate();
			return false;
		} else {
			selectedTool.commit();
			return true;
		}
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (camera == null || !isActive()) {
			return false;
		}

		if (pointer != 0) {
			deactivate();
			return false;
		} else {
			selectedTool.dragged(screenX, screenY);
			return true;
		}
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (selectedTool == null || transform == null) {
			return false;
		}

		ToolHandle handle = pickHandle(screenX, screenY);
		if (focusedHandle != handle) {
			if (focusedHandle != null) {
				focusedHandle.focusLost();
			}

			focusedHandle = handle;
			if (handle != null) {
				handle.focusGained();
			}
		}

		return false;
	}

	protected ToolHandle pickHandle(int screenX, int screenY) {
		if (camera == null) {
			return null;
		}

		selectedTool.update();

		Vector3 cameraPosition = camera.position;
		Ray pickRay = camera.getPickRay(screenX, screenY);
		float closestDistance = Float.MAX_VALUE;
		ToolHandle pick = null;

		for (ToolHandle toolHandle : selectedTool.getHandles()) {
			ModelInstance instance = toolHandle.modelInstance;
			if (modelIntesector.getIntersection(cameraPosition, pickRay, instance, intersection)) {
				float distance = intersection.dst2(cameraPosition);
				if (closestDistance > distance) {
					closestDistance = distance;
					pick = toolHandle;
				}
			}
		}

		return pick;
	}

	@Override
	public void onEditorClose() {
		Workbench.deactivate(editorId, this);
		EventService.unsubscribe(editorId, this);
		scaleTool.dispose();
		translateTool.dispose();
		rotateTool.dispose();
	}
}
