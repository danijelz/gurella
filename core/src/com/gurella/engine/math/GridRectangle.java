package com.gurella.engine.math;

public class GridRectangle {
	public int x, y;
	public int width, height;

	public GridRectangle() {
	}

	public GridRectangle(GridRectangle rect) {
		this.x = rect.x;
		this.y = rect.y;
		this.width = rect.width;
		this.height = rect.height;
	}

	public GridRectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public GridRectangle set(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		return this;
	}

	public GridRectangle set(GridRectangle rect) {
		this.x = rect.x;
		this.y = rect.y;
		this.width = rect.width;
		this.height = rect.height;
		return this;
	}
}
