package com.gurella.studio.editor.swtgl;

import com.badlogic.gdx.ApplicationAdapter;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.application.ApplicationDebugUpdateListener;
import com.gurella.studio.editor.camera.CameraManager;
import com.gurella.studio.editor.focus.FocusManager;
import com.gurella.studio.editor.input.InputManager;
import com.gurella.studio.editor.menu.ContextMenuManager;
import com.gurella.studio.editor.render.RenderSystem;
import com.gurella.studio.editor.subscription.EditorInputUpdateListener;
import com.gurella.studio.editor.subscription.EditorPreRenderUpdateListener;
import com.gurella.studio.editor.subscription.EditorRenderUpdateListener;
import com.gurella.studio.editor.subscription.EditorResizeListener;

final class EditorApplicationListener extends ApplicationAdapter {
	private final int editorId;

	@SuppressWarnings("unused")
	private CameraManager cameraManager;
	@SuppressWarnings("unused")
	private InputManager inputManager;
	@SuppressWarnings("unused")
	private FocusManager focusManager;
	@SuppressWarnings("unused")
	private ContextMenuManager contextMenuManager;
	@SuppressWarnings("unused")
	private RenderSystem renderSystem;

	public EditorApplicationListener(int editorId) {
		this.editorId = editorId;
	}

	@Override
	public void create() {
		cameraManager = new CameraManager(editorId);
		inputManager = new InputManager(editorId);
		focusManager = new FocusManager(editorId);
		contextMenuManager = new ContextMenuManager(editorId);
		renderSystem = new RenderSystem(editorId);
	}

	@Override
	public void resize(int width, int height) {
		EventService.post(editorId, EditorResizeListener.class, l -> l.resize(width, height));
	}

	@Override
	public void render() {
		EventService.post(editorId, EditorInputUpdateListener.class, l -> l.onInputUpdate());
		EventService.post(ApplicationDebugUpdateListener.class, l -> l.debugUpdate());
		EventService.post(editorId, EditorPreRenderUpdateListener.class, l -> l.onPreRenderUpdate());
		EventService.post(editorId, EditorRenderUpdateListener.class, l -> l.onRenderUpdate());
	}
}