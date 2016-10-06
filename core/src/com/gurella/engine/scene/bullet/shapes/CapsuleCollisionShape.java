package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShapeX;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShapeZ;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.math.geometry.Axis;

public class CapsuleCollisionShape extends BulletCollisionShape {
	private Axis axis = Axis.y;
	float radius = 1;
	float height = 0.2f;

	public Axis getAxis() {
		return axis;
	}

	public void setAxis(Axis axis) {
		this.axis = axis == null ? Axis.y : axis;
	}

	@Override
	public btCollisionShape createNativeShape() {
		switch (axis) {
		case x:
			return new btCapsuleShapeX(radius, height);
		case y:
			return new btCapsuleShape(radius, height);
		case z:
			return new btCapsuleShapeZ(radius, height);
		default:
			throw new GdxRuntimeException("Unsuported axis.");
		}
	}

	@Override
	public void debugRender(GenericBatch batch) {
		// TODO Auto-generated method stub
		
	}
}
