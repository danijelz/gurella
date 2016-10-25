package com.gurella.engine.scene.light.debug;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.debug.DebugRenderable.DebugRenderContext;
import com.gurella.engine.scene.light.LightComponent;
import com.gurella.engine.scene.light.PointLightComponent;
import com.gurella.engine.scene.light.SpotLightComponent;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;

public class LightDebugRenderer implements ApplicationShutdownListener {
	private static final String pointLightTextureLocation = "com/gurella/engine/scene/light/debug/pointLight.png";
	private static final String spotLightTextureLocation = "com/gurella/engine/scene/light/debug/spotLight.png";

	private static final Vector3 up = new Vector3(0, 1, 0);
	private static final ObjectMap<Application, LightDebugRenderer> instances = new ObjectMap<Application, LightDebugRenderer>();

	private Texture pointLightTexture;
	private Sprite pointLightSprite;
	private Texture spotLightTexture;
	private Sprite spotLightSprite;

	private Matrix4 transform = new Matrix4();
	private Vector3 position = new Vector3();

	public static void render(DebugRenderContext context, LightComponent<?> lightComponent) {
		Application app = Gdx.app;
		if (app == null) {
			return;
		}

		LightDebugRenderer renderer = instances.get(app);
		if (renderer == null) {
			renderer = new LightDebugRenderer();
			instances.put(app, renderer);
		}

		renderer.renderLight(context, lightComponent);
	}

	private LightDebugRenderer() {
		pointLightTexture = new Texture(Gdx.files.classpath(pointLightTextureLocation));
		pointLightTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		pointLightSprite = new Sprite(pointLightTexture);
		pointLightSprite.setSize(0.2f, 0.2f);
		pointLightSprite.setOriginCenter();

		spotLightTexture = new Texture(Gdx.files.classpath(spotLightTextureLocation));
		spotLightTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		spotLightSprite = new Sprite(spotLightTexture);
		spotLightSprite.setSize(0.2f, 0.2f);
		spotLightSprite.setOriginCenter();

		EventService.subscribe(this);
	}

	private void renderLight(DebugRenderContext context, LightComponent<?> lightComponent) {
		GenericBatch batch = context.batch;

		if (lightComponent instanceof PointLightComponent) {
			PointLightComponent pointLightComponent = (PointLightComponent) lightComponent;
			pointLightComponent.getTransform(transform);
			updateTransform(batch, context.camera);
			pointLightSprite.setColor(pointLightComponent.getColor());
			batch.render(pointLightSprite);
		} else if (lightComponent instanceof SpotLightComponent) {
			SpotLightComponent spotLightComponent = (SpotLightComponent) lightComponent;
			spotLightComponent.getTransform(transform);
			updateTransform(batch, context.camera);
			spotLightSprite.setColor(spotLightComponent.getColor());
			batch.render(spotLightSprite);
		}
	}

	protected void updateTransform(GenericBatch batch, Camera camera) {
		transform.getTranslation(position);
		transform.setToLookAt(position, camera.position, up);
		Matrix4.inv(transform.val);
		batch.set2dTransform(transform);
	}

	@Override
	public void shutdown() {
		Application app = Gdx.app;
		if (instances.get(app) == this) {
			instances.remove(app);
			pointLightTexture.dispose();
			spotLightTexture.dispose();
			EventService.unsubscribe(this);
		}
	}
}
