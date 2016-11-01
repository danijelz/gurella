package com.gurella.studio.editor.tool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.gurella.engine.graphics.render.GenericBatch;

public class TranslateTool extends TransformTool {
	private final float ARROW_THIKNESS = 0.4f;
	private final float ARROW_CAP_SIZE = 0.15f;
	private final int ARROW_DIVISIONS = 12;

	private TransformState state = TransformState.IDLE;
	private boolean initTranslate = true;

	private TranslateHandle xHandle;
	private TranslateHandle yHandle;
	private TranslateHandle zHandle;
	private TranslateHandle xzPlaneHandle;
	private TranslateHandle[] handles;

	private Vector3 lastPos = new Vector3();
	private boolean globalSpace = true;

	public TranslateTool() {
		ModelBuilder modelBuilder = new ModelBuilder();

		int usage = Usage.Position | Usage.Normal;
		Model xHandleModel = modelBuilder.createArrow(0, 0, 0, 1, 0, 0, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS,
				GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(COLOR_X)), usage);
		Model yHandleModel = modelBuilder.createArrow(0, 0, 0, 0, 1, 0, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS,
				GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(COLOR_Y)), usage);
		Model zHandleModel = modelBuilder.createArrow(0, 0, 0, 0, 0, 1, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS,
				GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(COLOR_Z)), usage);
		Model xzPlaneHandleModel = modelBuilder.createSphere(1, 1, 1, 20, 20,
				new Material(ColorAttribute.createDiffuse(COLOR_XZ)), usage);

		xHandle = new TranslateHandle(X_HANDLE_ID, COLOR_X, xHandleModel);
		yHandle = new TranslateHandle(Y_HANDLE_ID, COLOR_Y, yHandleModel);
		zHandle = new TranslateHandle(Z_HANDLE_ID, COLOR_Z, zHandleModel);
		xzPlaneHandle = new TranslateHandle(XZ_HANDLE_ID, COLOR_XZ, xzPlaneHandleModel);
		handles = new TranslateHandle[] { xHandle, yHandle, zHandle, xzPlaneHandle };
	}

	@Override
	void render(Vector3 translation, Camera camera, GenericBatch batch) {
		batch.begin(camera);
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
		xHandle.render(batch);
		yHandle.render(batch);
		zHandle.render(batch);
		xzPlaneHandle.render(batch);
		batch.end();
	}

	@Override
	void update(Vector3 translation, Camera camera) {
		scaleHandles(translation, camera);
		translateHandles(translation);
	}

	protected void scaleHandles(Vector3 translation, Camera camera) {
		float scaleFactor = camera.position.dst(translation) * 0.25f;

		xHandle.scale.set(scaleFactor * 0.7f, scaleFactor / 2, scaleFactor / 2);
		xHandle.applyTransform();

		yHandle.scale.set(scaleFactor / 2, scaleFactor * 0.7f, scaleFactor / 2);
		yHandle.applyTransform();

		zHandle.scale.set(scaleFactor / 2, scaleFactor / 2, scaleFactor * 0.7f);
		zHandle.applyTransform();

		xzPlaneHandle.scale.set(scaleFactor * 0.13f, scaleFactor * 0.13f, scaleFactor * 0.13f);
		xzPlaneHandle.applyTransform();
	}

	protected void translateHandles(Vector3 translation) {
		xHandle.position.set(translation);
		xHandle.applyTransform();
		yHandle.position.set(translation);
		yHandle.applyTransform();
		zHandle.position.set(translation);
		zHandle.applyTransform();
		xzPlaneHandle.position.set(translation);
		xzPlaneHandle.applyTransform();
	}

	@Override
	ToolHandle getIntersection(Vector3 cameraPosition, Ray ray, Vector3 intersection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispose() {
		xHandle.dispose();
		yHandle.dispose();
		zHandle.dispose();
		xzPlaneHandle.dispose();
	}
}
