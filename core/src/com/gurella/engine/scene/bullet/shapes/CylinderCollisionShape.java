package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShapeX;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShapeZ;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.math.geometry.Axis;

public class CylinderCollisionShape extends BulletCollisionShape {
	private Axis axis = Axis.y;
	public final Vector3 halfExtents = new Vector3(0.5f, 0.5f, 0.5f);

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
			return new btCylinderShapeX(halfExtents);
		case y:
			return new btCylinderShape(halfExtents);
		case z:
			return new btCylinderShapeZ(halfExtents);
		default:
			throw new GdxRuntimeException("Unsuported axis.");
		}
	}

	@Override
	public void debugRender(GenericBatch batch) {
		// TODO Auto-generated method stub
		
	}
}
