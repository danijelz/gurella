package com.gurella.engine.editor.ui;

import java.io.InputStream;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.utils.GridRectangle;

public interface EditorTableItem extends EditorItem {
	Color getBackground();

	Color getBackground(int index);

	GridRectangle getBounds();

	GridRectangle getBounds(int index);

	boolean getChecked();

	FontData getFont();

	FontData getFont(int index);

	Color getForeground();

	Color getForeground(int index);

	boolean getGrayed();

	EditorImage getImage(int index);

	GridRectangle getImageBounds(int index);

	EditorTable getParent();

	String getText(int index);

	GridRectangle getTextBounds(int index);

	void setBackground(Color color);

	void setBackground(int index, Color color);

	void setChecked(boolean checked);

	void setFont(FontData font);

	void setFont(int index, FontData font);

	void setForeground(Color color);

	void setForeground(int index, Color color);

	void setGrayed(boolean grayed);

	void setImage(EditorImage[] images);

	void setImage(int index, EditorImage image);
	
	void setImage(int index, InputStream imageStream);

	void setText(int index, String string);

	void setText(String[] strings);
}
