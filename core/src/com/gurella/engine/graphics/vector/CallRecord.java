package com.gurella.engine.graphics.vector;

import com.badlogic.gdx.utils.Array;

//TODO unused
public abstract class CallRecord {
	Canvas canvas;
	final CanvasState lastState = new CanvasState();
	final Array<GlCall> calls = new Array<GlCall>();
	
	CallRecord(Canvas canvas) {
		this.canvas = canvas;
	}

	public Array<GlCall> getCalls() {
		calls.clear();
		updateCanvasState(canvas.currentState);
		lastState.set(canvas.currentState);
		initCalls(calls);
		return calls;
	}
	
	abstract void initCalls(Array<GlCall> calls);
	
	abstract void updateCanvasState(CanvasState newState);

	static abstract class PathCallRecord extends CallRecord {
		final Path path = Path.obtain();
		
		PathCallRecord(Canvas canvas, Path path) {
			super(canvas);
			this.path.set(path);
		}
		
		public boolean updatePath(Path path) {
			if(this.path.equals(path)) {
				return false;
			} else {
				this.path.set(path);
				invalidateGeometry();
				return true;
			}
		}

		abstract void invalidateGeometry();
	}
	
	public static class ClipPathCallRecord extends PathCallRecord {
		private GlCall fillCall;
		
		public ClipPathCallRecord(Canvas canvas, Path path) {
			super(canvas, path);
		}
		
		@Override
		void invalidateGeometry() {
			if(fillCall != null) {
				fillCall.free();
				fillCall = null;
			}
		}

		@Override
		void initCalls(Array<GlCall> calls) {
			if(fillCall == null) {
				PathMesh pathMesh = PathMesh.obtain(canvas, path);
				fillCall = pathMesh.createFillCall();
				pathMesh.free();
			}
			calls.add(fillCall);
		}
		
		@Override
		void updateCanvasState(CanvasState newState) {
			if(fillCall != null && !lastState.xform.equals(newState.xform)) {
				fillCall.free();
				fillCall = null;
			}
		}
	}
	
	public static class DrawPathCallRecord extends PathCallRecord {
		private GlCall fillCall;
		private GlCall strokeCall;
		
		public DrawPathCallRecord(Canvas canvas, Path path) {
			super(canvas, path);
		}
		
		@Override
		void invalidateGeometry() {
			if(fillCall != null) {
				fillCall.free();
				fillCall = null;
			}
		}

		@Override
		void initCalls(Array<GlCall> calls) {
			PathMesh pathMesh = PathMesh.obtain(canvas, path);
			
			if (canvas.currentState.drawingStyle.drawFill()) {
				if (fillCall == null) {
					fillCall = pathMesh.createFillCall();
					pathMesh.free();
				}
				calls.add(fillCall);
			}
			
			if (canvas.currentState.drawingStyle.drawStroke()) {
				if (strokeCall == null) {
					fillCall = pathMesh.createStrokeCall();
					pathMesh.free();
				}
				calls.add(fillCall);
			}
		}
		
		@Override
		void updateCanvasState(CanvasState newState) {
			if(fillCall != null) {
				if (!lastState.xform.equals(newState.xform)) {
					fillCall.free();
					fillCall = null;
				} else if(!lastState.fillPaint.equals(newState.fillPaint) || !lastState.scissor.equals(newState.scissor)) {
					fillCall.getUniforms(0).init(newState.xform, newState.globalAlpha, newState.fillPaint, newState.scissor, canvas.fringeWidth, canvas.fringeWidth, -1.0f, false);
				}
			}
			
			if(strokeCall != null) {
				if (!lastState.xform.equals(newState.xform)
					|| lastState.strokeWidth != newState.strokeWidth
					|| lastState.miterLimit != newState.miterLimit
					|| lastState.lineJoin != newState.lineJoin
					|| lastState.lineCap != newState.lineCap
					|| lastState.dashOffset != newState.dashOffset
					|| !lastState.dashArray.equals(newState.dashArray)) {
					strokeCall.free();
					strokeCall = null;
				} else if(!lastState.strokePaint.equals(newState.strokePaint) || !lastState.scissor.equals(newState.scissor)) {
					strokeCall.getUniforms(0).init(newState.xform, newState.globalAlpha, newState.strokePaint, newState.scissor, newState.strokeWidth, canvas.fringeWidth, -1.0f, false);
					if (canvas.isStencilStrokes()) {
						strokeCall.getUniforms(0).init(newState.xform, newState.globalAlpha, newState.strokePaint, newState.scissor, newState.strokeWidth, canvas.fringeWidth, 1.0f - 0.5f / 255.0f, false);
					}
				}
			}
		}
	}
	
	public static class DrawTrianglesCallRecord extends CallRecord {
		private final Array<Vertex> triangleVertices = new Array<Vertex>();
		private VertexMode vertexMode;
		private boolean textured;
		private GlCall fillCall;
		
		DrawTrianglesCallRecord(Canvas canvas, VertexMode vertexMode, boolean textured, Vertex... vertices) {
			super(canvas);
			this.vertexMode = vertexMode;
			this.textured = textured;
			triangleVertices.addAll(vertices);
		}
		
		public void updateVertices(Vertex... vertices) {
			triangleVertices.clear();
			triangleVertices.addAll(vertices);
			if(fillCall != null) {
				fillCall.free();
				fillCall = null;
			}
		}
		
		public void setVertexMode(VertexMode vertexMode) {
			this.vertexMode = vertexMode;
			if(fillCall != null) {
				fillCall.vertexMode = vertexMode;
			}
		}
		
		public void setTextured(boolean textured) {
			this.textured = textured;
			if(fillCall != null) {
				fillCall.getUniforms(0).texturedVertices = textured;
			}
		}

		@Override
		void initCalls(Array<GlCall> calls) {
			if(fillCall == null) {
				TrianglesMesh trianglesMesh = TrianglesMesh.obtain(canvas);
				fillCall = trianglesMesh.createCall(vertexMode, textured, triangleVertices);
				trianglesMesh.free();
			}
			calls.add(fillCall);
		}

		@Override
		void updateCanvasState(CanvasState newState) {
			if(fillCall != null) {
				if (!lastState.xform.equals(newState.xform)) {
					fillCall.free();
					fillCall = null;
				} else if(!lastState.fillPaint.equals(newState.fillPaint) || !lastState.scissor.equals(newState.scissor)) {
					fillCall.getUniforms(0).init(newState.xform, newState.globalAlpha, newState.fillPaint, newState.scissor, canvas.fringeWidth, canvas.fringeWidth, -1.0f, textured);
				}
			}
		}
	}
}
