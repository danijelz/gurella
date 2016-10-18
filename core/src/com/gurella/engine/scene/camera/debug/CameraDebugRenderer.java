package com.gurella.engine.scene.camera.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.camera.CameraComponent;
import com.gurella.engine.scene.camera.CameraViewport;
import com.gurella.engine.scene.renderable.Layer;
import com.gurella.engine.scene.renderable.RenderSystem;

public class CameraDebugRenderer {
	private static int debugWidth = 240;
	private static int debugHeight = 160;

	private final CameraComponent<?> cameraComponent;
	private RenderSystem renderSystem;

	private final FrameBuffer fbo;

	private final Array<Layer> layers = new Array<Layer>();

	private Matrix4 projection;
	private SpriteBatch spriteBatch;

	public CameraDebugRenderer(CameraComponent<?> cameraComponent) {
		this.cameraComponent = cameraComponent;
		renderSystem = cameraComponent.getScene().renderSystem;

		fbo = new FrameBuffer(Format.RGBA8888, debugWidth, debugHeight, true);
		projection = new Matrix4();
		spriteBatch = new SpriteBatch();
	}

	public void debugRender(@SuppressWarnings("unused") GenericBatch batch) {
		fbo.begin();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glViewport(0, 0, debugWidth, debugHeight);
		cameraComponent.getRenderingLayers().appendTo(layers);
		if (layers.size == 0) {
			layers.add(Layer.DEFAULT);
		}
		CameraViewport viewport = cameraComponent.viewport;
		int oldViewportWidth = (int) viewport.getViewportWidth();
		int oldViewportHeight = (int) viewport.getViewportHeight();
		viewport.update(debugWidth, debugHeight);
		renderSystem.render(cameraComponent, layers);
		viewport.update(oldViewportWidth, oldViewportHeight);
		layers.clear();
		fbo.end();

		Graphics graphics = Gdx.graphics;
		int width = graphics.getWidth();
		int height = graphics.getHeight();

		projection.setToOrtho2D(0, 0, width, height);
		Gdx.gl.glViewport(0, 0, width, height);
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		spriteBatch.setProjectionMatrix(projection);
		spriteBatch.begin();
		spriteBatch.draw(fbo.getColorBufferTexture(), (float) width - debugWidth - 20, 20, debugWidth, debugHeight, 0,
				0, debugWidth, debugHeight, false, true);
		spriteBatch.end();

		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
	}

	public void dispose() {
		fbo.dispose();
		spriteBatch.dispose();
	}
}
