package com.gurella.engine.scene.bullet.shape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btTriangleShapeEx;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.transform.TransformComponent;

public class TriangleCollisionShape extends CollisionShape {
	private static final Color DEBUG_OUTLINE_COLOR = new Color(0f, 0f, 1f, 1f);

	public final Vector3 p0 = new Vector3(-1, 0, 0);
	public final Vector3 p1 = new Vector3(0, 1, 0);
	public final Vector3 p2 = new Vector3(1, 0, 0);

	@Override
	public btCollisionShape createNativeShape() {
		return new btTriangleShapeEx(p0, p1, p2);
	}

	@Override
	public void debugRender(GenericBatch batch, TransformComponent transformComponent) {
		Gdx.gl20.glLineWidth(2.4f);
		batch.setShapeRendererTransform(transformComponent);
		batch.setShapeRendererColor(DEBUG_OUTLINE_COLOR);
		batch.setShapeRendererShapeType(ShapeType.Line);
		batch.line(p0, p1);
		batch.line(p1, p2);
		batch.line(p2, p0);
		Gdx.gl20.glLineWidth(1f);
	}
}
