package com.gurella.engine.graphics.vector;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;

public class GlPathComponent implements Poolable {
	int triangleFanVerticesOffset = -1;
	final Array<Vertex> triangleFanVertices = new Array<Vertex>();
	
	int triangleStripVerticesOffset = -1;
	final Array<Vertex> triangleStripVertices = new Array<Vertex>();
	
	static GlPathComponent obtain() {
		return Pools.obtain(GlPathComponent.class);
	}

	@Override
	public void reset() {
		triangleFanVerticesOffset = -1;
		CanvasUtils.resetArray(triangleFanVertices);
		triangleStripVerticesOffset = -1;
		CanvasUtils.resetArray(triangleStripVertices);
	}
}
