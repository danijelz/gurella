package com.gurella.engine.application;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.gurella.engine.event.EventService;
import com.gurella.engine.input.InputMapper;
import com.gurella.engine.resource.SceneElementsResourceContext;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneManager;
import com.gurella.engine.utils.DisposableManager;

public class Application extends SceneElementsResourceContext implements ApplicationListener {
	private static final String SCENES_TAG = "scenes";
	private static final String INITIAL_SCENE_ID_TAG = "initialSceneId";
	private static final String BACKGROUND_COLOR_TAG = "backgroundColor";

	// TODO make not static
	public static final DisposableManager DISPOSABLE_MANAGER = new DisposableManager();
	public static final AsyncExecutor ASYNC_EXECUTOR = DISPOSABLE_MANAGER.add(new AsyncExecutor(4));
	private static final InputMultiplexer INPUT_PROCESSORS = new InputMultiplexer();

	public static SpriteBatch SPRITE_BATCH;
	public static ShapeRenderer SHAPE_RENDERER;

	private boolean paused;
	private String initialSceneId;
	private Color backgroundColor;
	private final SceneManager sceneManager = new SceneManager(this);

	private final ApplicationInitializer initializer;

	public static Application fromJson(String projectFileName) {
		return new Application(new JsonApplicationInitializer(projectFileName));
	}

	private Application() {
		super(null);
		this.initializer = null;
	}

	public Application(ApplicationInitializer initializer) {
		super(null);
		this.initializer = initializer;
	}

	public void addScene(Scene scene) {
		sceneManager.addScene(scene);
	}

	public void setInitialScene(Scene initialScene) {
		this.initialSceneId = initialScene.getId();
	}

	public void setInitialScene(String initialSceneId) {
		this.initialSceneId = initialSceneId;
	}

	public ObjectMap<String, Scene> getScenes() {
		return sceneManager.getScenes();
	}

	@Override
	public void create() {
		//TODO create services by checking if this is studio
		Gdx.app.setLogLevel(com.badlogic.gdx.Application.LOG_DEBUG);
		Gdx.input.setInputProcessor(INPUT_PROCESSORS);
		INPUT_PROCESSORS.addProcessor(InputMapper.INSTANCE);// TODO beautify
		SPRITE_BATCH = DISPOSABLE_MANAGER.add(new SpriteBatch());
		SHAPE_RENDERER = DISPOSABLE_MANAGER.add(new ShapeRenderer());
		// TODO Auto-generated method stub
		initializer.init(this);
		sceneManager.showScene(initialSceneId);
	}

	@Override
	public void resize(int width, int height) {
		//TODO must be handled globally
		sceneManager.resize(width, height);
	}

	@Override
	public void render() {
		//TODO clear must be handled by RenderSystem with spec from camera
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		EventService.notify(UpdateEvent.instance);
	}

	@Override
	public void pause() {
		paused = true;
		sceneManager.pause();
	}

	@Override
	public void resume() {
		paused = false;
		sceneManager.resume();
	}
	
	public boolean isPaused() {
		return paused;
	}

	public static void addInputProcessor(InputProcessor processor) {
		INPUT_PROCESSORS.addProcessor(processor);
	}

	public static void removeInputProcessor(InputProcessor processor) {
		INPUT_PROCESSORS.removeProcessor(processor);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		DISPOSABLE_MANAGER.dispose();
	}

	@Override
	public void write(Json json) {
		json.writeValue(INITIAL_SCENE_ID_TAG, initialSceneId);
		json.writeValue(BACKGROUND_COLOR_TAG, backgroundColor);
		json.writeArrayStart(SCENES_TAG);
		for (Entry<String, Scene> entry : getScenes().entries()) {
			json.writeValue(entry.value, Scene.class);
		}
		json.writeArrayEnd();
		super.write(json);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		initialSceneId = jsonData.getString(INITIAL_SCENE_ID_TAG);
		backgroundColor = json.readValue(Color.class, jsonData.get(BACKGROUND_COLOR_TAG));
		if (backgroundColor == null) {
			backgroundColor = Color.WHITE;
		}
		JsonValue scenesArray = jsonData.get(SCENES_TAG);
		for (JsonValue sceneValue : scenesArray) {
			Scene scene = new Scene(this, "");// TODO
			scene.read(json, sceneValue);
			addScene(scene);
		}
		super.read(json, jsonData);
	}
}
