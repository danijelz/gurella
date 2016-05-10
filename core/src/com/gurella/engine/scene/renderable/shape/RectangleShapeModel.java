package com.gurella.engine.scene.renderable.shape;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class RectangleShapeModel extends ShapeModel {
	private float x00 = 1;
	private float y00 = 0;
	private float z00 = -1;

	private float x10 = -1;
	private float y10 = 0;
	private float z10 = -1;

	private float x11 = -1;
	private float y11 = 0;
	private float z11 = 1;

	private float x01 = 1;
	private float y01 = 0;
	private float z01 = 1;

	private float normalX = 0;
	private float normalY = 1;
	private float normalZ = 0;

	public float getX00() {
		return x00;
	}

	public void setX00(float x00) {
		this.x00 = x00;
		dirty = true;
	}

	public float getY00() {
		return y00;
	}

	public void setY00(float y00) {
		this.y00 = y00;
		dirty = true;
	}

	public float getZ00() {
		return z00;
	}

	public void setZ00(float z00) {
		this.z00 = z00;
		dirty = true;
	}

	public float getX10() {
		return x10;
	}

	public void setX10(float x10) {
		this.x10 = x10;
		dirty = true;
	}

	public float getY10() {
		return y10;
	}

	public void setY10(float y10) {
		this.y10 = y10;
		dirty = true;
	}

	public float getZ10() {
		return z10;
	}

	public void setZ10(float z10) {
		this.z10 = z10;
		dirty = true;
	}

	public float getX11() {
		return x11;
	}

	public void setX11(float x11) {
		this.x11 = x11;
		dirty = true;
	}

	public float getY11() {
		return y11;
	}

	public void setY11(float y11) {
		this.y11 = y11;
		dirty = true;
	}

	public float getZ11() {
		return z11;
	}

	public void setZ11(float z11) {
		this.z11 = z11;
		dirty = true;
	}

	public float getX01() {
		return x01;
	}

	public void setX01(float x01) {
		this.x01 = x01;
		dirty = true;
	}

	public float getY01() {
		return y01;
	}

	public void setY01(float y01) {
		this.y01 = y01;
		dirty = true;
	}

	public float getZ01() {
		return z01;
	}

	public void setZ01(float z01) {
		this.z01 = z01;
		dirty = true;
	}

	public float getNormalX() {
		return normalX;
	}

	public void setNormalX(float normalX) {
		this.normalX = normalX;
		dirty = true;
	}

	public float getNormalY() {
		return normalY;
	}

	public void setNormalY(float normalY) {
		this.normalY = normalY;
		dirty = true;
	}

	public float getNormalZ() {
		return normalZ;
	}

	public void setNormalZ(float normalZ) {
		this.normalZ = normalZ;
		dirty = true;
	}

	@Override
	protected Model createModel(ModelBuilder builder) {
		builder.begin();
		builder.part("rect", getGlPrimitiveType(), getVertexAttributes(), getMaterial()).rect(x00, y00, z00, x10, y10,
				z10, x11, y11, z11, x01, y01, z01, normalX, normalY, normalZ);
		return builder.end();
	}
}
