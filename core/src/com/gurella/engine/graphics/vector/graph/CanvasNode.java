package com.gurella.engine.graphics.vector.graph;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.graphics.vector.CallRecord.DrawPathCallRecord;
import com.gurella.engine.graphics.vector.CallRecord.DrawTrianglesCallRecord;
import com.gurella.engine.graphics.vector.Canvas;
import com.gurella.engine.graphics.vector.Vertex;

//TODO unused
public abstract class CanvasNode implements Poolable {
	Canvas canvas;
	CanvasGraph canvasGraph;
	CanvasNode parent;
	final Array<CanvasNode> children = new Array<CanvasNode>();
	
	//final String id;//TODO add to graph map
	
	
	/////////////////// cached call data
	private DrawPathCallRecord drawPathCallRecord;
	private DrawTrianglesCallRecord drawTrianglesCallRecord;
	
	private final Array<Vertex> triangleVertices = new Array<Vertex>();
	
	//////////
	private boolean geometryDirty = true;
	private boolean uniformsDirty = true;
	
	public void addChild(CanvasNode child) {
		child.canvas = canvas;
		child.canvasGraph = canvasGraph;
		child.parent = this;
		child.markGeometryDirty();
		//TODO add to graph map
	}
	
	public void markGeometryDirty() {
		if(!geometryDirty) {
			geometryDirty = true;
			for(int i = 0; i < children.size; i++) {
				children.get(i).markGeometryDirty();
			}
		}
	}
	
	public void markUniformsDirty() {
		if(!uniformsDirty) {
			uniformsDirty = true;
			for(int i = 0; i < children.size; i++) {
				children.get(i).markUniformsDirty();
			}
		}
	}
	
	/*void appendCalls(Array<GlCall> calls) {
		if(drawPathCallRecord != null) {
			calls.addAll(drawPathCallRecord.getCalls());
		}
		
		if(drawTrianglesCallRecord != null) {
			calls.addAll(drawTrianglesCallRecord.getCalls());
		}
	}*/
}
