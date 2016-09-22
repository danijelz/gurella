package com.gurella.studio.editor.engine.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.editor.ui.EditorFont;
import com.gurella.engine.editor.ui.EditorGraphicContex;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.utils.GridRectangle;

public class SwtEditorGraphicContex implements EditorGraphicContex {
	GC gc;

	public SwtEditorGraphicContex(GC gc) {
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
	public int getFillRule() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Color getForeground() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getInterpolation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLineCap() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[] getLineDash() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getDashOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDashOffset(float dashOffset) {
		// TODO Auto-generated method stub

	}

	@Override
	public float getMiterLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMiterLimit(float miterLimit) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLineJoin() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLineStyle() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLineWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getStyle() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTextAntialias() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void getTransform(Affine2 transform) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isClipped() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setAdvanced(boolean advanced) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAntialias(Antialias antialias) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBackground(Color color) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setClipping(int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFillRule(int rule) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFont(EditorFont font) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setForeground(Color color) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setInterpolation(int interpolation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLineCap(int cap) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLineDash(int[] dashes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLineJoin(int join) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLineStyle(int lineStyle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLineWidth(int lineWidth) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTextAntialias(int antialias) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTransform(Affine2 transform) {
		// TODO Auto-generated method stub

	}

	@Override
	public GridPoint2 stringExtent(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GridPoint2 textExtent(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GridPoint2 textExtent(String string, int flags) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EditorFont getFont() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFontAscent() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFontDescent() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFontHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFontLeading() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFontAverageCharWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

}
