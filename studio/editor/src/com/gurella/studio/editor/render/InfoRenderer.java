package com.gurella.studio.editor.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;
import com.gurella.studio.editor.subscription.EditorResizeListener;

class InfoRenderer implements EditorPreCloseListener, EditorResizeListener {
	private final int editorId;
	private final BitmapFont font;
	private final Matrix4 infoProjection;
	private final StringBuffer info = new StringBuffer();
	private final Matrix4 lookAt = new Matrix4();
	private final Quaternion rotation = new Quaternion();

	InfoRenderer(int editorId) {
		this.editorId = editorId;
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		Graphics graphics = Gdx.graphics;
		infoProjection = new Matrix4().setToOrtho2D(0, 0, graphics.getWidth(), graphics.getHeight());

		EventService.subscribe(editorId, this);
	}

	@Override
	public void resize(int width, int height) {
		infoProjection.setToOrtho2D(0, 0, width, height);
	}

	public void renderInfo(Camera camera, GenericBatch batch) {
		batch.begin(camera);
		batch.activate2dRenderer();
		PolygonSpriteBatch spriteBatch = batch.getSpriteBatch();
		batch.set2dProjection(infoProjection);

		Vector3 position = camera.position;
		info.append("Transform X: ");
		info.append(position.x);
		info.append(" Y: ");
		info.append(position.y);
		if (camera instanceof PerspectiveCamera) {
			info.append(" Z: ");
			info.append(position.z);
		}
		font.draw(spriteBatch, info.toString(), 15, 40);
		info.setLength(0);

		Vector3 direction = camera.direction;
		if (camera instanceof PerspectiveCamera) {
			info.append("Direction   X: ");
			info.append(direction.x);
			info.append(" Y: ");
			info.append(direction.y);
			info.append(" Z: ");
			info.append(direction.z);
		} else {
			OrthographicCamera orthographicCamera = (OrthographicCamera) camera;
			lookAt.setToLookAt(orthographicCamera.direction, orthographicCamera.up);
			lookAt.getRotation(rotation);
			info.append("Rotation : ");
			info.append(rotation.getRoll());
			info.append(" Zoom: ");
			info.append(orthographicCamera.zoom);
		}

		font.draw(spriteBatch, info.toString(), 15, 20);
		info.setLength(0);

		int height = Gdx.graphics.getHeight();
		font.draw(spriteBatch, camera instanceof OrthographicCamera ? "2D" : "3D", 15, height - 20);

		batch.end();
	}

	@Override
	public void onEditorPreClose() {
		EventService.unsubscribe(editorId, this);
		font.dispose();
	}
}
