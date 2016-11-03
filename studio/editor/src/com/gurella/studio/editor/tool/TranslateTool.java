package com.gurella.studio.editor.tool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
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

	private TranslateHandle xHandle;
	private TranslateHandle yHandle;
	private TranslateHandle zHandle;
	private TranslateHandle xzPlaneHandle;

	private final Vector3 temp0 = new Vector3();
	private final Vector3 temp1 = new Vector3();
	private boolean initTranslate = true;
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

		xHandle = new TranslateHandle(HandleType.x, COLOR_X, xHandleModel);
		yHandle = new TranslateHandle(HandleType.y, COLOR_Y, yHandleModel);
		zHandle = new TranslateHandle(HandleType.z, COLOR_Z, zHandleModel);
		xzPlaneHandle = new TranslateHandle(HandleType.xz, COLOR_XZ, xzPlaneHandleModel);
		handles = new TranslateHandle[] { xHandle, yHandle, zHandle, xzPlaneHandle };
	}

	@Override
	ToolType getType() {
		return ToolType.translate;
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
	void activated(HandleType state) {
		super.activated(state);
		initTranslate = true;
	}

	@Override
	void mouseMoved(Vector3 translation, Camera camera, ToolHandle active, int screenX, int screenY) {
		translateHandles(translation);

		Ray ray = camera.getPickRay(screenX, screenY);
		Vector3 rayEnd = temp0.set(translation);
		float dst = camera.position.dst(rayEnd);
		rayEnd = ray.getEndPoint(rayEnd, dst);

		if (initTranslate) {
			initTranslate = false;
			lastPos.set(rayEnd);
		}

		boolean modified = false;
		switch (state) {
		case xz:
			temp1.set(rayEnd.x - lastPos.x, 0, rayEnd.z - lastPos.z);
			modified = true;
			break;
		case x:
			temp1.set(rayEnd.x - lastPos.x, 0, 0);
			modified = true;
			break;
		case y:
			temp1.set(0, rayEnd.y - lastPos.y, 0);
			modified = true;
			break;
		case z:
			temp1.set(0, 0, rayEnd.z - lastPos.z);
			modified = true;
			break;

		default:
			break;
		}

		// node.translate(vec);

		if (modified) {
			// gameObjectModifiedEvent.setGameObject(getProjectManager().current().currScene.currentSelection);
			// Mundus.INSTANCE.postEvent(gameObjectModifiedEvent);
		}

		lastPos.set(rayEnd);
	}

	@Override
	public void dispose() {
		xHandle.dispose();
		yHandle.dispose();
		zHandle.dispose();
		xzPlaneHandle.dispose();
	}
}
