package com.gurella.engine.math.geometry.shape;

public class Box extends Shape {
	public float centerX;
	public float centeryY;
	public float centerZ;
	public float width;
	public float height;
	public float depth;
	public float halfWidth;
	public float halfHeigth;
	public float halfDepth;

	public Box(float centerX, float centeryY, float centerZ, float width, float height, float depth) {
		this.centerX = centerX;
		this.centeryY = centeryY;
		this.centerZ = centerZ;
		this.width = width;
		this.height = height;
		this.depth = depth;
		halfWidth = width / 2;
		halfHeigth = height / 2;
		halfDepth = depth / 2;
		bounds.min.set(centerX - halfWidth, centeryY - halfHeigth, centerZ - halfDepth);
		bounds.max.set(centerX + halfWidth, centeryY + halfHeigth, centerZ + halfDepth);
	}
}
