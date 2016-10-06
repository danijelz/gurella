package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShapeX;
import com.badlogic.gdx.physics.bullet.collision.btConeShapeZ;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.math.geometry.Axis;

public class ConeCollisionShape extends BulletCollisionShape {
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
			return new btConeShapeX(radius, height);
		case y:
			return new btConeShape(radius, height);
		case z:
			return new btConeShapeZ(radius, height);
		default:
			throw new GdxRuntimeException("Unsuported axis.");
		}
	}

	@Override
	public void debugRender(GenericBatch batch) {
		// TODO Auto-generated method stub
		
	}
}
