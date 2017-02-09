package com.gurella.engine.editor.ui;

import java.io.InputStream;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.math.GridRectangle;

public interface EditorTableItem extends EditorItem {
	Color getBackground();

	Color getBackground(int index);

	GridRectangle getBounds();

	GridRectangle getBounds(int index);

	boolean getChecked();

	EditorFont getFont();

	EditorFont getFont(int index);

	void setFont(EditorFont font);

	void setFont(String name, int height, boolean bold, boolean italic);

	void setFont(int height, boolean bold, boolean italic);

	void setFont(int index, EditorFont font);

	void setFont(int index, String name, int height, boolean bold, boolean italic);

	void setFont(int index, int height, boolean bold, boolean italic);

	Color getForeground();

	Color getForeground(int index);

	boolean getGrayed();

	EditorImage getImage(int index);

	GridRectangle getImageBounds(int index);

	EditorTable<?> getParent();

	String getText(int index);

	GridRectangle getTextBounds(int index);

	void setBackground(Color color);

	void setBackground(int r, int g, int b, int a);

	void setBackground(int index, Color color);

	void setBackground(int index, int r, int g, int b, int a);

	void setChecked(boolean checked);

	void setForeground(Color color);

	void setForeground(int r, int g, int b, int a);

	void setForeground(int index, Color color);

	void setForeground(int index, int r, int g, int b, int a);

	void setGrayed(boolean grayed);

	void setImage(EditorImage[] images);

	void setImage(int index, EditorImage image);

	void setImage(int index, InputStream imageStream);

	void setText(int index, String string);

	void setText(String[] strings);
}
