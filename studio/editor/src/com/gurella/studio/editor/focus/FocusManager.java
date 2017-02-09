package com.gurella.studio.editor.focus;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.renderable.RenderableIntersector;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.utils.plugin.Workbench;
import com.gurella.studio.editor.SceneConsumer;
import com.gurella.studio.editor.camera.CameraConsumer;
import com.gurella.studio.editor.inspector.component.ComponentInspectable;
import com.gurella.studio.editor.inspector.node.NodeInspectable;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.subscription.EditorFocusListener;
import com.gurella.studio.editor.subscription.EditorFocusListener.EditorFocusData;
import com.gurella.studio.editor.subscription.EditorPreRenderUpdateListener;
import com.gurella.studio.editor.subscription.EditorSelectionListener;
import com.gurella.studio.editor.ui.bean.BeanEditor;
import com.gurella.studio.editor.ui.bean.BeanEditorContext;
import com.gurella.studio.editor.utils.GestureDetectorPlugin;
import com.gurella.studio.gdx.GdxContext;

public class FocusManager implements SceneConsumer, EditorSelectionListener, EditorCloseListener,
		EditorPreRenderUpdateListener, CameraConsumer {
	private final int editorId;
	private final GestureDetectorPlugin gestureDetector = new GestureDetectorPlugin(new FocusTapListener());

	private Scene scene;
	private Camera camera;

	private SceneNode focusedNode;
	private SceneNodeComponent focusedComponent;
	private Control lastFocusControl;
	private boolean focusDataFromSelection;

	private final RenderableIntersector intersector = new RenderableIntersector();
	private final Array<Spatial> spatials = new Array<>(64);

	public FocusManager(int editorId) {
		this.editorId = editorId;
		GdxContext.subscribe(editorId, editorId, this);
		Workbench.activate(editorId, this);
		Workbench.activate(editorId, gestureDetector);
	}

	@Override
	public void setScene(Scene scene) {
		this.scene = scene;
	}

	@Override
	public void onPreRenderUpdate() {
		Display current = Display.getCurrent();
		if (current == null) {
			return;
		}

		Control control = current.getFocusControl();
		if (control == lastFocusControl && focusDataFromSelection) {
			return;
		}

		lastFocusControl = control;
		focusDataFromSelection = false;

		if (control == null) {
			return;
		}

		while (control != null) {
			if (control instanceof BeanEditor) {
				BeanEditorContext<?> context = ((BeanEditor<?>) control).getContext();
				Object bean = context.bean;
				if (bean instanceof SceneNodeComponent) {
					focusedComponent = (SceneNodeComponent) bean;
					focusedNode = focusedComponent.getNode();
					focusChanged();
					return;
				} else if (bean instanceof SceneNode) {
					focusedComponent = null;
					focusedNode = (SceneNode) bean;
					focusChanged();
					return;
				}
			}
			control = control.getParent();
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
			focusedNode = focusedComponent.getNode();
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
		GdxContext.post(editorId, editorId, EditorFocusListener.class, l -> l.focusChanged(focusData));
	}

	private void updateSelection(float x, float y) {
		if (scene == null || camera == null) {
			return;
		}

		camera.update(true);
		Ray pickRay = camera.getPickRay(x, y);
		scene.spatialSystem.getSpatials(pickRay, spatials, null);
		if (spatials.size == 0) {
			if (!focusDataFromSelection) {
				focusedNode = null;
				focusedComponent = null;
				focusChanged();
			}
			return;
		}

		intersector.set(camera, pickRay);
		Spatial closestSpatial = null;
		for (int i = 0, n = spatials.size; i < n; i++) {
			Spatial spatial = spatials.get(i);
			if (intersector.append(spatial.renderable)) {
				closestSpatial = spatial;
			}
		}

		intersector.reset();
		spatials.clear();

		if (closestSpatial != null) {
			focusDataFromSelection = false;
			focusedComponent = closestSpatial.renderable;
			focusedNode = focusedComponent.getNode();
		} else if (!focusDataFromSelection) {
			focusedNode = null;
			focusedComponent = null;
		}

		focusChanged();
	}

	@Override
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	@Override
	public void onEditorClose() {
		GdxContext.unsubscribe(editorId, editorId, this);
		Workbench.activate(editorId, this);
		Workbench.deactivate(editorId, gestureDetector);
	}

	private class FocusTapListener extends GestureAdapter {
		@Override
		public boolean tap(float x, float y, int count, int button) {
			if (count == 1 && button == Buttons.LEFT) {
				updateSelection(x, y);
			}
			return false;
		}
	}
}
