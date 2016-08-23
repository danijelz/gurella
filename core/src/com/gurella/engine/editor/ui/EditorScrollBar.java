package com.gurella.engine.editor.ui;

import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.utils.GridRectangle;

public interface EditorScrollBar extends EditorWidget {
	boolean getEnabled();

	int getIncrement();

	int getMaximum();

	int getMinimum();

	int getPageIncrement();

	EditorScrollable getParent();

	int getSelection();

	GridPoint2 getSize();

	int getThumb();

	GridRectangle getThumbBounds();

	GridRectangle getThumbTrackBounds();

	boolean getVisible();

	boolean isEnabled();

	boolean isVisible();

	void setEnabled(boolean enabled);

	void setIncrement(int value);

	void setMaximum(int value);

	void setMinimum(int value);

	void setPageIncrement(int value);

	void setSelection(int selection);

	void setThumb(int value);

	void setValues(int selection, int minimum, int maximum, int thumb, int increment, int pageIncrement);

	void setVisible(boolean visible);
}
