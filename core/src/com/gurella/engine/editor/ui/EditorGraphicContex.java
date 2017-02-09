package com.gurella.engine.editor.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.math.GridRectangle;

public interface EditorGraphicContex {
	void copyArea(EditorImage image, int x, int y);

	void copyArea(int srcX, int srcY, int width, int height, int destX, int destY);

	void copyArea(int srcX, int srcY, int width, int height, int destX, int destY, boolean paint);

	void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle);

	void drawFocus(int x, int y, int width, int height);

	void drawImage(EditorImage image, int x, int y);

	void drawImage(EditorImage image, int srcX, int srcY, int srcWidth, int srcHeight, int destX, int destY,
			int destWidth, int destHeight);

	void drawLine(int x1, int y1, int x2, int y2);

	void drawOval(int x, int y, int width, int height);

	void drawPoint(int x, int y);

	void drawPolygon(int[] pointArray);

	void drawPolyline(int[] pointArray);

	void drawRectangle(int x, int y, int width, int height);

	void drawRoundRectangle(int x, int y, int width, int height, int arcWidth, int arcHeight);

	void drawString(String string, int x, int y);

	void drawString(String string, int x, int y, boolean isTransparent);

	void drawText(String string, int x, int y);

	void drawText(String string, int x, int y, boolean isTransparent);

	void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle);

	void fillGradientRectangle(int x, int y, int width, int height, boolean vertical);

	void fillOval(int x, int y, int width, int height);

	void fillPolygon(int[] pointArray);

	void fillRectangle(int x, int y, int width, int height);

	void fillRoundRectangle(int x, int y, int width, int height, int arcWidth, int arcHeight);

	boolean getAdvanced();

	int getAdvanceWidth(char ch);

	int getAlpha();

	Antialias getAntialias();

	Color getBackground();

	int getCharWidth(char ch);

	GridRectangle getClipping();

	FillRule getFillRule();

	Color getForeground();

	Interpolation getInterpolation();

	LineCap getLineCap();

	int[] getLineDash();

	float getDashOffset();

	void setDashOffset(float dashOffset);

	float getMiterLimit();

	void setMiterLimit(float miterLimit);

	LineJoin getLineJoin();

	LineStyle getLineStyle();

	int getLineWidth();

	Antialias getTextAntialias();

	Affine2 getTransform(Affine2 out);

	boolean isClipped();

	void setAdvanced(boolean advanced);

	void setAlpha(int alpha);

	void setAntialias(Antialias antialias);

	void setBackground(Color color);

	void setBackground(int r, int g, int b, int a);

	void setClipping(int x, int y, int width, int height);

	void setFillRule(FillRule rule);

	void setFont(EditorFont font);

	void setForeground(Color color);

	void setForeground(int r, int g, int b, int a);

	void setInterpolation(Interpolation interpolation);

	void setLineCap(LineCap cap);

	void setLineDash(int[] dashes);

	void setLineJoin(LineJoin join);

	void setLineStyle(LineStyle lineStyle);

	void setLineWidth(int lineWidth);

	void setTextAntialias(Antialias antialias);

	void setTransform(Affine2 transform);

	GridPoint2 stringExtent(String string);

	GridPoint2 textExtent(String string);

	EditorFont getFont();

	int getFontAscent();

	int getFontDescent();

	int getFontHeight();

	int getFontLeading();

	int getFontAverageCharWidth();

	public enum LineStyle {
		CUSTOM, DASH, DASHDOT, DASHDOTDOT, DOT, SOLID
	}

	public enum LineCap {
		FLAT, ROUND, SQUARE
	}

	public enum LineJoin {
		BEVEL, MITER, ROUND
	}

	public enum Antialias {
		DEFAULT, OFF, ON
	}

	public enum FillRule {
		EVEN_ODD, WINDING
	}

	public enum Interpolation {
		DEFAULT, NONE, LOW, HIGH
	}
}
