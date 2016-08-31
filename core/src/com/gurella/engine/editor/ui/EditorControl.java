package com.gurella.engine.editor.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.utils.GridRectangle;

public interface EditorControl extends EditorWidget {
	EditorBaseComposite getParent();

	int getBorderWidth();

	boolean setFocus();

	boolean forceFocus();

	void redraw();

	void pack();

	void moveAbove(EditorControl control);

	void moveBelow(EditorControl control);

	Color getBackground();

	void setBackground(Color color);

	GridRectangle getBounds();

	void setBounds(int x, int y, int width, int height);

	boolean getDragDetect();

	void setDragDetect(boolean dragDetect);

	boolean getEnabled();

	boolean isEnabled();

	EditorFont getFont();

	void setFont(EditorFont font);

	void setFont(String name, int height, boolean bold, boolean italic);

	void setFont(int height, boolean bold, boolean italic);

	Color getForeground();

	void setForeground(Color color);

	Object getLayoutData();

	void setLayoutData(Object layoutData);

	GridPoint2 getLocation();

	void setLocation(int x, int y);

	EditorMenu getMenu();

	void setMenu(EditorMenu menu);

	Direction getOrientation();

	void setOrientation(Direction direction);

	GridPoint2 getSize();

	void setSize(int width, int height);

	Direction getTextDirection();

	void setTextDirection(Direction textDirection);

	String getToolTipText();

	void setToolTipText(String string);

	boolean getTouchEnabled();

	void setTouchEnabled(boolean enabled);

	boolean getVisible();

	void setVisible(boolean visible);

	boolean isFocusControl();

	boolean isReparentable();

	boolean isVisible();

	void setCapture(boolean capture);

	void setEnabled(boolean enabled);

	boolean setParent(EditorComposite parent);

	GridPoint2 toControl(int x, int y);

	GridPoint2 toDisplay(int x, int y);

	public static class ControlStyle {
		public Direction textDirection;
		public boolean border;
		public boolean flipTextDirection;
	}
}
