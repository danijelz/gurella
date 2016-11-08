package com.gurella.engine.scene.bullet.shape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBox2dShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.transform.TransformComponent;

public class Box2dCollisionShape extends CollisionShape {
	private static final Color DEBUG_OUTLINE_COLOR = new Color(0f, 0f, 1f, 1f);

	public final Vector2 halfExtents = new Vector2(0.5f, 0.5f);

	@Override
	public btCollisionShape createNativeShape() {
		return new btBox2dShape(new Vector3(halfExtents, 0));
	}

	@Override
	public void debugRender(GenericBatch batch, TransformComponent transformComponent) {
		Gdx.gl20.glLineWidth(2.4f);
		batch.setShapeRendererTransform(transformComponent);
		batch.setShapeRendererColor(DEBUG_OUTLINE_COLOR);
		batch.setShapeRendererShapeType(ShapeType.Line);
		batch.line(-halfExtents.x, -halfExtents.y, -halfExtents.x, halfExtents.y);
		batch.line(-halfExtents.x, halfExtents.y, halfExtents.x, halfExtents.y);
		batch.line(halfExtents.x, halfExtents.y, halfExtents.x, -halfExtents.y);
		batch.line(halfExtents.x, -halfExtents.y, -halfExtents.x, -halfExtents.y);
		Gdx.gl20.glLineWidth(1f);
	}
}
