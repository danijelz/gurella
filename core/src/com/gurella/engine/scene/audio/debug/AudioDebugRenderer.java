package com.gurella.engine.scene.audio.debug;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.audio.AudioListenerComponent;
import com.gurella.engine.scene.audio.AudioSourceComponent;
import com.gurella.engine.scene.debug.DebugRenderable.RenderContext;
import com.gurella.engine.scene.light.LightComponent;
import com.gurella.engine.scene.light.PointLightComponent;
import com.gurella.engine.scene.light.SpotLightComponent;
import com.gurella.engine.scene.light.debug.LightDebugRenderer;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;

public class AudioDebugRenderer  implements ApplicationShutdownListener {
	private static final String listenerTextureLocation = "com/gurella/engine/scene/audio/debug/listener.png";
	private static final String speakerTextureLocation = "com/gurella/engine/scene/light/debug/speaker.png";
	
	private static final Vector3 up = new Vector3(0, 1, 0);
	private static final ObjectMap<Application, AudioDebugRenderer> instances = new ObjectMap<Application, AudioDebugRenderer>();

	private Texture listenerTexture;
	private Sprite listenerSprite;
	private Texture speakerTexture;
	private Sprite speakerSprite;
	
	private Matrix4 transform = new Matrix4();
	private Vector3 position = new Vector3();

	public static void render(RenderContext context, AudioListenerComponent listenerComponent) {
		Application app = Gdx.app;
		if (app == null) {
			return;
		}

		AudioDebugRenderer renderer = instances.get(app);
		if (renderer == null) {
			renderer = new AudioDebugRenderer();
			instances.put(app, renderer);
		}

		renderer.renderListener(context, listenerComponent);
	}
	
	public static void render(RenderContext context, AudioSourceComponent sourceComponent) {
		Application app = Gdx.app;
		if (app == null) {
			return;
		}

		AudioDebugRenderer renderer = instances.get(app);
		if (renderer == null) {
			renderer = new AudioDebugRenderer();
			instances.put(app, renderer);
		}

		renderer.renderSource(context, sourceComponent);
	}

	private AudioDebugRenderer() {
		listenerTexture = new Texture(Gdx.files.classpath(listenerTextureLocation));
		listenerTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		listenerSprite = new Sprite(listenerTexture);
		listenerSprite.setSize(0.2f, 0.2f);
		listenerSprite.setOriginCenter();

		speakerTexture = new Texture(Gdx.files.classpath(speakerTextureLocation));
		speakerTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		speakerSprite = new Sprite(speakerTexture);
		speakerSprite.setSize(0.2f, 0.2f);
		speakerSprite.setOriginCenter();
	}

	private void renderListener(RenderContext context, AudioListenerComponent listenerComponent) {
		GenericBatch batch = context.batch;
		Camera camera = context.camera;
		listenerComponent.getTransform(transform);
		transform.getTranslation(position);
		transform.setToLookAt(position, camera.position, up);
		Matrix4.inv(transform.val);
		batch.set2dTransform(transform);
		batch.render(listenerSprite);
	}
	
	private void renderSource(RenderContext context, AudioSourceComponent listenerComponent) {
		GenericBatch batch = context.batch;
		Camera camera = context.camera;
		listenerComponent.getTransform(transform);
		transform.getTranslation(position);
		transform.setToLookAt(position, camera.position, up);
		Matrix4.inv(transform.val);
		batch.set2dTransform(transform);
		batch.render(listenerSprite);
	}

	@Override
	public void shutdown() {
		instances.remove(Gdx.app);
		listenerTexture.dispose();
		speakerTexture.dispose();
	}
}
