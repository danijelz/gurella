package com.gurella.engine.graphics.vector;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.graphics.vector.GlCall.CallType;

class TrianglesMesh implements Poolable {
	Canvas canvas;
	final Array<Vertex> triangleVertices = new Array<Vertex>();
	
	static TrianglesMesh obtain(Canvas canvas) {
		TrianglesMesh trianglesMesh = Pools.obtain(TrianglesMesh.class);
		trianglesMesh.canvas = canvas;
		return trianglesMesh;
	}
	
	private void newVertex(float x, float y, float u, float v) {
		triangleVertices.add(Vertex.obtain(x, y, u, v, canvas.currentState.xform));
	}
	
	public GlCall createTexturedCall(VertexMode mode, float... vertices) {
		addTexturedVertices(vertices);
		return createCall(mode, true);
	}
	
	private void addTexturedVertices(float... vertices) {
		if(vertices.length < 12) {
			return;
		}

		int i = 0;
		while(i <= vertices.length - 5) {
			newVertex(vertices[i++], vertices[i++], vertices[i++], vertices[i++]);
		}
	}
	
	public GlCall createTexturedCall(VertexMode mode, FloatArray vertices) {
		addTexturedVertices(vertices);
		return createCall(mode, true);
	}
	
	private void addTexturedVertices(FloatArray vertices) {
		if(vertices.size < 12) {
			return;
		}
		
		int i = 0;
		while(i <= vertices.size - 5) {
			newVertex(vertices.get(i++), vertices.get(i++), vertices.get(i++), vertices.get(i++));
		}
	}
	
	public GlCall createTexturedCall(VertexMode mode, Vector2... vertices) {
		addTexturedVertices(vertices);
		return createCall(mode, true);
	}
	
	private void addTexturedVertices(Vector2... vertices) {
		if(vertices.length < 6) {
			return;
		}

		int i = 0;
		while(i <= vertices.length - 3) {
			Vector2 xy = vertices[i++];
			Vector2 uv = vertices[i++];
			newVertex(xy.x, xy.y, uv.x, uv.y);
		}
	}
	
	public GlCall createTexturedCall(VertexMode mode, Vertex... vertices) {
		addTexturedVertices(vertices);
		return createCall(mode, true);
	}
	
	private void addTexturedVertices(Vertex... vertices) {
		if(vertices.length < 3) {
			return;
		}

		for(int i = 0; i < vertices.length - 1; i++) {
			Vertex vertex = vertices[i];
			newVertex(vertex.x, vertex.y, vertex.u, vertex.v);
		}
	}
	
	GlCall createCall(VertexMode mode, boolean textured, Array<Vertex> vertices) {
		triangleVertices.addAll(vertices);
		return createCall(mode, textured);
	}
	
	public GlCall createCall(VertexMode mode, float... vertices) {
		addVertices(vertices);
		return createCall(mode, false);
	}
	
	private void addVertices(float... vertices) {
		if(vertices.length < 6) {
			return;
		}

		int i = 0;
		while(i <= vertices.length - 3) {
			newVertex(vertices[i++], vertices[i++], 0.5f, 1);
		}
	}
	
	public GlCall createCall(VertexMode mode, FloatArray vertices) {
		addVertices(vertices);
		return createCall(mode, false);
	}
	
	private void addVertices(FloatArray vertices) {
		if(vertices.size < 6) {
			return;
		}
		
		int i = 0;
		while(i <= vertices.size - 3) {
			newVertex(vertices.get(i++), vertices.get(i++), 0.5f, 1);
		}
	}
	
	public GlCall createCall(VertexMode mode, Vector2... vertices) {
		addVertices(vertices);
		return createCall(mode, false);
	}
	
	private void addVertices(Vector2... vertices) {
		if(vertices.length < 3) {
			return;
		}
		
		for (int i = 0; i < vertices.length; i++) {
			Vector2 point = vertices[i];
			newVertex(point.x, point.y, 0.5f, 1);
		}
	}
	
	private GlCall createCall(VertexMode mode, boolean textured) {
		CanvasState currentState = canvas.currentState;
		GlCall call = GlCall.obtain();
		call.callType = CallType.triangles;
		call.blendMode = currentState.blendMode;
		call.clips.addAll(currentState.clips);
		call.vertexMode = mode;
		CanvasState state = currentState;
		call.initUniform(state.xform, state.globalAlpha, state.scissor, state.fillPaint, canvas.fringeWidth, canvas.fringeWidth, -1.0f, textured);
		call.triangleVertices.addAll(triangleVertices);
		triangleVertices.clear();
		return call;
	}
	
	@Override
	public void reset() {
		canvas = null;
	}
	
	public void free() {
		Pools.free(this);
	}
}
