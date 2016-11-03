package com.gurella.studio.editor.focus;

import java.util.Optional;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.event.EventService;
import com.gurella.engine.plugin.Workbench;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.studio.editor.camera.CameraProvider;
import com.gurella.studio.editor.camera.CameraProviderExtension;
import com.gurella.studio.editor.common.bean.BeanEditor;
import com.gurella.studio.editor.common.bean.BeanEditorContext;
import com.gurella.studio.editor.inspector.component.ComponentInspectable;
import com.gurella.studio.editor.inspector.node.NodeInspectable;
import com.gurella.studio.editor.subscription.EditorFocusListener;
import com.gurella.studio.editor.subscription.EditorFocusListener.EditorFocusData;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;
import com.gurella.studio.editor.subscription.EditorPreRenderUpdateListener;
import com.gurella.studio.editor.subscription.EditorSelectionListener;
import com.gurella.studio.editor.subscription.SceneLoadedListener;
import com.gurella.studio.editor.utils.GestureDetectorPlugin;

public class FocusManager implements SceneLoadedListener, EditorSelectionListener, EditorPreCloseListener,
		EditorPreRenderUpdateListener, CameraProviderExtension {
	private final int editorId;
	private final GestureDetectorPlugin gestureDetector = new GestureDetectorPlugin(new FocusTapListener());

	private Scene scene;
	private CameraProvider cameraProvider;

	private SceneNode2 focusedNode;
	private SceneNodeComponent2 focusedComponent;
	private Control lastFocusControl;
	private boolean focusDataFromSelection;

	private final Ray pickRay = new Ray();
	private final Vector3 intersection = new Vector3();
	private final Array<Spatial> spatials = new Array<>(64);

	public FocusManager(int editorId) {
		this.editorId = editorId;
		EventService.subscribe(editorId, this);
		Workbench.activate(this);
		Workbench.activate(gestureDetector);
	}

	@Override
	public void sceneLoaded(Scene scene) {
		this.scene = scene;
		EventService.subscribe(scene.getInstanceId(), this);
	}

	@Override
	public void onPreRenderUpdate() {
		Display current = Display.getCurrent();
		if (current == null) {
			return;
		}

		Control focusControl = current.getFocusControl();
		if (focusControl == lastFocusControl && focusDataFromSelection) {
			return;
		}

		lastFocusControl = focusControl;
		focusDataFromSelection = false;

		if (focusControl == null) {
			return;
		}

		Composite temp = focusControl instanceof Composite ? (Composite) focusControl : focusControl.getParent();
		while (temp != null) {
			if (temp instanceof BeanEditor) {
				BeanEditorContext<?> context = ((BeanEditor<?>) temp).getContext();
				Object modelInstance = context.modelInstance;
				if (modelInstance instanceof SceneNodeComponent2) {
					focusedComponent = (SceneNodeComponent2) modelInstance;
					focusedNode = focusedComponent == null ? null : focusedComponent.getNode();
					focusChanged();
					return;
				} else if (modelInstance instanceof SceneNode2) {
					focusedComponent = null;
					focusedNode = (SceneNode2) modelInstance;
					focusChanged();
					return;
				}
			}
			temp = temp.getParent();
		}
	}

	@Override
	public void selectionChanged(Object selection) {
		if (selection instanceof NodeInspectable) {
			focusedNode = ((NodeInspectable) selection).target;
			focusedComponent = null;
			focusDataFromSelection = true;
		} else if (selection instanceof ComponentInspectable) {
			focusedComponent = ((ComponentInspectable) selection).target;
			focusedNode = focusedComponent == null ? null : focusedComponent.getNode();
			focusDataFromSelection = true;
		} else {
			focusedComponent = null;
			focusedNode = null;
			focusDataFromSelection = true;
		}

		focusChanged();
	}

	private void focusChanged() {
		EditorFocusData focusData = new EditorFocusData(focusedNode, focusedComponent);
		EventService.post(editorId, EditorFocusListener.class, l -> l.focusChanged(focusData));
	}

	private void updateSelection(float x, float y) {
		Camera camera = getCamera();
		if (scene == null || camera == null) {
			return;
		}

		camera.update(true);
		pickRay.set(camera.getPickRay(x, y));
		scene.spatialSystem.getSpatials(pickRay, spatials, null);
		if (spatials.size == 0) {
			if (!focusDataFromSelection) {
				focusedNode = null;
				focusedComponent = null;
				focusChanged();
			}
			return;
		}

		Vector3 cameraPosition = camera.position;
		Spatial closestSpatial = null;
		float closestDistance = Float.MAX_VALUE;

		for (int i = 0; i < spatials.size; i++) {
			Spatial spatial = spatials.get(i);
			RenderableComponent renderableComponent = spatial.renderableComponent;
			if (renderableComponent.getIntersection(pickRay, intersection)) {
				float distance = intersection.dst2(cameraPosition);
				if (closestDistance > distance) {
					closestDistance = distance;
					closestSpatial = spatial;
					// TODO Z order of sprites
				}
			}
		}

		spatials.clear();
		if (closestSpatial != null) {
			focusDataFromSelection = false;
			focusedComponent = closestSpatial.renderableComponent;
			focusedNode = focusedComponent.getNode();
		} else if (!focusDataFromSelection) {
			focusedNode = null;
			focusedComponent = null;
		}

		focusChanged();
	}

	@Override
	public void setCameraProvider(CameraProvider cameraProvider) {
		this.cameraProvider = cameraProvider;
	}

	private Camera getCamera() {
		return cameraProvider == null ? null : cameraProvider.getCamera();
	}

	@Override
	public void onEditorPreClose() {
		EventService.unsubscribe(editorId, this);
		Optional.ofNullable(scene).ifPresent(s -> EventService.unsubscribe(s.getInstanceId(), this));
		Workbench.activate(this);
		Workbench.deactivate(gestureDetector);
	}

	private class FocusTapListener extends GestureAdapter {
		@Override
		public boolean tap(float x, float y, int count, int button) {
			if (count != 1 || button != Buttons.LEFT) {
				return false;
			}

			updateSelection(x, y);
			return true;
		}
	}
}
