package com.gurella.studio.editor.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.studio.editor.subscription.EditorActiveCameraProvider;
import com.gurella.studio.editor.subscription.EditorCameraChangedListener;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;

public class Grid2d implements EditorCameraChangedListener, EditorPreCloseListener {
	private final int editorId;

	private Camera camera;

	public Grid2d(int editorId) {
		this.editorId = editorId;
		EventService.post(editorId, EditorActiveCameraProvider.class, l -> camera = l.getActiveCamera());
		EventService.subscribe(editorId, this);
	}

	@Override
	public void cameraChanged(Camera camera) {
		this.camera = camera;
	}

	public void render(GenericBatch batch) {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		batch.begin(camera);
		//batch.render(instance, environment);
		batch.end();
	}

	@Override
	public void onEditorPreClose() {
		EventService.unsubscribe(editorId, this);
	}
}
