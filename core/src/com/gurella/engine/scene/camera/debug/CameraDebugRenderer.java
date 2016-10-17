package com.gurella.engine.scene.camera.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.camera.CameraComponent;
import com.gurella.engine.scene.renderable.Layer;
import com.gurella.engine.scene.renderable.LayerMask;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.scene.spatial.SpatialSystem;

public class CameraDebugRenderer {
	private CameraComponent<?> cameraComponent;
	private final FrameBuffer fbo;
	private final Array<Spatial> spatials = new Array<Spatial>();
	private final LayerMask layerMask = new LayerMask();

	private final Environment environment = new Environment();
	private final ColorAttribute ambientLight = new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f);
	private final ColorAttribute fog = new ColorAttribute(ColorAttribute.Fog, 1f, 1f, 1f, 1f);
	private final DepthTestAttribute depthTest = new DepthTestAttribute();

	private Matrix4 infoProjection;
	private SpriteBatch spriteBatch;

	public CameraDebugRenderer(CameraComponent<?> cameraComponent) {
		this.cameraComponent = cameraComponent;

		int width = 240;
		int height = 180;

		fbo = new FrameBuffer(Format.RGBA8888, width, height, true);

		environment.set(ambientLight);
		environment.set(fog);
		environment.set(depthTest);

		infoProjection = new Matrix4().setToOrtho2D(0, 0, width, height);
		spriteBatch = new SpriteBatch();
	}

	public void debugRender(GenericBatch batch) {
		fbo.begin();
		Gdx.gl.glClearColor(0, 0, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glViewport(0, 0, 240, 180);
		SpatialSystem<?> spatialSystem = cameraComponent.getScene().spatialSystem;
		layerMask.reset();
		layerMask.allowed(Layer.DEFAULT);
		spatialSystem.getSpatials(cameraComponent.camera.frustum, spatials, layerMask);
		fbo.end();

		spriteBatch.setProjectionMatrix(infoProjection);
		spriteBatch.begin();
		spriteBatch.draw(fbo.getColorBufferTexture(), 100, 100);
		spriteBatch.end();
		// TODO Auto-generated method stub
	}

	public void dispose() {
		fbo.dispose();
		spriteBatch.dispose();
	}
}
