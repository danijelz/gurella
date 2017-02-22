package com.gurella.engine.scene.renderable;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.IntSet.IntSetIterator;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.BuiltinSceneSystem;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.camera.CameraComponent;
import com.gurella.engine.scene.camera.CameraComponent.OrdinalComparator;
import com.gurella.engine.scene.camera.PerspectiveCameraComponent;
import com.gurella.engine.scene.light.DirectionalLightComponent;
import com.gurella.engine.scene.light.PointLightComponent;
import com.gurella.engine.scene.light.SpotLightComponent;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.scene.spatial.SpatialSystem;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.subscriptions.scene.camera.CameraOrdinalChangedListener;
import com.gurella.engine.subscriptions.scene.renderable.RenderableVisibilityListener;
import com.gurella.engine.subscriptions.scene.update.RenderUpdateListener;
import com.gurella.engine.utils.Exceptions;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Values;

public class RenderSystem extends BuiltinSceneSystem
		implements ComponentActivityListener, RenderUpdateListener, CameraOrdinalChangedListener {
	static {
		defaultCullFace();
	}

	@SuppressWarnings("deprecation")
	private static void defaultCullFace() {
		DefaultShader.defaultCullFace = 0;
	}

	private GenericBatch batch;

	private final Array<CameraComponent<?>> cameras = new Array<CameraComponent<?>>();

	private final LayerMask layerMask = new LayerMask();
	private final Array<Spatial> tempSpatials = new Array<Spatial>(256);

	private IntSet lastVisibleRenderables = new IntSet(256);
	private IntSet currentVisibleRenderables = new IntSet(256);
	private final VisibilityChangedEvent visibilityChangedEvent = new VisibilityChangedEvent();

	private final Environment environment = new Environment();
	private final ColorAttribute ambientLight = new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f);
	private final ColorAttribute fog = new ColorAttribute(ColorAttribute.Fog, 1f, 1f, 1f, 1f);
	private final DepthTestAttribute depthTest = new DepthTestAttribute();
	private final DirectionalLightsAttribute directionalLights = new DirectionalLightsAttribute();
	private final PointLightsAttribute pointLights = new PointLightsAttribute();
	private final SpotLightsAttribute spotLights = new SpotLightsAttribute();

	private SpatialSystem<?> spatialSystem;

	public RenderSystem(Scene scene) {
		super(scene);
		environment.set(depthTest);
		environment.set(directionalLights);
		environment.set(pointLights);
		environment.set(spotLights);
	}

	@Override
	protected void serviceActivated() {
		batch = new GenericBatch();
		spatialSystem = scene.spatialSystem;
	}

	@Override
	protected void serviceDeactivated() {
		spatialSystem = null;
		batch.dispose();
		batch = null;
	}

	@Override
	public void onRenderUpdate() {
		for (int i = 0, n = cameras.size; i < n; i++) {
			render(cameras.get(i));
		}

		visibilityChangedEvent.visible = false;

		IntSetIterator iterator = lastVisibleRenderables.iterator();
		while (iterator.hasNext) {
			int renderableNodeId = iterator.next();
			if (!currentVisibleRenderables.contains(renderableNodeId)) {
				EventService.post(renderableNodeId, visibilityChangedEvent);
			}
		}

		IntSet temp = lastVisibleRenderables;
		lastVisibleRenderables = currentVisibleRenderables;
		currentVisibleRenderables = temp;
		currentVisibleRenderables.clear();
	}

	public void render(CameraComponent<?> cameraComponent) {
		layerMask.reset();
		ImmutableArray<Layer> renderingLayers = cameraComponent.renderingLayers;
		int layersSize = renderingLayers.size();

		if (layersSize == 0) {
			layerMask.allowed(Layer.DEFAULT);
			layerMask.allowed(Layer.SKY);
		} else {
			for (int i = 0, n = layersSize; i > n; i++) {
				layerMask.allowed(renderingLayers.get(i));
			}
		}

		render(cameraComponent, layerMask);
	}

	public void render(CameraComponent<?> cameraComponent, Predicate<RenderableComponent> predicate) {
		Camera camera = cameraComponent.camera;
		cameraComponent.viewport.apply();

		clearGlData(cameraComponent);

		batch.begin(camera);
		batch.setEnvironment(updateEnvironment(cameraComponent));

		try {
			renderSpatials(camera, predicate);
		} catch (Exception e) {
			Exceptions.rethrowAsGdxRuntime(e);
		} finally {
			batch.end();
			batch.setEnvironment(null);
		}
	}

	private static void clearGlData(CameraComponent<?> cameraComponent) {
		int clearValue = 0;

		if (cameraComponent.clearColor) {
			Color color = cameraComponent.clearColorValue;
			Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
			clearValue |= GL20.GL_COLOR_BUFFER_BIT;
		}

		if (cameraComponent.clearDepth) {
			Gdx.gl.glClearDepthf(cameraComponent.clearDepthValue);
			clearValue |= GL20.GL_DEPTH_BUFFER_BIT;
		}

		if (cameraComponent.clearStencil) {
			Gdx.gl.glClearStencil(cameraComponent.clearStencilValue);
			clearValue |= GL20.GL_STENCIL_BUFFER_BIT;
		}

		if (clearValue != 0) {
			Gdx.gl.glClear(clearValue);
		}
	}

	private Environment updateEnvironment(CameraComponent<?> cameraComponent) {
		if (cameraComponent instanceof PerspectiveCameraComponent) {
			PerspectiveCameraComponent perspectiveCamera = (PerspectiveCameraComponent) cameraComponent;

			Color ambientLightColor = perspectiveCamera.ambientLight;
			if (ambientLightColor == null) {
				environment.remove(ambientLight.type);
			} else {
				ambientLight.color.set(ambientLightColor);
				environment.set(ambientLight);
			}

			Color fogColor = perspectiveCamera.fog;
			if (fogColor == null) {
				environment.remove(fog.type);
			} else {
				fog.color.set(fogColor);
				environment.set(fog);
			}
		} else {
			environment.remove(ambientLight.type | fog.type);
		}
		return environment;
	}

	private void renderSpatials(Camera camera, Predicate<RenderableComponent> predicate) {
		spatialSystem.getSpatials(camera.frustum, tempSpatials, predicate);
		tempSpatials.sort(LayerOrdinalComparator.instance);
		visibilityChangedEvent.visible = true;

		for (int i = 0; i < tempSpatials.size; i++) {
			Spatial spatial = tempSpatials.get(i);
			RenderableComponent renderable = spatial.renderable;
			int nodeId = renderable.getNodeId();

			if (!lastVisibleRenderables.contains(nodeId) && !currentVisibleRenderables.add(nodeId)) {
				EventService.post(nodeId, visibilityChangedEvent);
			}

			renderable.render(batch);
		}

		tempSpatials.clear();
	}

	@Override
	public void onComponentActivated(SceneNodeComponent component) {
		if (component instanceof CameraComponent) {
			cameras.add((CameraComponent<?>) component);
			cameras.sort(OrdinalComparator.instance);
		} else if (component instanceof DirectionalLightComponent) {
			directionalLights.lights.add(((DirectionalLightComponent) component).getLight());
		} else if (component instanceof PointLightComponent) {
			pointLights.lights.add(((PointLightComponent) component).getLight());
		} else if (component instanceof SpotLightComponent) {
			spotLights.lights.add(((SpotLightComponent) component).getLight());
		}
	}

	@Override
	public void onComponentDeactivated(SceneNodeComponent component) {
		if (component instanceof CameraComponent) {
			cameras.removeValue((CameraComponent<?>) component, true);
		} else if (component instanceof DirectionalLightComponent) {
			directionalLights.lights.removeValue(((DirectionalLightComponent) component).getLight(), true);
		} else if (component instanceof PointLightComponent) {
			pointLights.lights.removeValue(((PointLightComponent) component).getLight(), true);
		} else if (component instanceof SpotLightComponent) {
			spotLights.lights.removeValue(((SpotLightComponent) component).getLight(), true);
		}
	}

	@Override
	public void onOrdinalChanged(CameraComponent<?> cameraComponent) {
		cameras.sort(OrdinalComparator.instance);
	}

	private static class VisibilityChangedEvent implements Event<RenderableVisibilityListener> {
		boolean visible;

		@Override
		public Class<RenderableVisibilityListener> getSubscriptionType() {
			return RenderableVisibilityListener.class;
		}

		@Override
		public void dispatch(RenderableVisibilityListener subscriber) {
			subscriber.onVisibilityChanged(visible);
		}
	}

	public static final class LayerOrdinalComparator implements Comparator<Spatial> {
		public static final LayerOrdinalComparator instance = new LayerOrdinalComparator();

		@Override
		public int compare(Spatial spatial1, Spatial spatial2) {
			return Values.compare(spatial1.renderable.layer.ordinal, spatial2.renderable.layer.ordinal);
		}
	}
}
