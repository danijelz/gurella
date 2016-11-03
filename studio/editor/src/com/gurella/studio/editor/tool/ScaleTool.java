package com.gurella.studio.editor.tool;

import static com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute.createDiffuse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.transform.TransformComponent;

public class ScaleTool extends TransformTool {
	private final ScaleHandle xHandle;
	private final ScaleHandle yHandle;
	private final ScaleHandle zHandle;
	private final ScaleHandle xyzHandle;

	private final Matrix4 shapeRenderMat = new Matrix4();

	private final Vector3 temp0 = new Vector3();
	private final Vector3 temp1 = new Vector3();
	private final Vector3 tempScale = new Vector3();
	private final Vector3 tempScaleDst = new Vector3();

	private ShapeRenderer shapeRenderer = new ShapeRenderer();

	public ScaleTool() {
		ModelBuilder modelBuilder = new ModelBuilder();

		Model xPlaneHandleModel = box(COLOR_X, new Vector3(15, 0, 0));
		Model yPlaneHandleModel = box(COLOR_Y, new Vector3(0, 15, 0));
		Model zPlaneHandleModel = box(COLOR_Z, new Vector3(0, 0, 15));

		int usage = Usage.Position | Usage.Normal;
		Material material = new Material(createDiffuse(COLOR_XYZ));
		Model xyzPlaneHandleModel = modelBuilder.createBox(3, 3, 3, material, usage);

		xHandle = new ScaleHandle(HandleType.x, COLOR_X, xPlaneHandleModel);
		yHandle = new ScaleHandle(HandleType.y, COLOR_Y, yPlaneHandleModel);
		zHandle = new ScaleHandle(HandleType.z, COLOR_Z, zPlaneHandleModel);
		xyzHandle = new ScaleHandle(HandleType.xyz, COLOR_XYZ, xyzPlaneHandleModel);

		handles = new ScaleHandle[] { xHandle, yHandle, zHandle, xyzHandle };
	}

	@Override
	ToolType getType() {
		return ToolType.scale;
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
		temp0.set(translation);
		Vector3 pivot = camera.project(temp0, 0, 0, graphics.getWidth(), graphics.getHeight());
		shapeRenderMat.setToOrtho2D(0, 0, graphics.getWidth(), graphics.getHeight());

		switch (state) {
		case x:
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(COLOR_X);
			shapeRenderer.setProjectionMatrix(shapeRenderMat);
			shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), graphics.getHeight() - Gdx.input.getY(), 2);
			shapeRenderer.end();
			break;
		case y:
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(COLOR_Y);
			shapeRenderer.setProjectionMatrix(shapeRenderMat);
			shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), graphics.getHeight() - Gdx.input.getY(), 2);
			shapeRenderer.end();
			break;
		case z:
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(COLOR_Z);
			shapeRenderer.setProjectionMatrix(shapeRenderMat);
			shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), graphics.getHeight() - Gdx.input.getY(), 2);
			shapeRenderer.end();
			break;
		case xyz:
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

	public static Model box(Color color, Vector3 to) {
		Material mat = new Material(createDiffuse(color));
		ModelBuilder builder = new ModelBuilder();
		builder.begin();
		MeshPartBuilder meshBuilder = builder.part("line", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, mat);
		meshBuilder.line(0, 0, 0, to.x, to.y, to.z);
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

	@Override
	void activated(TransformComponent component, Camera camera, HandleType state) {
		super.activated(component, camera, state);

		component.getScale(tempScale);
		tempScaleDst.x = getCurrentDst(camera) / tempScale.x;
		tempScaleDst.y = getCurrentDst(camera) / tempScale.y;
		tempScaleDst.z = getCurrentDst(camera) / tempScale.z;

	}

	@Override
	void mouseMoved(TransformComponent component, Vector3 translation, Camera camera, ToolHandle active, int screenX,
			int screenY) {
		translateHandles(translation);
		float dst = getCurrentDst(camera);

		boolean modified = false;
		switch (state) {
		case x:
			tempScale.x = (100 / tempScaleDst.x * dst) / 1000;
			component.setScale(tempScale.x, tempScale.y, tempScale.z);
			modified = true;
			break;
		case y:
			tempScale.y = (100 / tempScaleDst.y * dst) / 1000;
			component.setScale(tempScale.x, tempScale.y, tempScale.z);
			modified = true;
			break;
		case z:
			tempScale.z = (100 / tempScaleDst.z * dst) / 1000;
			component.scale(tempScale.x, tempScale.y, tempScale.z);
			modified = true;
			break;
		case xyz:
			tempScale.x = (100 / tempScaleDst.x * dst) / 1000;
			tempScale.y = (100 / tempScaleDst.y * dst) / 1000;
			tempScale.z = (100 / tempScaleDst.z * dst) / 1000;
			component.setScale(tempScale.x, tempScale.y, tempScale.z);
			modified = true;
			break;
		default:
			break;
		}
	}

	private float getCurrentDst(Camera camera) {
		Vector3 pivot = camera.project(temp0, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Vector3 mouse = temp1.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 0);
		return dst(pivot.x, pivot.y, mouse.x, mouse.y);
	}

	private static float dst(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
	}

	@Override
	public void dispose() {
		xHandle.dispose();
		yHandle.dispose();
		zHandle.dispose();
		xyzHandle.dispose();
	}
}
