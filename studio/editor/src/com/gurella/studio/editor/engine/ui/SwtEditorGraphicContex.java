package com.gurella.studio.editor.engine.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.editor.ui.EditorFont;
import com.gurella.engine.editor.ui.EditorGraphicContex;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.math.GridRectangle;

public class SwtEditorGraphicContex implements EditorGraphicContex {
	private Widget owner;
	private GC gc;
	private Transform transform;
	private float[] transformElements;

	public SwtEditorGraphicContex(Control owner) {
		this.owner = owner;
		this.gc = new GC(owner);
		owner.addDisposeListener(e -> gc.dispose());
	}

	public SwtEditorGraphicContex(Widget owner, GC gc) {
		this.owner = owner;
		this.gc = gc;
	}

	@Override
	public void copyArea(EditorImage image, int x, int y) {
		gc.copyArea(((SwtEditorImage) image).image, x, y);
	}

	@Override
	public void copyArea(int srcX, int srcY, int width, int height, int destX, int destY) {
		gc.copyArea(srcX, srcY, width, height, destX, destY);
	}

	@Override
	public void copyArea(int srcX, int srcY, int width, int height, int destX, int destY, boolean paint) {
		gc.copyArea(srcX, srcY, width, height, destX, destY, paint);
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		gc.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void drawFocus(int x, int y, int width, int height) {
		gc.drawFocus(x, y, width, height);
	}

	@Override
	public void drawImage(EditorImage image, int x, int y) {
		gc.drawImage(((SwtEditorImage) image).image, x, y);
	}

	@Override
	public void drawImage(EditorImage image, int srcX, int srcY, int srcWidth, int srcHeight, int destX, int destY,
			int destWidth, int destHeight) {
		gc.drawImage(((SwtEditorImage) image).image, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth,
				destHeight);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		gc.drawLine(x1, y1, x2, y2);
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		gc.drawOval(x, y, width, height);
	}

	@Override
	public void drawPoint(int x, int y) {
		gc.drawPoint(x, y);
	}

	@Override
	public void drawPolygon(int[] pointArray) {
		gc.drawPolygon(pointArray);
	}

	@Override
	public void drawPolyline(int[] pointArray) {
		gc.drawPolyline(pointArray);
	}

	@Override
	public void drawRectangle(int x, int y, int width, int height) {
		gc.drawRectangle(x, y, width, height);
	}

	@Override
	public void drawRoundRectangle(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		gc.drawRoundRectangle(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void drawString(String string, int x, int y) {
		gc.drawString(string, x, y);
	}

	@Override
	public void drawString(String string, int x, int y, boolean isTransparent) {
		gc.drawString(string, x, y, isTransparent);
	}

	@Override
	public void drawText(String string, int x, int y) {
		gc.drawText(string, x, y);
	}

	@Override
	public void drawText(String string, int x, int y, boolean isTransparent) {
		gc.drawText(string, x, y, isTransparent);
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		gc.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void fillGradientRectangle(int x, int y, int width, int height, boolean vertical) {
		gc.fillGradientRectangle(x, y, width, height, vertical);
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		gc.fillOval(x, y, width, height);
	}

	@Override
	public void fillPolygon(int[] pointArray) {
		gc.fillPolygon(pointArray);
	}

	@Override
	public void fillRectangle(int x, int y, int width, int height) {
		gc.fillRectangle(x, y, width, height);
	}

	@Override
	public void fillRoundRectangle(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		gc.fillRoundRectangle(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public boolean getAdvanced() {
		return gc.getAdvanced();
	}

	@Override
	public int getAdvanceWidth(char ch) {
		return gc.getAdvanceWidth(ch);
	}

	@Override
	public int getAlpha() {
		return gc.getAlpha();
	}

	@Override
	public Antialias getAntialias() {
		switch (gc.getAntialias()) {
		case SWT.DEFAULT:
			return Antialias.DEFAULT;
		case SWT.ON:
			return Antialias.ON;
		case SWT.OFF:
			return Antialias.OFF;
		default:
			return Antialias.DEFAULT;
		}
	}

	@Override
	public Color getBackground() {
		return SwtEditorWidget.toGdxColor(gc.getBackground());
	}

	@Override
	public int getCharWidth(char ch) {
		return gc.getCharWidth(ch);
	}

	@Override
	public GridRectangle getClipping() {
		Rectangle rect = gc.getClipping();
		return new GridRectangle(rect.x, rect.y, rect.width, rect.height);
	}

	@Override
	public FillRule getFillRule() {
		switch (gc.getFillRule()) {
		case SWT.FILL_EVEN_ODD:
			return FillRule.EVEN_ODD;
		case SWT.FILL_WINDING:
			return FillRule.WINDING;
		default:
			return null;
		}
	}

	@Override
	public Color getForeground() {
		return SwtEditorWidget.toGdxColor(gc.getForeground());
	}

	@Override
	public Interpolation getInterpolation() {
		switch (gc.getInterpolation()) {
		case SWT.DEFAULT:
			return Interpolation.DEFAULT;
		case SWT.NONE:
			return Interpolation.NONE;
		case SWT.LOW:
			return Interpolation.LOW;
		case SWT.HIGH:
			return Interpolation.HIGH;
		default:
			return null;
		}
	}

	@Override
	public LineCap getLineCap() {
		switch (gc.getLineCap()) {
		case SWT.CAP_FLAT:
			return LineCap.FLAT;
		case SWT.CAP_ROUND:
			return LineCap.ROUND;
		case SWT.CAP_SQUARE:
			return LineCap.SQUARE;
		default:
			return null;
		}
	}

	@Override
	public int[] getLineDash() {
		return gc.getLineDash();
	}

	@Override
	public float getDashOffset() {
		return gc.getLineAttributes().dashOffset;
	}

	@Override
	public void setDashOffset(float dashOffset) {
		gc.getLineAttributes().dashOffset = dashOffset;
	}

	@Override
	public float getMiterLimit() {
		return gc.getLineAttributes().miterLimit;
	}

	@Override
	public void setMiterLimit(float miterLimit) {
		gc.getLineAttributes().miterLimit = miterLimit;
	}

	@Override
	public LineJoin getLineJoin() {
		switch (gc.getLineJoin()) {
		case SWT.JOIN_BEVEL:
			return LineJoin.BEVEL;
		case SWT.JOIN_MITER:
			return LineJoin.MITER;
		case SWT.JOIN_ROUND:
			return LineJoin.ROUND;
		default:
			return null;
		}
	}

	@Override
	public LineStyle getLineStyle() {
		switch (gc.getLineStyle()) {
		case SWT.LINE_CUSTOM:
			return LineStyle.CUSTOM;
		case SWT.LINE_DASH:
			return LineStyle.DASH;
		case SWT.LINE_DASHDOT:
			return LineStyle.DASHDOT;
		case SWT.LINE_DASHDOTDOT:
			return LineStyle.DASHDOTDOT;
		case SWT.LINE_DOT:
			return LineStyle.DOT;
		case SWT.LINE_SOLID:
			return LineStyle.SOLID;
		default:
			return null;
		}
	}

	@Override
	public int getLineWidth() {
		return gc.getLineWidth();
	}

	@Override
	public Antialias getTextAntialias() {
		switch (gc.getTextAntialias()) {
		case SWT.DEFAULT:
			return Antialias.DEFAULT;
		case SWT.ON:
			return Antialias.ON;
		case SWT.OFF:
			return Antialias.OFF;
		default:
			return Antialias.DEFAULT;
		}
	}

	@Override
	public boolean isClipped() {
		return gc.isClipped();
	}

	@Override
	public void setAdvanced(boolean advanced) {
		gc.setAdvanced(advanced);
	}

	@Override
	public void setAlpha(int alpha) {
		gc.setAlpha(alpha);
	}

	@Override
	public void setAntialias(Antialias antialias) {
		switch (antialias) {
		case DEFAULT:
			gc.setAntialias(SWT.DEFAULT);
			break;
		case ON:
			gc.setAntialias(SWT.ON);
			break;
		case OFF:
			gc.setAntialias(SWT.OFF);
			break;
		default:
			gc.setAntialias(SWT.DEFAULT);
			break;
		}
	}

	@Override
	public void setBackground(Color color) {
		gc.setBackground(SwtEditorUi.toSwtColor(owner, color));
	}

	@Override
	public void setBackground(int r, int g, int b, int a) {
		gc.setBackground(SwtEditorUi.toSwtColor(owner, r, g, b, a));
	}

	@Override
	public void setClipping(int x, int y, int width, int height) {
		gc.setClipping(x, y, width, height);
	}

	@Override
	public void setFillRule(FillRule rule) {
		switch (rule) {
		case EVEN_ODD:
			gc.setFillRule(SWT.FILL_EVEN_ODD);
			break;
		case WINDING:
			gc.setFillRule(SWT.FILL_WINDING);
			break;
		default:
			throw new IllegalArgumentException("Unsupported fill rule: " + rule.name());
		}
	}

	@Override
	public void setFont(EditorFont font) {
		gc.setFont(font == null ? null : ((SwtEditorFont) font).font);
	}

	@Override
	public void setForeground(Color color) {
		gc.setForeground(SwtEditorUi.toSwtColor(owner, color));
	}

	@Override
	public void setForeground(int r, int g, int b, int a) {
		gc.setForeground(SwtEditorUi.toSwtColor(owner, r, g, b, a));
	}

	@Override
	public void setInterpolation(Interpolation interpolation) {
		switch (interpolation) {
		case DEFAULT:
			gc.setInterpolation(SWT.DEFAULT);
			return;
		case NONE:
			gc.setInterpolation(SWT.NONE);
			return;
		case LOW:
			gc.setInterpolation(SWT.LOW);
			return;
		case HIGH:
			gc.setInterpolation(SWT.HIGH);
			return;
		default:
			return;
		}
	}

	@Override
	public void setLineCap(LineCap cap) {
		switch (cap) {
		case FLAT:
			gc.setLineCap(SWT.CAP_FLAT);
			return;
		case ROUND:
			gc.setLineCap(SWT.CAP_ROUND);
			return;
		case SQUARE:
			gc.setLineCap(SWT.CAP_SQUARE);
			return;
		default:
			return;
		}
	}

	@Override
	public void setLineDash(int[] dashes) {
		gc.setLineDash(dashes);
	}

	@Override
	public void setLineJoin(LineJoin join) {
		switch (join) {
		case BEVEL:
			gc.setLineJoin(SWT.JOIN_BEVEL);
			return;
		case MITER:
			gc.setLineJoin(SWT.JOIN_MITER);
			return;
		case ROUND:
			gc.setLineJoin(SWT.JOIN_ROUND);
			return;
		default:
			return;
		}
	}

	@Override
	public void setLineStyle(LineStyle lineStyle) {
		switch (lineStyle) {
		case CUSTOM:
			gc.setLineStyle(SWT.LINE_CUSTOM);
			return;
		case DASH:
			gc.setLineStyle(SWT.LINE_DASH);
			return;
		case DASHDOT:
			gc.setLineStyle(SWT.LINE_DASHDOT);
			return;
		case DASHDOTDOT:
			gc.setLineStyle(SWT.LINE_DASHDOTDOT);
			return;
		case DOT:
			gc.setLineStyle(SWT.LINE_DOT);
			return;
		case SOLID:
			gc.setLineStyle(SWT.LINE_SOLID);
			return;
		default:
			return;
		}
	}

	@Override
	public void setLineWidth(int lineWidth) {
		gc.setLineWidth(lineWidth);
	}

	@Override
	public void setTextAntialias(Antialias antialias) {
		switch (antialias) {
		case DEFAULT:
			gc.setTextAntialias(SWT.DEFAULT);
			break;
		case ON:
			gc.setTextAntialias(SWT.ON);
			break;
		case OFF:
			gc.setTextAntialias(SWT.OFF);
			break;
		default:
			gc.setTextAntialias(SWT.DEFAULT);
			break;
		}
	}

	@Override
	public Affine2 getTransform(Affine2 out) {
		getTransform().getElements(transformElements);
		out.m00 = transformElements[0];
		out.m01 = transformElements[1];
		out.m02 = transformElements[4];
		out.m10 = transformElements[2];
		out.m11 = transformElements[3];
		out.m12 = transformElements[5];
		return out;
	}

	private Transform getTransform() {
		if (transform == null) {
			transform = new Transform(owner.getDisplay());
			owner.addDisposeListener(e -> transform.dispose());
			transformElements = new float[6];
		}
		return transform;
	}

	@Override
	public void setTransform(Affine2 transform) {
		Transform swtTransform = getTransform();
		swtTransform.setElements(transform.m00, transform.m01, transform.m10, transform.m11, transform.m02,
				transform.m12);
		gc.setTransform(swtTransform);
	}

	@Override
	public GridPoint2 stringExtent(String string) {
		Point point = gc.stringExtent(string);
		return new GridPoint2(point.x, point.y);
	}

	@Override
	public GridPoint2 textExtent(String string) {
		Point point = gc.textExtent(string);
		return new GridPoint2(point.x, point.y);
	}

	@Override
	public EditorFont getFont() {
		return SwtEditorWidget.toEditorFont(gc.getFont());
	}

	@Override
	public int getFontAscent() {
		return gc.getFontMetrics().getAscent();
	}

	@Override
	public int getFontDescent() {
		return gc.getFontMetrics().getDescent();
	}

	@Override
	public int getFontHeight() {
		return gc.getFontMetrics().getHeight();
	}

	@Override
	public int getFontLeading() {
		return gc.getFontMetrics().getLeading();
	}

	@Override
	public int getFontAverageCharWidth() {
		return gc.getFontMetrics().getAverageCharWidth();
	}
}
