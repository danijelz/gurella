package com.gurella.engine.graphics.vector;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.graphics.vector.GlCall.CallType;
import com.badlogic.gdx.utils.Pools;

class PathMesh implements PathConstants, Poolable {
	Canvas canvas;
	
	final Array<PathComponent> components = new Array<PathComponent>();
	final Array<PathComponent> dashedStrokeComponents = new Array<PathComponent>();
	private Array<PathComponent> strokeComponents;
	
	final float[] bounds = new float[]{1e6f, 1e6f, -1e6f, -1e6f};
	
	final Dasher dasher = new Dasher();
	
	private PathComponent lastComponent;
	private Point lastPoint;
	
	public static PathMesh obtain(Canvas canvas, Path path) {
		PathMesh pathMesh = Pools.obtain(PathMesh.class);
		pathMesh.canvas = canvas;
		pathMesh.tesselatePath(path.getTansformedCommands(canvas.currentState.xform));
		return pathMesh;
	}
	
	private void tesselatePath(FloatArray commands) {
		boolean firstComponent = true;
		addComponent();
		
		int i = 0;
		while (i < commands.size) {
			int cmd = (int) commands.get(i);
			switch (cmd) {
			case moveTo:
				if (!firstComponent) {
					addComponent();
				} else {
					firstComponent = false;
				}
				addPoint(commands.get(i + 1), commands.get(i + 2), PT_CORNER);
				i += 3;
				break;
			case lineTo:
				if(firstComponent) {
					addPoint(0, 0, PT_CORNER);
					firstComponent = false;
				}
				addPoint(commands.get(i + 1), commands.get(i + 2), PT_CORNER);
				i += 3;
				break;
			case cubicTo:
				if(firstComponent) {
					addPoint(0, 0, PT_CORNER);
					firstComponent = false;
				}
				tesselateBezier(lastPoint.x, lastPoint.y,
						commands.get(i + 1), commands.get(i + 2),
						commands.get(i + 3), commands.get(i + 4),
						commands.get(i + 5), commands.get(i + 6),
						0, PT_CORNER);
				i += 7;
				break;
			case close:
				closePath();
				i++;
				break;
			case winding:
				pathWinding(Winding.values()[(int) commands.get(i + 1)]);
				i += 2;
				break;
			default:
				i++;
			}
		}
		
		refineLastComponent();
		lastComponent = null;
		lastPoint = null;
	}

	private void addComponent() {
		refineLastComponent();
		lastComponent = PathComponent.obtain(this);
		components.add(lastComponent);
		lastPoint = null;
	}

	private void refineLastComponent() {
		if(lastComponent != null) {
			lastComponent.refine();
			float[] componentBounds = lastComponent.bounds;
			bounds[0] = Math.min(bounds[0], componentBounds[0]);
			bounds[1] = Math.min(bounds[1], componentBounds[1]);
			bounds[2] = Math.max(bounds[2], componentBounds[2]);
			bounds[3] = Math.max(bounds[3], componentBounds[3]);
		}
	}

	private void addPoint(float x, float y, int flags) {
		if (lastComponent == null) {
			return;
		}

		Array<Point> points = lastComponent.points;
		if (points.size > 0 && lastPoint != null && lastPoint.pointEquals(x, y, Canvas.distanceTolerance)) {
			lastPoint.flags |= flags;
		} else {
			lastPoint = Point.obtain();
			points.add(lastPoint);
			lastPoint.x = x;
			lastPoint.y = y;
			lastPoint.flags = flags;
		}
	}
	
	public static boolean pointEquals(float x1, float y1, float x2, float y2, float tol) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		return dx * dx + dy * dy < tol * tol;
	}

	private void closePath() {
		if (lastComponent != null) {
			lastComponent.closed = true;
		}
	}

	private void pathWinding(Winding winding) {
		if (lastComponent != null) {
			lastComponent.winding = winding;
		}
	}

	private void tesselateBezier(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int level, int type) {
		if (level > 10) {
			return;
		} else if (level == 10 || checkTesselationTolerance(x1, y1, x2, y2, x3, y3, x4, y4)) {
			addPoint(x4, y4, type);
			return;
		}

		float x12 = (x1 + x2) * 0.5f;
		float y12 = (y1 + y2) * 0.5f;
		float x23 = (x2 + x3) * 0.5f;
		float y23 = (y2 + y3) * 0.5f;
		float x34 = (x3 + x4) * 0.5f;
		float y34 = (y3 + y4) * 0.5f;
		float x123 = (x12 + x23) * 0.5f;
		float y123 = (y12 + y23) * 0.5f;
		float x234 = (x23 + x34) * 0.5f;
		float y234 = (y23 + y34) * 0.5f;
		float x1234 = (x123 + x234) * 0.5f;
		float y1234 = (y123 + y234) * 0.5f;

		tesselateBezier(x1, y1, x12, y12, x123, y123, x1234, y1234, level + 1, 0);
		tesselateBezier(x1234, y1234, x234, y234, x34, y34, x4, y4, level + 1, type);
	}
	
	private boolean checkTesselationTolerance(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		float dx = x4 - x1;
		float dy = y4 - y1;
		float d2 = Math.abs(((x2 - x4) * dy - (y2 - y4) * dx));
		float d3 = Math.abs(((x3 - x4) * dy - (y3 - y4) * dx));

		return ((d2 + d3) * (d2 + d3) < canvas.tesselationTolerance * (dx * dx + dy * dy));
	}

	GlCall createFillCall() {
		GlCall fillCall = GlCall.obtain();
		fillCall.callType = isConvex() ? CallType.convexFill : CallType.fill;
		fillCall.blendMode = canvas.getBlendMode();

		for (int i = 0; i < components.size; i++) {
			PathComponent pathComponent = components.get(i);
			fillCall.components.add(pathComponent.createFillComponent());
		}

		addFillQuad(fillCall);
		setupFillUniforms(fillCall);
		
		return fillCall;
	}
	
	boolean isConvex() {
		return components.size == 1 && components.get(0).convex;
	}

	private void addFillQuad(GlCall fillCall) {
		fillCall.newTriangleVertex(bounds[0], bounds[3], 0.5f, 1.0f);
		fillCall.newTriangleVertex(bounds[2], bounds[3], 0.5f, 1.0f);
		fillCall.newTriangleVertex(bounds[2], bounds[1], 0.5f, 1.0f);

		fillCall.newTriangleVertex(bounds[0], bounds[3], 0.5f, 1.0f);
		fillCall.newTriangleVertex(bounds[2], bounds[1], 0.5f, 1.0f);
		fillCall.newTriangleVertex(bounds[0], bounds[1], 0.5f, 1.0f);
	}

	private void setupFillUniforms(GlCall fillCall) {
		CanvasState state = canvas.currentState;
		fillCall.newUniform(state.xform, state.globalAlpha, state.scissor, state.fillPaint, canvas.fringeWidth, canvas.fringeWidth, -1.0f);
	}
	
	GlCall createStrokeCall() {
		prepareStrokeComponents();
		
		GlCall strokeCall = GlCall.obtain();
		strokeCall.callType = CallType.stroke;
		strokeCall.blendMode = canvas.getBlendMode();
		
		for (int i = 0; i < strokeComponents.size; i++) {
			PathComponent pathComponent = strokeComponents.get(i);
			strokeCall.components.add(pathComponent.createStrokeComponent());
		}

		setupStrokeUniforms(strokeCall);
		return strokeCall;
	}

	private void prepareStrokeComponents() {
		if(canvas.currentState.isDashedStroke()) {
			createDashedStrokeComponents();
			strokeComponents = dashedStrokeComponents;
		} else {
			strokeComponents = components;
		}
	}
	
	private void createDashedStrokeComponents() {
		//TODO check if needs component recreation
		CanvasUtils.resetArray(dashedStrokeComponents);
		CanvasState currentState = canvas.currentState;
		dasher.init(currentState.dashArray, currentState.dashOffset);
		
		for (int i = 0; i < components.size; i++) {
			PathComponent pathComponent = components.get(i);
			dasher.appendDashedStrokeComponents(dashedStrokeComponents, pathComponent);
		}
		
		dasher.reset();
	}

	private void setupStrokeUniforms(GlCall strokeCall) {
		CanvasState state = canvas.currentState;
		strokeCall.newUniform(state.xform, state.globalAlpha, state.scissor, state.strokePaint, state.strokeWidth, canvas.fringeWidth, -1.0f);
		if (canvas.isStencilStrokes()) {
			strokeCall.newUniform(state.xform, state.globalAlpha, state.scissor, state.strokePaint, state.strokeWidth, canvas.fringeWidth, 1.0f - 0.5f / 255.0f);
		}
	}

	@Override
	public void reset() {
		canvas = null;
		bounds[0] = bounds[1] = 1e6f;
		bounds[2] = bounds[3] = -1e6f;
		CanvasUtils.resetArray(components);
		CanvasUtils.resetArray(dashedStrokeComponents);
		strokeComponents = null;
	}
	
	public void free() {
		Pools.free(this);
	}
}
/*
Point[x= 104.488, y=270.0,   dx=-0.077909544, dy=-0.9969604,  len=4.1586366,  dmx=-1.081196,   dmy=-1.0,        flags=43], 
Point[x= 104.164, y=265.854, dx=-1.0,         dy=0.0,         len=1.0319977,  dmx=-0.9249017,  dmy=1.0,         flags=43], 
Point[x= 103.132, y=265.854, dx=-0.24300909,  dy=0.97002405,  len=2.9875655,  dmx=0.7803837,   dmy=1.0,         flags=43], 
Point[x= 102.406, y=268.752, dx=-0.25990054,  dy=-0.96563536, len=3.0011435,  dmx=0.03470179,  dmy=3.9765546,   flags=57], 
Point[x= 101.626, y=265.854, dx=-1.0,         dy=0.0,         len=1.0199966,  dmx=-0.76643777, dmy=1.0,         flags=43], 
Point[x= 100.606, y=265.854, dx=-0.07934436,  dy=0.9968473,   len=4.1591086,  dmx=0.92356735,  dmy=1.0,         flags=43], 
Point[x= 100.276, y=270.0,   dx=1.0,          dy=0.0,         len=0.76799774, dmx=1.0827581,   dmy=-1.0,        flags=43], 
Point[x= 101.044, y=270.0,   dx=0.059596453,  dy=-0.9982226,  len=2.0135887,  dmx=-0.9420781,  dmy=-1.0,        flags=43], 
Point[x= 101.164, y=267.99,  dx=0.0044223457, dy=-0.9999902,  len=1.3560008,  dmx=-0.9998681,  dmy=-0.0320338,  flags=3], 
Point[x= 101.17,  y=266.634, dx=0.2716021,    dy=0.9624096,   len=3.048588,   dmx=-0.9685501,  dmy=-7.1138678,  flags=57], 
Point[x= 101.998, y=269.568, dx=1.0,          dy=0.0,         len=0.76200104, dmx=0.75684804,  dmy=-1.0,        flags=43], 
Point[x= 102.76,  y=269.568, dx=0.2532272,    dy=-0.96740675, len=3.0328405,  dmx=-0.7719325,  dmy=-1.0000001,  flags=43], 
Point[x= 103.528, y=266.634, dx=0.04201971,   dy=0.99911684,  len=1.2851316,  dmx=0.71924317,  dmy=-6.696743,   flags=57], 
Point[x= 103.582, y=267.918, dx=0.06327167,   dy=0.9979963,   len=2.086181,   dmx=0.9986696,   dmy=-0.05265165, flags=3], 
Point[x= 103.714, y=270.0,   dx=1.0,          dy=0.0,         len=0.7740021,  dmx=0.938609,    dmy=-1.0000001,  flags=43]]


2.168986
1.8554422
1.6089993
15.814161
1.5874257
1.8529779
2.1723635
1.887515
1.0007623
51.54453
1.5728208
1.5958792
45.363754
1.0001134
1.8809851


Commands:
moveTo: 104.488, 270.0
lineTo: 104.164, 265.854
lineTo: 103.132, 265.854
lineTo: 102.406, 268.752
lineTo: 102.382, 268.752
lineTo: 101.626, 265.854
lineTo: 100.606, 265.854
lineTo: 100.276, 270.0
lineTo: 101.044, 270.0
lineTo: 101.164, 267.99
cubicTo: 101.194, 267.486,101.194, 266.874,101.17, 266.634
lineTo: 101.194, 266.634
lineTo: 101.998, 269.568
lineTo: 102.76, 269.568
lineTo: 103.528, 266.634
lineTo: 103.552, 266.634
cubicTo: 103.54, 266.85,103.552, 267.414,103.582, 267.918
lineTo: 103.714, 270.0
close

 */