package com.gurella.engine.editor.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.editor.ui.EditorControlDecoration.HorizontalAlignment;
import com.gurella.engine.editor.ui.EditorControlDecoration.VerticalAlignment;
import com.gurella.engine.editor.ui.layout.EditorLayoutData;
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

	void setBackground(int r, int g, int b, int a);

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

	void setForeground(int r, int g, int b, int a);

	EditorLayoutData getLayoutData();

	EditorLayoutData getOrCreateLayoutData();

	EditorLayoutData getOrCreateDefaultLayoutData();

	void setLayoutData(EditorLayoutData layoutData);

	void setLayoutData(int horizontalSpan, int verticalSpan);

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

	EditorControlDecoration getDecoration();

	EditorControlDecoration createDecoration(HorizontalAlignment horizontalAlignment,
			VerticalAlignment verticalAlignment);

	EditorControlDecoration getOrCreateDecoration(HorizontalAlignment horizontalAlignment,
			VerticalAlignment verticalAlignment);

	void clearDecoration();

	public static class ControlStyle<T extends ControlStyle<T>> {
		public Direction textDirection;
		public boolean border;
		public boolean flipTextDirection;

		public T textDirection(Direction textDirection) {
			this.textDirection = textDirection;
			return cast();
		}

		public T border(boolean border) {
			this.border = border;
			return cast();
		}

		public T flipTextDirection(boolean flipTextDirection) {
			this.flipTextDirection = flipTextDirection;
			return cast();
		}

		@SuppressWarnings("unchecked")
		protected T cast() {
			return (T) this;
		}
	}
}
