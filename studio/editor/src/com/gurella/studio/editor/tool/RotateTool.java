package com.gurella.studio.editor.tool;

import static com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute.createDiffuse;
import static com.badlogic.gdx.math.MathUtils.PI;
import static com.badlogic.gdx.math.MathUtils.PI2;
import static com.badlogic.gdx.math.MathUtils.cos;
import static com.badlogic.gdx.math.MathUtils.sin;
import static com.gurella.studio.editor.tool.HandleType.x;
import static com.gurella.studio.editor.tool.HandleType.y;
import static com.gurella.studio.editor.tool.HandleType.z;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.transform.TransformComponent;

public class RotateTool extends TransformTool {
	private RotateHandle xHandle;
	private RotateHandle yHandle;
	private RotateHandle zHandle;

	private Vector3 temp0 = new Vector3();
	private Vector3 temp1 = new Vector3();
	private Quaternion tempQuat = new Quaternion();

	private Matrix4 shapeRenderMat = new Matrix4();
	private ShapeRenderer shapeRenderer = new ShapeRenderer();

	private float lastRot = 0;

	public RotateTool(ToolManager manager) {
		super(manager);

		xHandle = new RotateHandle(x, COLOR_X, torus(COLOR_X, 20, 1, 50, 50));
		xHandle.rotationEuler.set(0, 90, 0);
		xHandle.applyTransform();

		yHandle = new RotateHandle(y, COLOR_Y, torus(COLOR_Y, 20, 1, 50, 50));
		yHandle.rotationEuler.set(90, 0, 0);
		yHandle.applyTransform();

		zHandle = new RotateHandle(z, COLOR_Z, torus(COLOR_Z, 20, 1, 50, 50));
		zHandle.rotationEuler.set(0, 0, 0);
		zHandle.applyTransform();

		handles = new RotateHandle[] { xHandle, yHandle, zHandle };
	}

	@Override
	ToolType getType() {
		return ToolType.rotate;
	}

	@Override
	public void render(Vector3 translation, Camera camera, GenericBatch batch) {
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

		if (activeHandleType == HandleType.none) {
			batch.begin(camera);
			xHandle.render(batch);
			yHandle.render(batch);
			zHandle.render(batch);
			batch.end();
		} else {
			temp0.set(translation);
			Vector3 pivot = camera.project(temp0);
			Graphics graphics = Gdx.graphics;
			shapeRenderMat.setToOrtho2D(0, 0, graphics.getWidth(), graphics.getHeight());

			switch (activeHandleType) {
			case x:
				shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
				shapeRenderer.setColor(Color.BLACK);
				shapeRenderer.setProjectionMatrix(shapeRenderMat);
				shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), graphics.getHeight() - Gdx.input.getY(), 2);
				shapeRenderer.setColor(COLOR_X);
				shapeRenderer.circle(pivot.x, pivot.y, 7);
				shapeRenderer.end();
				break;
			case y:
				shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
				shapeRenderer.setColor(Color.BLACK);
				shapeRenderer.setProjectionMatrix(shapeRenderMat);
				shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), graphics.getHeight() - Gdx.input.getY(), 2);
				shapeRenderer.setColor(COLOR_Y);
				shapeRenderer.circle(pivot.x, pivot.y, 7);
				shapeRenderer.end();
				break;
			case z:
				shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
				shapeRenderer.setColor(Color.BLACK);
				shapeRenderer.setProjectionMatrix(shapeRenderMat);
				shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), graphics.getHeight() - Gdx.input.getY(), 2);
				shapeRenderer.setColor(COLOR_Z);
				shapeRenderer.circle(pivot.x, pivot.y, 7);
				shapeRenderer.end();
				break;
			default:
				break;
			}
		}
	}

	@Override
	void update(Vector3 nodePosition, Vector3 cameraPosition) {
		scaleHandles(nodePosition, cameraPosition);
		translateHandles(nodePosition);
		xHandle.applyTransform();
		yHandle.applyTransform();
		zHandle.applyTransform();
	}

	protected void translateHandles(Vector3 nodePosition) {
		xHandle.position.set(nodePosition);
		yHandle.position.set(nodePosition);
		zHandle.position.set(nodePosition);
	}

	protected void scaleHandles(Vector3 nodePosition, Vector3 cameraPosition) {
		float scaleFactor = cameraPosition.dst(nodePosition) * 0.005f;
		xHandle.scale.set(scaleFactor, scaleFactor, scaleFactor);
		yHandle.scale.set(scaleFactor, scaleFactor, scaleFactor);
		zHandle.scale.set(scaleFactor, scaleFactor, scaleFactor);
	}

	private static Model torus(Color color, float width, float height, int divisionsU, int divisionsV) {
		Material mat = new Material(createDiffuse(color));
		VertexInfo v0 = new VertexInfo();
		VertexInfo v1 = new VertexInfo();

		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder builder = modelBuilder.part("torus", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, mat);

		MeshPartBuilder.VertexInfo vert1 = v0.set(null, null, null, null);
		vert1.hasUV = false;
		vert1.hasNormal = true;
		vert1.hasPosition = true;

		MeshPartBuilder.VertexInfo vert2 = v1.set(null, null, null, null);
		vert2.hasUV = false;
		vert2.hasNormal = true;
		vert2.hasPosition = true;

		int i, j, k;
		float s, t;
		float u, v;
		float theta, rho;
		float x, y, z;
		float nx, ny, nz;
		short i1, i2, i3 = 0, i4 = 0;

		for (i = 0; i < divisionsV; i++) {
			for (j = 0; j <= divisionsU; j++) {
				for (k = 1; k >= 0; k--) {
					s = (i + k) % divisionsV + 0.5f;
					t = j % divisionsU;

					u = j / (divisionsU - 1.0f);
					v = i / (divisionsV - 1.0f);
					theta = u * 2.0f * PI;
					rho = v * 2.0f * PI;

					x = (width + height * cos(s * PI2 / divisionsV)) * cos(t * PI2 / divisionsU);
					y = (width + height * cos(s * PI2 / divisionsV)) * sin(t * PI2 / divisionsU);
					z = height * sin(s * PI2 / divisionsV);

					nx = cos(theta) * cos(rho);
					ny = sin(theta) * cos(rho);
					nz = sin(rho);

					vert1.position.set(x, y, z);
					vert1.normal.set(nx, ny, nz);

					k--;
					s = (i + k) % divisionsV + 0.5f;

					x = (width + height * cos(s * PI2 / divisionsV)) * cos(t * PI2 / divisionsU);
					y = (width + height * cos(s * PI2 / divisionsV)) * sin(t * PI2 / divisionsU);
					z = height * sin(s * PI2 / divisionsV);

					vert2.position.set(x, y, z);
					vert2.normal.set(nx, ny, nz);

					i1 = builder.vertex(vert1);
					i2 = builder.vertex(vert2);
					builder.rect(i4, i2, i1, i3);
					i4 = i2;
					i3 = i1;
				}
			}
		}

		return modelBuilder.end();
	}

	@Override
	void dragged(TransformComponent transform, Camera camera, int screenX, int screenY) {
		translateHandles(transform.getTranslation(temp0));

		float angle = getCurrentAngle(temp0, camera);
		float rot = angle - lastRot;

		switch (activeHandleType) {
		case x:
			tempQuat.setEulerAngles(0, -rot, 0);
			transform.rotate(tempQuat);
			break;
		case y:
			tempQuat.setEulerAngles(-rot, 0, 0);
			transform.rotate(tempQuat);
			break;
		case z:
			tempQuat.setEulerAngles(0, 0, -rot);
			transform.rotate(tempQuat);
			break;
		default:
			break;
		}

		lastRot = angle;
	}

	private float getCurrentAngle(Vector3 translation, Camera camera) {
		temp0.set(translation);
		Vector3 pivot = camera.project(temp0);
		Vector3 mouse = temp1.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 0);
		return angle(pivot.x, pivot.y, mouse.x, mouse.y);
	}

	private static float angle(float x1, float y1, float x2, float y2) {
		return (float) Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
	}

	@Override
	TransformOperation createOperation(ToolHandle handle, TransformComponent component, Camera camera) {
		return new RotateOperation(manager.editorId, component);
	}

	@Override
	public void dispose() {
		xHandle.dispose();
		yHandle.dispose();
		zHandle.dispose();
	}
}
