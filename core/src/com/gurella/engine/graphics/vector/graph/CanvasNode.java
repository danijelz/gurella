package com.gurella.engine.graphics.vector.graph;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.graphics.vector.AffineTransform;
import com.gurella.engine.graphics.vector.BlendMode;
import com.gurella.engine.graphics.vector.Canvas;
import com.gurella.engine.graphics.vector.DrawingStyle;
import com.gurella.engine.graphics.vector.Effect;
import com.gurella.engine.graphics.vector.Font;
import com.gurella.engine.graphics.vector.LineCap;
import com.gurella.engine.graphics.vector.LineJoin;
import com.gurella.engine.graphics.vector.Paint;
import com.gurella.engine.graphics.vector.Path;
import com.gurella.engine.graphics.vector.PointStyle;
import com.gurella.engine.graphics.vector.Scissor;
import com.gurella.engine.graphics.vector.Vertex;
import com.gurella.engine.graphics.vector.VertexMode;
import com.gurella.engine.graphics.vector.Winding;
import com.gurella.engine.graphics.vector.CallRecord.DrawPathCallRecord;
import com.gurella.engine.graphics.vector.CallRecord.DrawTrianglesCallRecord;

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
