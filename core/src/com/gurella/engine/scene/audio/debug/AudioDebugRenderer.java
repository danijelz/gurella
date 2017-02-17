package com.gurella.engine.scene.audio.debug;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.async.AsyncService;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.audio.AudioListenerComponent;
import com.gurella.engine.scene.audio.AudioSourceComponent;
import com.gurella.engine.scene.debug.DebugRenderable.DebugRenderContext;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;

public class AudioDebugRenderer implements ApplicationShutdownListener, Disposable {
	private static final String listenerTextureLocation = "com/gurella/engine/scene/audio/debug/listener.png";
	private static final String speakerTextureLocation = "com/gurella/engine/scene/audio/debug/speaker.png";

	private static final Vector3 up = new Vector3(0, 1, 0);
	private static final ObjectMap<Application, AudioDebugRenderer> instances = new ObjectMap<Application, AudioDebugRenderer>();

	private Texture listenerTexture;
	private Sprite listenerSprite;
	private Texture speakerTexture;
	private Sprite speakerSprite;

	private Matrix4 transform = new Matrix4();
	private Vector3 position = new Vector3();

	public static void render(DebugRenderContext context, AudioListenerComponent listenerComponent) {
		getRenderer().renderListener(context, listenerComponent);
	}

	private static AudioDebugRenderer getRenderer() {
		synchronized (instances) {
			Application app = AsyncService.getCurrentApplication();
			AudioDebugRenderer renderer = instances.get(app);
			if (renderer == null) {
				renderer = DisposablesService.add(new AudioDebugRenderer());
				instances.put(app, renderer);
				EventService.subscribe(renderer);
			}
			return renderer;
		}
	}

	public static void render(DebugRenderContext context, AudioSourceComponent sourceComponent) {
		getRenderer().renderSource(context, sourceComponent);
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

	private void renderListener(DebugRenderContext context, AudioListenerComponent listenerComponent) {
		GenericBatch batch = context.batch;
		Camera camera = context.camera;
		listenerComponent.getTransform(transform);
		transform.getTranslation(position);
		transform.setToLookAt(position, camera.position, up);
		Matrix4.inv(transform.val);
		batch.set2dTransform(transform);
		batch.render(listenerSprite);
	}

	private void renderSource(DebugRenderContext context, AudioSourceComponent listenerComponent) {
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
		EventService.unsubscribe(this);
		DisposablesService.dispose(this);
		synchronized (instances) {
			instances.remove(AsyncService.getCurrentApplication());
		}
	}

	@Override
	public void dispose() {
		listenerTexture.dispose();
		speakerTexture.dispose();
	}
}
