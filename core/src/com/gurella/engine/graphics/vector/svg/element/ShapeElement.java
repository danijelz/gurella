package com.gurella.engine.graphics.vector.svg.element;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.FloatArray;
import com.gurella.engine.graphics.vector.AffineTransform;
import com.gurella.engine.graphics.vector.Canvas;
import com.gurella.engine.graphics.vector.DrawingStyle;
import com.gurella.engine.graphics.vector.LineCap;
import com.gurella.engine.graphics.vector.LineJoin;
import com.gurella.engine.graphics.vector.Path;
import com.gurella.engine.graphics.vector.svg.SvgRenderContext;
import com.gurella.engine.graphics.vector.svg.property.PropertyType;
import com.gurella.engine.graphics.vector.svg.property.value.Length;
import com.gurella.engine.graphics.vector.svg.property.value.Paint;

public abstract class ShapeElement extends KnownElement implements RenderableElement {
	private Path path = Path.obtain();
	private boolean geometryDirty = true;

	public ShapeElement(ElementType elementType) {
		super(elementType);
	}

	@Override
	public void renderGeometry(SvgRenderContext state) {
		float globalOpacity = getOpacity(PropertyType.opacity);
		if (globalOpacity == 0) {
			return;
		}

		Canvas canvas = state.canvas;
		boolean hasFill = initFillPaint(canvas, globalOpacity);
		boolean hasStroke = initStrokePaint(canvas, globalOpacity);

		DrawingStyle drawingStyle = DrawingStyle.valueOf(hasFill, hasStroke);
		if (drawingStyle == null) {
			return;
		}

		canvas.setDrawingStyle(drawingStyle);
		canvas.mulGlobalAlpha(globalOpacity);
		canvas.drawPath(getPath());
	}

	protected boolean initFillPaint(Canvas canvas, float globalOpacity) {
		Paint fillPaint = getPropertyOrDefault(PropertyType.fill);
		if(fillPaint == Paint.currentColor) {
			fillPaint = getPropertyOrDefault(PropertyType.color);
		}
		
		if (fillPaint == null || fillPaint == Paint.none) {
			return false;
		}
		
		float fillOpacity = getOpacity(PropertyType.fillOpacity);
		if (fillOpacity * globalOpacity > 0) {
			fillPaint.initCanvasPaint(getSvg(), canvas.getFillPaint(), fillOpacity);
			return true;
		} else {
			return false;
		}
	}

	protected boolean initStrokePaint(Canvas canvas, float globalOpacity) {
		Paint strokePaint = getPropertyOrDefault(PropertyType.stroke);
		if(strokePaint == Paint.currentColor) {
			strokePaint = getPropertyOrDefault(PropertyType.color);
		}
		
		if (strokePaint == null || strokePaint == Paint.none) {
			return false;
		}

		float strokeWidth = this.<Length> getPropertyOrDefault(PropertyType.strokeWidth).getPixels();
		if (strokeWidth <= 0) {
			return false;
		}

		float strokeOpacity = getOpacity(PropertyType.strokeOpacity);
		if (strokeOpacity * globalOpacity <= 0) {
			return false;
		}

		canvas.setStrokeWidth(strokeWidth);
		canvas.setStrokeLineCap(this.<LineCap> getPropertyOrDefault(PropertyType.strokeLinecap));
		canvas.setStrokeLineJoin(this.<LineJoin> getPropertyOrDefault(PropertyType.strokeLinejoin));
		canvas.setStrokeMiterLimit(this.<Float> getPropertyOrDefault(PropertyType.strokeMiterlimit).floatValue());

		FloatArray strokeDasharray = getPropertyOrDefault(PropertyType.strokeDasharray);
		if (strokeDasharray != null && strokeDasharray.size > 0) {
			canvas.setStrokeDashArray(strokeDasharray);
			canvas.setStrokeDashOffset(this.<Float> getPropertyOrDefault(PropertyType.strokeDashoffset).floatValue());
		}

		strokePaint.initCanvasPaint(getSvg(), canvas.getStrokePaint(), strokeOpacity);
		return true;
	}

	private float getOpacity(PropertyType propertyType) {
		float opacity = getPropertyOrDefault(propertyType);
		return MathUtils.clamp(opacity, 0, 1);
	}

	@Override
	protected Rectangle getElementBounds(Rectangle out, AffineTransform worldTransform) {
		getPath().getBounds(out, worldTransform);

		Paint stroke = getPropertyOrDefault(PropertyType.stroke);
		if (stroke == Paint.none) {
			return out;
		}

		float strokeWidth = this.<Length> getPropertyOrDefault(PropertyType.strokeWidth).getPixels();
		if (strokeWidth <= 0f) {
			return out;
		}

		float halfStrokeWidth = strokeWidth / 2;
		return out.set(out.x - halfStrokeWidth, out.y - halfStrokeWidth, out.width + strokeWidth, out.height + strokeWidth);
	}

	private Path getPath() {
		if (geometryDirty) {
			initPath(path);
			geometryDirty = false;
		}
		return path;
	}

	protected abstract void initPath(Path out);
}
