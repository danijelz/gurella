package com.gurella.engine.graphics.vector.graph;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.graphics.vector.Canvas;

public class CanvasGraph implements Poolable {
	Canvas canvas;

	final Array<CanvasNode> nodes = new Array<CanvasNode>();
	
	final ObjectMap<String, CanvasNode> nodesById = new ObjectMap<String, CanvasNode>();

	public void addNode(CanvasNode child) {
		child.canvas = canvas;
		child.parent = null;
		child.markGeometryDirty();
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}
