package com.gurella.studio.editor.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.studio.editor.subscription.EditorActiveCameraProvider;
import com.gurella.studio.editor.subscription.EditorCameraSelectionListener;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;

//TODO unused
public class Grid2d implements EditorCameraSelectionListener, EditorPreCloseListener {
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
		int x = (int) camera.position.x;
		int m = x % 50;
		x -= m;
		
		int y = (int) camera.position.y;
		m = y % 50;
		y -= m;

		batch.begin(camera);
		batch.setShapeRendererColor(Color.WHITE);

		int temp1 = x - 50;
		int temp2 = x + 50;
		batch.line(x, 0 - height, x, height + height);
		for (int i = 0, n = width / 50; i < n; i++) {
			batch.line(temp1, 0 - height, temp1, height + height);
			batch.line(temp2, 0 - height, temp2, height + height);
			temp1 -= 50;
			temp2 += 50;
		}
		
		temp1 = y - 50;
		temp2 = y + 50;
		batch.line(0 - width, y, x + width, y);
		for (int i = 0, n = width / 50; i < n; i++) {
			batch.line(0 - width, temp1, x + width, temp1);
			batch.line(0 - width, temp2, x + width, temp2);
			temp1 -= 50;
			temp2 += 50;
		}

		batch.end();
	}

	@Override
	public void onEditorPreClose() {
		EventService.unsubscribe(editorId, this);
	}
}
