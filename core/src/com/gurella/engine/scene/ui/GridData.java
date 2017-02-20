package com.gurella.engine.scene.ui;

import com.badlogic.gdx.math.GridPoint2;

public class GridData {
	public int column = 0;
	public int row = 0;

	public int widthHint = -1;
	public int heightHint = -1;
	public int horizontalIndent = 0;
	public int verticalIndent = 0;
	public int horizontalSpan = 1;
	public int verticalSpan = 1;
	public int minimumWidth = 0;
	public int minimumHeight = 0;
	public boolean grabExcessHorizontalSpace = false;
	public boolean grabExcessVerticalSpace = false;
	public VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;
	public HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
	public boolean exclude = false;

	int cacheWidth = -1, cacheHeight = -1;
	int defaultWhint, defaultHhint, defaultWidth = -1, defaultHeight = -1;
	int currentWhint, currentHhint, currentWidth = -1, currentHeight = -1;

	void computeSize(UiComponent control, int wHint, int hHint, boolean flushCache) {
		if (cacheWidth != -1 && cacheHeight != -1) {
			return;
		}

		if (wHint == this.widthHint && hHint == this.heightHint) {
			if (defaultWidth == -1 || defaultHeight == -1 || wHint != defaultWhint || hHint != defaultHhint) {
				GridPoint2 size = null; //TODO control.computeSize(wHint, hHint, flushCache);
				defaultWhint = wHint;
				defaultHhint = hHint;
				defaultWidth = size.x;
				defaultHeight = size.y;
			}
			cacheWidth = defaultWidth;
			cacheHeight = defaultHeight;
			return;
		}

		if (currentWidth == -1 || currentHeight == -1 || wHint != currentWhint || hHint != currentHhint) {
			GridPoint2 size = null;//TODO control.computeSize(wHint, hHint, flushCache);
			currentWhint = wHint;
			currentHhint = hHint;
			currentWidth = size.x;
			currentHeight = size.y;
		}

		cacheWidth = currentWidth;
		cacheHeight = currentHeight;
	}

	void flushCache() {
		cacheWidth = cacheHeight = -1;
		defaultWidth = defaultHeight = -1;
		currentWidth = currentHeight = -1;
	}

	public enum VerticalAlignment {
		TOP, BOTTOM, CENTER, FILL;
	}

	public enum HorizontalAlignment {
		LEFT, RIGHT, CENTER, FILL;
	}
}
