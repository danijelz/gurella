package com.gurella.engine.editor.ui;

import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.math.GridRectangle;

public interface EditorScrollBar extends EditorWidget {
	boolean getEnabled();

	boolean isEnabled();

	void setEnabled(boolean enabled);

	int getIncrement();

	void setIncrement(int value);

	int getMaximum();

	void setMaximum(int value);

	int getMinimum();

	void setMinimum(int value);

	int getPageIncrement();

	void setPageIncrement(int value);

	EditorScrollable getParent();

	int getSelection();

	void setSelection(int selection);

	GridPoint2 getSize();

	int getThumb();

	void setThumb(int value);

	GridRectangle getThumbBounds();

	GridRectangle getThumbTrackBounds();

	boolean getVisible();

	boolean isVisible();

	void setVisible(boolean visible);

	void setValues(int selection, int minimum, int maximum, int thumb, int increment, int pageIncrement);
}
