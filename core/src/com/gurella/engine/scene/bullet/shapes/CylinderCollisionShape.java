package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShapeX;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShapeZ;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.math.geometry.Axis;

public class CylinderCollisionShape extends BulletCollisionShape {
	private Axis axis = Axis.y;
	public final Vector3 dimensions = new Vector3(1, 1, 1);

	public Axis getAxis() {
		return axis;
	}

	public void setAxis(Axis axis) {
		this.axis = axis == null ? Axis.y : axis;
	}

	@Override
	public btCollisionShape createNativeShape() {
		float x = dimensions.x;
		float y = dimensions.y;
		float z = dimensions.z;
		btCollisionShape shape = doCreateShape();
		dimensions.set(x, y, z);
		return shape;
	}

	private btCollisionShape doCreateShape() {
		switch (axis) {
		case x:
			return new btCylinderShapeX(dimensions);
		case y:
			return new btCylinderShape(dimensions);
		case z:
			return new btCylinderShapeZ(dimensions);
		default:
			throw new GdxRuntimeException("Unsuported axis.");
		}
	}
}
