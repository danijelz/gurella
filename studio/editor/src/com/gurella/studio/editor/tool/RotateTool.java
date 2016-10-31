package com.gurella.studio.editor.tool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.graphics.render.GenericBatch;

public class RotateTool extends TransformTool {
	private RotateHandle xHandle;
	private RotateHandle yHandle;
	private RotateHandle zHandle;
	private RotateHandle[] handles;

	private Matrix4 shapeRenderMat = new Matrix4();

	private Vector3 temp0 = new Vector3();
	private Vector3 temp1 = new Vector3();
	private Quaternion tempQuat = new Quaternion();

	private ShapeRenderer shapeRenderer;

	private TransformState state = TransformState.IDLE;
	private float lastRot = 0;

	public RotateTool() {
		this.shapeRenderer = new ShapeRenderer();
		xHandle = new RotateHandle(X_HANDLE_ID, COLOR_X);
		yHandle = new RotateHandle(Y_HANDLE_ID, COLOR_Y);
		zHandle = new RotateHandle(Z_HANDLE_ID, COLOR_Z);
		handles = new RotateHandle[] { xHandle, yHandle, zHandle };
	}

	@Override
	public void render(Vector3 translation, Camera camera, GenericBatch batch) {
		init(translation, camera);
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

		if (state == TransformState.IDLE) {
			batch.begin(camera);
			xHandle.render(batch);
			yHandle.render(batch);
			zHandle.render(batch);
			batch.end();
		} else {

			temp0.set(translation);
			Vector3 pivot = camera.project(temp0);

			shapeRenderMat.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			switch (state) {
			case TRANSFORM_X:
				shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
				shapeRenderer.setColor(Color.BLACK);
				shapeRenderer.setProjectionMatrix(shapeRenderMat);
				shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(),
						2);
				shapeRenderer.setColor(COLOR_X);
				shapeRenderer.circle(pivot.x, pivot.y, 7);
				shapeRenderer.end();
				break;
			case TRANSFORM_Y:
				shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
				shapeRenderer.setColor(Color.BLACK);
				shapeRenderer.setProjectionMatrix(shapeRenderMat);
				shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(),
						2);
				shapeRenderer.setColor(COLOR_Y);
				shapeRenderer.circle(pivot.x, pivot.y, 7);
				shapeRenderer.end();
				break;
			case TRANSFORM_Z:
				shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
				shapeRenderer.setColor(Color.BLACK);
				shapeRenderer.setProjectionMatrix(shapeRenderMat);
				shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(),
						2);
				shapeRenderer.setColor(COLOR_Z);
				shapeRenderer.circle(pivot.x, pivot.y, 7);
				shapeRenderer.end();
				break;
			default:
				break;
			}
		}
	}

	void init(Vector3 selctionTranslation, Camera camera) {
		scaleHandles(selctionTranslation, camera);
		rotateHandles();
		translateHandles(selctionTranslation);
	}

	protected void rotateHandles() {
		xHandle.rotationEuler.set(0, 90, 0);
		xHandle.applyTransform();
		yHandle.rotationEuler.set(90, 0, 0);
		yHandle.applyTransform();
		zHandle.rotationEuler.set(0, 0, 0);
		zHandle.applyTransform();
	}

	protected void translateHandles(Vector3 selctionTranslation) {
		final Vector3 pos = selctionTranslation;
		xHandle.position.set(pos);
		xHandle.applyTransform();
		yHandle.position.set(pos);
		yHandle.applyTransform();
		zHandle.position.set(pos);
		zHandle.applyTransform();
	}

	protected void scaleHandles(Vector3 selctionTranslation, Camera camera) {
		Vector3 pos = selctionTranslation;
		float scaleFactor = camera.position.dst(pos) * 0.005f;

		xHandle.scale.set(scaleFactor, scaleFactor, scaleFactor);
		xHandle.applyTransform();

		yHandle.scale.set(scaleFactor, scaleFactor, scaleFactor);
		yHandle.applyTransform();

		zHandle.scale.set(scaleFactor, scaleFactor, scaleFactor);
		zHandle.applyTransform();
	}

	@Override
	public void dispose() {
		xHandle.dispose();
		yHandle.dispose();
		zHandle.dispose();
	}
}
