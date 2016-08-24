package com.gurella.engine.editor.ui;

import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.utils.GridRectangle;

public interface EditorCoolItem extends EditorItem {
	GridRectangle getBounds();

	EditorCoolBar getParent();

	EditorControl getControl();

	void setControl(EditorControl control);

	GridPoint2 getMinimumSize();

	void setMinimumSize(int width, int height);

	GridPoint2 getPreferredSize();

	void setPreferredSize(int width, int height);

	GridPoint2 getSize();

	void setSize(int width, int height);
}
