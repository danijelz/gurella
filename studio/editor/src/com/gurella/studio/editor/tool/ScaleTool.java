package com.gurella.studio.editor.tool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.gurella.engine.graphics.render.GenericBatch;

public class ScaleTool extends TransformTool {
	private final ScaleHandle xHandle;
	private final ScaleHandle yHandle;
	private final ScaleHandle zHandle;
	private final ScaleHandle xyzHandle;
	private final ScaleHandle[] handles;

	private final Matrix4 shapeRenderMat = new Matrix4();

	private final Vector3 temp0 = new Vector3();
	private final Vector3 temp1 = new Vector3();
	private final Vector3 tempScale = new Vector3();
	private final Vector3 tempScaleDst = new Vector3();

	private TransformState state = TransformState.IDLE;

	private ShapeRenderer shapeRenderer;

	public ScaleTool() {
		ModelBuilder modelBuilder = new ModelBuilder();

		Model xPlaneHandleModel = createArrowStub(new Material(ColorAttribute.createDiffuse(COLOR_X)), Vector3.Zero,
				new Vector3(15, 0, 0));
		Model yPlaneHandleModel = createArrowStub(new Material(ColorAttribute.createDiffuse(COLOR_Y)), Vector3.Zero,
				new Vector3(0, 15, 0));
		Model zPlaneHandleModel = createArrowStub(new Material(ColorAttribute.createDiffuse(COLOR_Z)), Vector3.Zero,
				new Vector3(0, 0, 15));
		Model xyzPlaneHandleModel = modelBuilder.createBox(3, 3, 3,
				new Material(ColorAttribute.createDiffuse(COLOR_XYZ)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

		xHandle = new ScaleHandle(X_HANDLE_ID, COLOR_X, xPlaneHandleModel);
		yHandle = new ScaleHandle(Y_HANDLE_ID, COLOR_Y, yPlaneHandleModel);
		zHandle = new ScaleHandle(Z_HANDLE_ID, COLOR_Z, zPlaneHandleModel);
		xyzHandle = new ScaleHandle(XYZ_HANDLE_ID, COLOR_XYZ, xyzPlaneHandleModel);

		handles = new ScaleHandle[] { xHandle, yHandle, zHandle, xyzHandle };
	}

	@Override
	void render(Vector3 translation, Camera camera, GenericBatch batch) {
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
		batch.begin(camera);
		xHandle.render(batch);
		yHandle.render(batch);
		zHandle.render(batch);
		xyzHandle.render(batch);
		batch.end();

		Graphics graphics = Gdx.graphics;
		//transform.getTranslation(temp0);
		temp0.set(translation);
		Vector3 pivot = camera.project(temp0, 0, 0, graphics.getWidth(), graphics.getHeight());
		shapeRenderMat.setToOrtho2D(0, 0, graphics.getWidth(), graphics.getHeight());

		switch (state) {
		case TRANSFORM_X:
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(COLOR_X);
			shapeRenderer.setProjectionMatrix(shapeRenderMat);
			shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), graphics.getHeight() - Gdx.input.getY(), 2);
			shapeRenderer.end();
			break;
		case TRANSFORM_Y:
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(COLOR_Y);
			shapeRenderer.setProjectionMatrix(shapeRenderMat);
			shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), graphics.getHeight() - Gdx.input.getY(), 2);
			shapeRenderer.end();
			break;
		case TRANSFORM_Z:
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(COLOR_Z);
			shapeRenderer.setProjectionMatrix(shapeRenderMat);
			shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), graphics.getHeight() - Gdx.input.getY(), 2);
			shapeRenderer.end();
			break;
		case TRANSFORM_XYZ:
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(COLOR_XYZ);
			shapeRenderer.setProjectionMatrix(shapeRenderMat);
			shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), graphics.getHeight() - Gdx.input.getY(), 2);
			shapeRenderer.end();
			break;
		default:
			break;
		}
	}

	public static Model createArrowStub(Material mat, Vector3 from, Vector3 to) {
		ModelBuilder builder = new ModelBuilder();
		builder.begin();
		// line
		MeshPartBuilder meshBuilder = builder.part("line", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, mat);
		meshBuilder.line(from.x, from.y, from.z, to.x, to.y, to.z);
		// stub
		Node node = builder.node();
		node.translation.set(to.x, to.y, to.z);
		meshBuilder = builder.part("stub", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, mat);
		BoxShapeBuilder.build(meshBuilder, 2, 2, 2);

		return builder.end();
	}

	@Override
	void update(Vector3 translation, Camera camera) {
		translateHandles(translation);
		scaleHandles(translation, camera);
	}

	protected void translateHandles(Vector3 translation) {
		xHandle.position.set(translation);
		xHandle.applyTransform();
		yHandle.position.set(translation);
		yHandle.applyTransform();
		zHandle.position.set(translation);
		zHandle.applyTransform();
		xyzHandle.position.set(translation);
		xyzHandle.applyTransform();
	}

	protected void scaleHandles(Vector3 translation, Camera camera) {
		float scaleFactor = camera.position.dst(translation) * 0.01f;

		xHandle.scale.set(scaleFactor, scaleFactor, scaleFactor);
		xHandle.applyTransform();

		yHandle.scale.set(scaleFactor, scaleFactor, scaleFactor);
		yHandle.applyTransform();

		zHandle.scale.set(scaleFactor, scaleFactor, scaleFactor);
		zHandle.applyTransform();

		xyzHandle.scale.set(scaleFactor, scaleFactor, scaleFactor);
		xyzHandle.applyTransform();
	}

	///////////////////////intersection

	@Override
	ToolHandle getIntersection(Vector3 cameraPosition, Ray ray, Vector3 intersection) {
		Vector3 closestIntersection = new Vector3(Float.NaN, Float.NaN, Float.NaN);
		float closestDistance = Float.MAX_VALUE;
		ScaleHandle closestHandle = null;

		for (ScaleHandle scaleHandle : handles) {
			scaleHandle.modelInstance.calculateTransforms();

			if (scaleHandle.getIntersection(cameraPosition, ray, intersection)) {
				float distance = intersection.dst2(cameraPosition);
				if (closestDistance > distance) {
					closestDistance = distance;
					closestIntersection.set(intersection);
					closestHandle = scaleHandle;
				}
			}
		}

		if (closestHandle != null) {
			System.out.println(closestHandle.id);
		}

		return closestHandle;
	}

	@Override
	public void dispose() {
		xHandle.dispose();
		yHandle.dispose();
		zHandle.dispose();
		xyzHandle.dispose();
	}
}
