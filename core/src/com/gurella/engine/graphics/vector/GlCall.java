package com.gurella.engine.graphics.vector;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;

class GlCall implements Poolable {
	CallType callType = CallType.none;
	BlendMode blendMode = BlendMode.over;

	final GlUniforms uniform = new GlUniforms();
	final GlUniforms stencilStrokesUniform = new GlUniforms();
	final Array<GlPathComponent> components = new Array<GlPathComponent>();

	int triangleVerticesOffset = -1;
	final Array<Vertex> triangleVertices = new Array<Vertex>();
	VertexMode vertexMode = VertexMode.triangles;

	final Array<Clip> clips = new Array<Clip>();

	static GlCall obtain() {
		return Pools.obtain(GlCall.class);
	}

	static GlCall obtainFrameBufferRenderCall() {
		GlCall call = Pools.obtain(GlCall.class);
		call.callType = CallType.triangles;
		call.uniform.initForFrameBuffer();
		return call;
	}

	void newTriangleVertex(float x, float y, float u, float v) {
		triangleVertices.add(Vertex.obtain(x, y, u, v));
	}

	void initUniform(AffineTransform globalXform, float globalAlpha, Scissor scissor, Paint paint, float width,
			float fringe, float strokeThr) {
		uniform.init(globalXform, globalAlpha, paint, scissor, width, fringe, strokeThr, false);
	}

	void initUniform(AffineTransform globalXform, float globalAlpha, Scissor scissor, Paint paint, float width,
			float fringe, float strokeThr, boolean texturedVertices) {
		uniform.init(globalXform, globalAlpha, paint, scissor, width, fringe, strokeThr, texturedVertices);
	}

	void initStrokesUniform(AffineTransform globalXform, float globalAlpha, Scissor scissor, Paint paint, float width,
			float fringe, float strokeThr) {
		stencilStrokesUniform.init(globalXform, globalAlpha, paint, scissor, width, fringe, strokeThr, false);
	}

	@Override
	public void reset() {
		callType = CallType.none;
		blendMode = BlendMode.over;
		uniform.reset();
		stencilStrokesUniform.reset();
		triangleVerticesOffset = -1;
		CanvasUtils.resetArray(triangleVertices);
		vertexMode = VertexMode.triangles;
		CanvasUtils.resetArray(components);
		clips.clear();
	}

	void free() {
		Pools.free(this);
	}

	enum CallType {
		none, fill, convexFill, stroke, triangles;
	}
}
