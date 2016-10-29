package com.gurella.studio.editor;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.gurella.engine.application.GurellaStateProvider;
import com.gurella.engine.event.Dispatcher;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.debug.DebugRenderable.DebugRenderContext;
import com.gurella.engine.subscriptions.application.ApplicationDebugUpdateListener;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.common.Compass;
import com.gurella.studio.editor.render.EditorInfoRenderer;
import com.gurella.studio.editor.render.GridModelInstance;
import com.gurella.studio.editor.render.SceneEditorRenderSystem;
import com.gurella.studio.editor.subscription.EditorContextMenuContributor;
import com.gurella.studio.editor.subscription.EditorMouseListener;
import com.gurella.studio.editor.subscription.SceneLoadedListener;

final class SceneEditorApplicationListener extends ApplicationAdapter
		implements GurellaStateProvider, EditorMouseListener, SceneLoadedListener {
	private static final Dispatcher<ApplicationDebugUpdateListener> debugUpdateDispatcher = l -> l.debugUpdate();

	private final int editorId;

	private Thread renderThread;

	@SuppressWarnings("unused")
	private SceneEditorFocusManager focusManager;
	private SceneEditorCameraManager cameraManager;

	private GenericBatch batch;
	private DebugRenderContext renderContext = new DebugRenderContext();
	private Environment environment;
	private Color backgroundColor = new Color(0.501960f, 0.501960f, 0.501960f, 1f);

	private GridModelInstance gridModelInstance;
	private Compass compass;

	private EditorInfoRenderer infoRenderer;

	private SceneEditorRenderSystem renderSystem;

	public SceneEditorApplicationListener(int editorId) {
		this.editorId = editorId;
		EventService.subscribe(editorId, this);

	}

	@Override
	@SuppressWarnings("deprecation")
	public void create() {
		renderThread = Thread.currentThread();
		cameraManager = new SceneEditorCameraManager(editorId);
		focusManager = new SceneEditorFocusManager(editorId);
		renderSystem = new SceneEditorRenderSystem(editorId);

		batch = new GenericBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
		environment.set(new DepthTestAttribute());
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		gridModelInstance = new GridModelInstance();
		compass = new Compass(editorId);
		infoRenderer = new EditorInfoRenderer(editorId);

		DefaultShader.defaultCullFace = 0;
	}

	@Override
	public void sceneLoaded(Scene scene) {
		debugUpdate();
	}

	@Override
	public void resize(int width, int height) {
		cameraManager.resize(width, height);
		infoRenderer.resize(width, height);
	}

	@Override
	public void render() {
		debugUpdate();
		inputQueue.drain();
		inputController.update();
		renderScene();
	}

	static void debugUpdate() {
		EventService.post(ApplicationDebugUpdateListener.class, debugUpdateDispatcher);
	}

	public void renderScene() {
		synchronized (GurellaStudioPlugin.glMutex) {
			updateGlState();

			batch.begin(camera);
			batch.render(gridModelInstance, environment);
			compass.render(batch.getModelBatch());
			batch.end();

			renderContext.batch = batch;
			renderContext.camera = camera;
			renderSystem.renderScene(renderContext);
			infoRenderer.renderInfo(camera, batch);
		}
	}

	protected void updateGlState() {
		Color color = backgroundColor;
		Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
		Gdx.gl.glClearDepthf(1);
		Gdx.gl.glClearStencil(0);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
		Graphics graphics = Gdx.graphics;
		Gdx.gl.glViewport(0, 0, graphics.getWidth(), graphics.getHeight());
	}

	@Override
	public boolean isInRenderThread() {
		return renderThread == Thread.currentThread();
	}

	@Override
	public void onMouseMenu(float x, float y) {
		ContextMenuActions actions = new ContextMenuActions();
		EventService.post(editorId, EditorContextMenuContributor.class, c -> c.contribute(actions));
		actions.showMenu();
	}

	@Override
	public void onMouseSelection(float x, float y) {
	}

	@Override
	public void dispose() {
		EventService.post(ApplicationShutdownListener.class, l -> l.shutdown());
		debugUpdate();
		EventService.unsubscribe(editorId, this);
		batch.dispose();
		gridModelInstance.dispose();
		compass.dispose();
		// TODO DisposablesService.disposeAll();
	}
}