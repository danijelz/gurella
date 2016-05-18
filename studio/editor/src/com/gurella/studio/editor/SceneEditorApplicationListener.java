package com.gurella.studio.editor;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.subscriptions.application.ApplicationDebugUpdateListener;
import com.gurella.studio.editor.scene.StudioSceneRenderer;

final class SceneEditorApplicationListener extends ApplicationAdapter implements EditorMessageListener {
	private final Array<ApplicationDebugUpdateListener> listeners = new Array<>(64);

	private StudioSceneRenderer renderer;

	@Override
	public void create() {
		renderer = new StudioSceneRenderer();
	}

	public void presentScene(Scene scene) {
		renderer.setScene(scene);
		debugUpdate();
	}

	@Override
	public void resize(int width, int height) {
		renderer.resize(width, height);
	}

	@Override
	public void render() {
		debugUpdate();
		renderer.render();
	}

	private void debugUpdate() {
		EventService.getSubscribers(ApplicationDebugUpdateListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).debugUpdate();
		}
		listeners.clear();
	}

	@Override
	public void handleMessage(Object source, Object message) {
		renderer.handleMessage(source, message);
	}
}