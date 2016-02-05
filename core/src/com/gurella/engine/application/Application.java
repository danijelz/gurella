package com.gurella.engine.application;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.gurella.engine.application.events.ApplicationActivitySignal;
import com.gurella.engine.application.events.ApplicationResizeSignal;
import com.gurella.engine.application.events.ApplicationShutdownSignal;
import com.gurella.engine.application.events.ApplicationUpdateSignal;
import com.gurella.engine.application.events.UpdateEvent;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.event.EventService;
import com.gurella.engine.resource.SceneElementsResourceContext;
import com.gurella.engine.scene.Scene;

public final class Application extends SceneElementsResourceContext implements ApplicationListener {
	private static final String SCENES_TAG = "scenes";
	private static final String INITIAL_SCENE_ID_TAG = "initialSceneId";
	private static final String BACKGROUND_COLOR_TAG = "backgroundColor";

	private String initialSceneId;
	private Color backgroundColor;

	private boolean paused;
	private final SceneManager sceneManager = new SceneManager(this);

	private final ApplicationUpdateSignal updateSignal = new ApplicationUpdateSignal();
	private final ApplicationResizeSignal resizeSignal = new ApplicationResizeSignal();
	private final ApplicationActivitySignal activitySignal = new ApplicationActivitySignal();
	private final ApplicationShutdownSignal shutdownSignal = new ApplicationShutdownSignal(); 

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

	@Override
	public final void create() {
		// TODO create services by checking if this is studio
		Gdx.app.setLogLevel(com.badlogic.gdx.Application.LOG_DEBUG);
		// TODO Auto-generated method stub
		initializer.init(this);
		// TODO add init scripts to initializer
		sceneManager.showScene(initialSceneId);
	}

	@Override
	public final void resize(int width, int height) {
		// TODO not yet handled by scene
		resizeSignal.onResize(width, height);
	}

	@Override
	public final void render() {
		// TODO clear must be handled by RenderSystem with spec from camera
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		EventService.notify(UpdateEvent.instance);
		updateSignal.update();
	}

	@Override
	public final void pause() {
		paused = true;
		// TODO not yet handled by scene
		activitySignal.onPause();
	}

	@Override
	public final void resume() {
		paused = false;
		// TODO not yet handled by scene
		activitySignal.onResume();
	}

	public final boolean isPaused() {
		return paused;
	}

	public ObjectMap<String, Scene> getScenes() {
		return sceneManager.getScenes();
	}

	public Scene getCurrentScene() {
		return sceneManager.getCurrentScene();
	}

	public String getCurrentSceneGroup() {
		return sceneManager.getCurrentSceneGroup();
	}

	@Override
	public void dispose() {
		shutdownSignal.onShutdown();
		// TODO Auto-generated method stub
		// TODO sceneManager.stop();
		DisposablesService.disposeAll();
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
