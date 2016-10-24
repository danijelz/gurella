package com.gurella.engine.scene.light.debug;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.debug.DebugRenderable.RenderContext;
import com.gurella.engine.scene.light.LightComponent;
import com.gurella.engine.scene.light.PointLightComponent;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;

public class LightDebugRenderer implements ApplicationShutdownListener {
	private static final ObjectMap<Application, LightDebugRenderer> instances = new ObjectMap<Application, LightDebugRenderer>();
	private static final Vector3 up = new Vector3(0, 1, 0);

	private Texture pointLightTexture;
	private Sprite pointLightSprite;
	private Texture spotLightTexture;
	private Sprite spotLightSprite;
	private Matrix4 transform = new Matrix4();
	private Vector3 position = new Vector3();

	public static void render(RenderContext context, LightComponent<?> lightComponent) {
		LightDebugRenderer renderer = instances.get(Gdx.app);
		if (renderer == null) {
			renderer = new LightDebugRenderer();
			instances.put(Gdx.app, renderer);
		}
		renderer.renderLight(context, lightComponent);
	}

	private LightDebugRenderer() {
		pointLightTexture = new Texture(Gdx.files.classpath("com/gurella/engine/scene/light/debug/pointLight.png"));
		pointLightSprite = new Sprite(pointLightTexture);
		pointLightSprite.setOriginCenter();

		spotLightTexture = new Texture(Gdx.files.classpath("com/gurella/engine/scene/light/debug/spotLight.png"));
		spotLightSprite = new Sprite(pointLightTexture);
		spotLightSprite.setOriginCenter();
	}

	private void renderLight(RenderContext context, LightComponent<?> lightComponent) {
		GenericBatch batch = context.batch;
		Camera camera = context.camera;

		if (lightComponent instanceof PointLightComponent) {
			PointLightComponent pointLightComponent = (PointLightComponent) lightComponent;
			pointLightComponent.getTransform(transform);
			transform.getTranslation(position);
			transform.setToLookAt(position, camera.position, up);
			batch.set2dTransform(transform);
			batch.render(pointLightSprite);
		}
	}

	@Override
	public void shutdown() {
		pointLightTexture.dispose();
		spotLightTexture.dispose();
		instances.remove(Gdx.app);
	}
}
