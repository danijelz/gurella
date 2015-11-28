package com.gurella.engine.graph.bullet;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw.DebugDrawModes;
import com.gurella.engine.application.Application;
import com.gurella.engine.application.Units;
import com.gurella.engine.application.CommonUpdateOrder;
import com.gurella.engine.graph.SceneGraph;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneProcessor;
import com.gurella.engine.graph.camera.CameraComponent;

public class BulletDebugDrawProcessor extends SceneProcessor {
	private Camera camera;
	private DebugDrawer debugDrawer;
	private btDynamicsWorld dynamicsWorld;

	@Override
	public int getOrdinal() {
		return CommonUpdateOrder.DEBUG_RENDER;
	}

	@Override
	public void activated() {
		super.activated();

		SceneGraph graph = getGraph();
		SceneNode mainCamera = graph.tagManager.getSingleNodeByTag(PsyclistTag.MAIN_CAMERA);
		camera = mainCamera.getComponent(CameraComponent.class).camera;

		debugDrawer = Application.DISPOSABLE_MANAGER.add(new DebugDrawer());
		debugDrawer.setDebugMode(DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);

		dynamicsWorld = graph.getSystem(BulletPhysicsProcessor.class).dynamicsWorld;
		dynamicsWorld.setDebugDrawer(debugDrawer);
	}

	@Override
	public void update() {
		debugDrawer.begin(camera);
		dynamicsWorld.debugDrawWorld();
		debugDrawer.end();
	}

	private static class DebugDrawer extends btIDebugDraw {
		private int debugMode = 0;
		private ShapeRenderer shapeRenderer = Application.SHAPE_RENDERER;
		private Matrix4 projectionMatrix = new Matrix4();

		@Override
		public void drawLine(Vector3 from, Vector3 to, Vector3 color) {
			if (color.x == 1 && color.y == 1 && color.z == 1) {
				shapeRenderer.setColor(Color.GREEN);
			} else {
				shapeRenderer.setColor(color.x, color.y, color.z, 1f);
			}
			shapeRenderer.line(from, to);
		}

		@Override
		public void drawContactPoint(Vector3 PointOnB, Vector3 normalOnB, float distance, int lifeTime, Vector3 color) {
		}

		@Override
		public void reportErrorWarning(String warningString) {
		}

		@Override
		public void draw3dText(Vector3 location, String textString) {
		}

		@Override
		public void setDebugMode(int debugMode) {
			this.debugMode = debugMode;
		}

		@Override
		public int getDebugMode() {
			return debugMode;
		}

		public void begin(Camera cam) {
			projectionMatrix.set(cam.combined);
			projectionMatrix.scl(Units.PIXELS_PER_METER);
			shapeRenderer.setProjectionMatrix(projectionMatrix);
			shapeRenderer.begin(ShapeType.Line);
		}

		public void end() {
			shapeRenderer.end();
		}
	}
}
