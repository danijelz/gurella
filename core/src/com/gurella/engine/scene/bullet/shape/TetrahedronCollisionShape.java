package com.gurella.engine.scene.bullet.shape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btTetrahedronShapeEx;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.transform.TransformComponent;

public class TetrahedronCollisionShape extends CollisionShape {
	private static final Color DEBUG_OUTLINE_COLOR = new Color(0f, 0f, 1f, 1f);

	public final Vector3 v0 = new Vector3(-0.5f, 0, -0.5f);
	public final Vector3 v1 = new Vector3(0.5f, 0, -0.5f);
	public final Vector3 v2 = new Vector3(0, 0, 0.5f);
	public final Vector3 v3 = new Vector3(0, 1, 0);

	@Override
	public btCollisionShape createNativeShape() {
		btTetrahedronShapeEx shape = new btTetrahedronShapeEx();
		shape.setVertices(v0, v1, v2, v3);
		return shape;
	}

	@Override
	public void debugRender(GenericBatch batch, TransformComponent transformComponent) {
		Gdx.gl20.glLineWidth(2.4f);
		batch.setShapeRendererTransform(transformComponent);
		batch.setShapeRendererColor(DEBUG_OUTLINE_COLOR);
		batch.setShapeRendererShapeType(ShapeType.Line);

		batch.line(v0, v1);
		batch.line(v1, v2);
		batch.line(v2, v0);

		batch.line(v1, v2);
		batch.line(v2, v3);
		batch.line(v3, v1);

		batch.line(v0, v2);
		batch.line(v2, v3);
		batch.line(v3, v0);

		batch.line(v0, v3);
		batch.line(v3, v2);
		batch.line(v2, v0);

		Gdx.gl20.glLineWidth(1f);
	}
}
