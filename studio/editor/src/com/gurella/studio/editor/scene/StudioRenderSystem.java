package com.gurella.studio.editor.scene;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.graphics.GenericBatch;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.camera.CameraComponent;
import com.gurella.engine.scene.debug.DebugRenderable;
import com.gurella.engine.scene.layer.Layer;
import com.gurella.engine.scene.layer.Layer.LayerOrdinalComparator;
import com.gurella.engine.scene.layer.LayerMask;
import com.gurella.engine.scene.light.DirectionalLightComponent;
import com.gurella.engine.scene.light.PointLightComponent;
import com.gurella.engine.scene.light.SpotLightComponent;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.studio.editor.EditorMessageListener;
import com.gurella.studio.editor.model.ModelEditor;
import com.gurella.studio.editor.model.ModelEditorContext;
import com.gurella.studio.editor.scene.SceneHierarchyView.ComponentInspectable;
import com.gurella.studio.editor.scene.SceneHierarchyView.NodeInspectable;

public class StudioRenderSystem implements ComponentActivityListener, EditorMessageListener, Disposable {
	private Scene scene;

	private GenericBatch batch;
	private Array<Layer> orderedLayers = new Array<Layer>();
	private IntMap<Array<CameraComponent<?>>> camerasByLayer = new IntMap<Array<CameraComponent<?>>>();
	private IntMap<Array<DebugRenderable>> debugRenderablesByNode = new IntMap<Array<DebugRenderable>>();

	private final Environment environment = new Environment();
	private final ColorAttribute ambientLight = new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f);
	private final DepthTestAttribute depthTest = new DepthTestAttribute();
	private final DirectionalLightsAttribute directionalLights = new DirectionalLightsAttribute();
	private final PointLightsAttribute pointLights = new PointLightsAttribute();
	private final SpotLightsAttribute spotLights = new SpotLightsAttribute();

	private final LayerMask layerMask = new LayerMask();
	private final Array<Spatial> tempSpatials = new Array<Spatial>(256);

	SceneNode2 selectedNode;

	public StudioRenderSystem(Scene scene) {
		this.scene = scene;
		layerMask.allowed(Layer.DEFAULT);
		batch = new GenericBatch();

		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
		environment.set(depthTest);
		environment.set(directionalLights);
		environment.set(pointLights);
		environment.set(spotLights);

		directionalLights.lights.add(new DirectionalLight().set(0.6f, 0.6f, 0.6f, -1f, -0.8f, -0.2f));
	}

	@Override
	public void componentActivated(SceneNodeComponent2 component) {
		if (component instanceof CameraComponent) {
			CameraComponent<?> cameraComponent = (CameraComponent<?>) component;
			boolean layersUpdated = false;

			if (cameraComponent.renderingLayers.size > 0) {
				for (Layer layer : cameraComponent.renderingLayers) {
					layersUpdated |= addCameraComponent(layer, cameraComponent);
				}
			} else {
				layersUpdated |= addCameraComponent(Layer.DEFAULT, cameraComponent);
			}

			if (layersUpdated) {
				orderedLayers.sort(LayerOrdinalComparator.instance);
			}
		} else if (component instanceof DebugRenderable) {
			int nodeId = component.getNodeId();
			Array<DebugRenderable> renderables = debugRenderablesByNode.get(nodeId);
			if (renderables == null) {
				renderables = new Array<>();
				debugRenderablesByNode.put(nodeId, renderables);
			}
			renderables.add((DebugRenderable) component);
		} else if (component instanceof DirectionalLightComponent) {
			directionalLights.lights.add(((DirectionalLightComponent) component).getLight());
		} else if (component instanceof PointLightComponent) {
			pointLights.lights.add(((PointLightComponent) component).getLight());
		} else if (component instanceof SpotLightComponent) {
			spotLights.lights.add(((SpotLightComponent) component).getLight());
		}
	}

	private boolean addCameraComponent(Layer layer, CameraComponent<?> cameraComponent) {
		int layerId = layer.id;
		if (!camerasByLayer.containsKey(layerId)) {
			camerasByLayer.put(layerId, new Array<CameraComponent<?>>());
		}

		Array<CameraComponent<?>> layerCameras = camerasByLayer.get(layerId);
		layerCameras.add(cameraComponent);
		layerCameras.sort();
		return addLayer(layer);
	}

	private boolean addLayer(Layer layer) {
		if (mustAddLayer(layer)) {
			orderedLayers.add(layer);
			return true;
		} else {
			return false;
		}
	}

	private boolean mustAddLayer(Layer layer) {
		int layerId = layer.id;
		return camerasByLayer.containsKey(layerId) && !orderedLayers.contains(layer, true);
	}

	@Override
	public void componentDeactivated(SceneNodeComponent2 component) {
		if (component instanceof CameraComponent) {
			CameraComponent<?> cameraComponent = (CameraComponent<?>) component;
			boolean layersUpdated = false;
			for (Layer layer : cameraComponent.renderingLayers) {
				layersUpdated |= removeCameraComponent(layer, cameraComponent);
			}

			if (layersUpdated) {
				orderedLayers.sort(LayerOrdinalComparator.instance);
			}
		} else if (component instanceof DebugRenderable) {
			int nodeId = component.getNodeId();
			Array<DebugRenderable> renderables = debugRenderablesByNode.get(nodeId);
			if (renderables == null) {
				return;
			}

			renderables.removeValue((DebugRenderable) component, true);
			if (renderables.size < 1) {
				debugRenderablesByNode.remove(nodeId);
			}
		} else if (component instanceof DirectionalLightComponent) {
			directionalLights.lights.removeValue(((DirectionalLightComponent) component).getLight(), true);
		} else if (component instanceof PointLightComponent) {
			pointLights.lights.removeValue(((PointLightComponent) component).getLight(), true);
		} else if (component instanceof SpotLightComponent) {
			spotLights.lights.removeValue(((SpotLightComponent) component).getLight(), true);
		}
	}

	private boolean removeCameraComponent(Layer layer, CameraComponent<?> cameraComponent) {
		int layerId = layer.id;
		Array<CameraComponent<?>> layerCameras = camerasByLayer.get(layerId);
		layerCameras.removeValue(cameraComponent, true);

		if (layerCameras.size < 1) {
			camerasByLayer.remove(layerId);
			orderedLayers.removeValue(layer, true);
			return true;
		} else {
			layerCameras.sort();
			return false;
		}
	}

	public void renderScene(Camera camera) {
		batch.begin(camera);
		batch.setEnvironment(environment);
		scene.spatialPartitioningSystem.getSpatials(camera.frustum, tempSpatials, layerMask);
		SceneNodeComponent2 focusedComponent = findFocusedComponent();

		for (int i = 0; i < tempSpatials.size; i++) {
			Spatial spatial = tempSpatials.get(i);
			spatial.renderableComponent.render(batch);
			debugRender(spatial, focusedComponent);
		}

		tempSpatials.clear();
		batch.end();
	}

	private static SceneNodeComponent2 findFocusedComponent() {
		Display current = Display.getCurrent();
		if (current == null) {
			return null;
		}

		Control focusControl = current.getFocusControl();
		if (focusControl == null) {
			return null;
		}

		Composite parent = focusControl instanceof Composite ? (Composite) focusControl : focusControl.getParent();
		while (parent != null) {
			if (parent instanceof ModelEditor) {
				ModelEditorContext<?> context = ((ModelEditor<?>) parent).getContext();
				Object modelInstance = context.modelInstance;
				if (modelInstance instanceof SceneNodeComponent2) {
					return (SceneNodeComponent2) modelInstance;
				} else {
					return null;
				}
			}
			parent = parent.getParent();
		}

		return null;
	}

	private void debugRender(Spatial spatial, SceneNodeComponent2 focusedComponent) {
		Array<DebugRenderable> renderables = debugRenderablesByNode.get(spatial.nodeId);
		if (renderables != null) {
			for (int j = 0; j < renderables.size; j++) {
				DebugRenderable debugRenderable = renderables.get(j);
				if (debugRenderable == focusedComponent) {
					debugRenderable.debugRender(batch);
				}
			}
		}
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	@Override
	public void handleMessage(Object source, Object message) {
		if (source instanceof SceneHierarchyView && message instanceof SelectionMessage) {
			SelectionMessage selectionMessage = (SelectionMessage) message;
			Object selection = selectionMessage.seclection;
			if (selection instanceof NodeInspectable) {
				selectedNode = ((NodeInspectable) selection).target;
			} else if (selection instanceof ComponentInspectable) {
				SceneNodeComponent2 target = ((ComponentInspectable) selection).target;
				selectedNode = target == null ? null : target.getNode();
			} else {
				selectedNode = null;
			}
		} else {
			selectedNode = null;
		}
	}
}
