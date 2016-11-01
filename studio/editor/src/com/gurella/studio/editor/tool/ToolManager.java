package com.gurella.studio.editor.tool;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.input.InputService;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.input.PickResult;
import com.gurella.engine.scene.renderable.Layer;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.studio.editor.subscription.EditorActiveCameraProvider;
import com.gurella.studio.editor.subscription.EditorCameraSelectionListener;
import com.gurella.studio.editor.subscription.EditorFocusListener;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;

public class ToolManager extends InputAdapter
		implements EditorPreCloseListener, EditorFocusListener, EditorCameraSelectionListener {
	private final int editorId;

	private final ScaleTool scaleTool = new ScaleTool();
	private final TranslateTool translateTool = new TranslateTool();
	private final RotateTool rotateTool = new RotateTool();
	private final Environment environment;

	private TransformTool selected;

	private Camera camera;
	private final Vector3 translation = new Vector3();
	
	private final Vector3 intersection = new Vector3();
	private final Vector3 closestIntersection = new Vector3();

	private TransformComponent transformComponent;

	public ToolManager(int editorId) {
		this.editorId = editorId;

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.65f, 0.65f, 0.65f, 1f));
		environment.set(new DepthTestAttribute());
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		EventService.subscribe(editorId, this);
		InputService.addInputProcessor(this);
		EventService.post(editorId, EditorActiveCameraProvider.class, l -> camera = l.getActiveCamera());
	}

	@Override
	public void focusChanged(EditorFocusData focusData) {
		SceneNode2 node = focusData.focusedNode;
		transformComponent = node == null ? null : node.getComponent(TransformComponent.class);
	}

	@Override
	public void cameraChanged(Camera camera) {
		this.camera = camera;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.S) {
			selected = scaleTool;
			return true;
		} else if (keycode == Keys.T) {
			selected = translateTool;
			return true;
		} else if (keycode == Keys.R) {
			selected = rotateTool;
			return true;
		} else if (keycode == Keys.ESCAPE || keycode == Keys.M) {
			selected = null;
			return true;
		} else {
			return false;
		}
	}

	public void render(GenericBatch batch) {
		if (selected != null && transformComponent != null) {
			transformComponent.getWorldTranslation(translation);
			batch.setEnvironment(environment);
			selected.update(translation, camera);
			selected.render(translation, camera, batch);
		}
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if(selected == null) {
			return false;
		}
		
		selected.update(translation, camera);
		Vector3 cameraPosition = camera.position;
		Ray pickRay = camera.getPickRay(screenX, screenY);
		ToolHandle pick = selected.getIntersection(cameraPosition, pickRay, intersection);
		if(pick != null) {
			pick.changeColor(Color.YELLOW);
		}
		return false;
	}

	@Override
	public void onEditorPreClose() {
		InputService.removeInputProcessor(this);
		EventService.unsubscribe(editorId, this);
		scaleTool.dispose();
		translateTool.dispose();
		rotateTool.dispose();
	}
}
