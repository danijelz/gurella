package com.gurella.studio.editor.tool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
	private TranslateHandle xzHandle;
	private ToolHandle[] handles;

	private final Vector3 temp0 = new Vector3();
	private final Vector3 temp1 = new Vector3();
	private boolean initTranslate = true;
	private Vector3 lastPos = new Vector3();

	public TranslateTool(int editorId) {
		super(editorId);

		ModelBuilder modelBuilder = new ModelBuilder();
		Model xHandleModel = arrow(modelBuilder, COLOR_X, 0, 0, 0, 1, 0, 0);
		Model yHandleModel = arrow(modelBuilder, COLOR_Y, 0, 0, 0, 0, 1, 0);
		Model zHandleModel = arrow(modelBuilder, COLOR_Z, 0, 0, 0, 0, 0, 1);
		Model xzPlaneHandleModel = modelBuilder.createSphere(1, 1, 1, 20, 20,
				new Material(ColorAttribute.createDiffuse(COLOR_XZ)), Usage.Position | Usage.Normal);

		xHandle = new TranslateHandle(HandleType.x, COLOR_X, xHandleModel);
		yHandle = new TranslateHandle(HandleType.y, COLOR_Y, yHandleModel);
		zHandle = new TranslateHandle(HandleType.z, COLOR_Z, zHandleModel);
		xzHandle = new TranslateHandle(HandleType.xz, COLOR_XZ, xzPlaneHandleModel);
		handles = new TranslateHandle[] { xHandle, yHandle, zHandle, xzHandle };
	}

	private Model arrow(ModelBuilder builder, Color color, float x1, float y1, float z1, float x2, float y2, float z2) {
		int usage = Usage.Position | Usage.Normal;
		Material material = new Material(ColorAttribute.createDiffuse(color));
		return builder.createArrow(x1, y1, z1, x2, y2, z2, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS,
				GL20.GL_TRIANGLES, material, usage);
	}

	@Override
	ToolType getType() {
		return ToolType.translate;
	}

	@Override
	ToolHandle[] getHandles() {
		return handles;
	}

	@Override
	void render(GenericBatch batch) {
		update();

		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

		batch.begin(camera);
		xHandle.render(batch);
		yHandle.render(batch);
		zHandle.render(batch);
		xzHandle.render(batch);
		batch.end();
	}

	@Override
	void update() {
		Vector3 position = getPosition();
		scaleHandles(position);
		translateHandles(position);
		
		xHandle.applyTransform();
		yHandle.applyTransform();
		zHandle.applyTransform();
		xzHandle.applyTransform();
	}

	protected void scaleHandles(Vector3 position) {
		float scaleFactor = camera.position.dst(position) * 0.25f;
		xHandle.scale.set(scaleFactor * 0.7f, scaleFactor / 2, scaleFactor / 2);
		yHandle.scale.set(scaleFactor / 2, scaleFactor * 0.7f, scaleFactor / 2);
		zHandle.scale.set(scaleFactor / 2, scaleFactor / 2, scaleFactor * 0.7f);
		xzHandle.scale.set(scaleFactor * 0.13f, scaleFactor * 0.13f, scaleFactor * 0.13f);
	}

	protected void translateHandles(Vector3 position) {
		xHandle.position.set(position);
		yHandle.position.set(position);
		zHandle.position.set(position);
		xzHandle.position.set(position);
	}

	@Override
	void activate(ToolHandle handle) {
		super.activate(handle);
		initTranslate = true;
	}

	@Override
	void dragged(int screenX, int screenY) {
		translateHandles(transform.getTranslation(temp0));

		Ray ray = camera.getPickRay(screenX, screenY);
		Vector3 rayEnd = temp0;
		float dst = camera.position.dst(rayEnd);
		rayEnd = ray.getEndPoint(rayEnd, dst);

		if (initTranslate) {
			initTranslate = false;
			lastPos.set(rayEnd);
		}

		switch (activeHandleType) {
		case xz:
			temp1.set(rayEnd.x - lastPos.x, 0, rayEnd.z - lastPos.z);
			break;
		case x:
			temp1.set(rayEnd.x - lastPos.x, 0, 0);
			break;
		case y:
			temp1.set(0, rayEnd.y - lastPos.y, 0);
			break;
		case z:
			temp1.set(0, 0, rayEnd.z - lastPos.z);
			break;
		default:
			break;
		}

		transform.translate(temp1);
		lastPos.set(rayEnd);
	}

	@Override
	TransformOperation createOperation(ToolHandle handle) {
		return new TranslateOperation(editorId, transform);
	}

	@Override
	public void dispose() {
		xHandle.dispose();
		yHandle.dispose();
		zHandle.dispose();
		xzHandle.dispose();
	}
}
