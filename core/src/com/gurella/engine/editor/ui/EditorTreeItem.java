package com.gurella.engine.editor.ui;

import java.io.InputStream;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.utils.GridRectangle;

public interface EditorTreeItem extends EditorItem {
	void clear(int index, boolean all);

	void clearAll(boolean all);

	Color getBackground();

	Color getBackground(int index);

	GridRectangle getBounds();

	GridRectangle getBounds(int index);

	boolean getChecked();

	boolean getExpanded();

	FontData getFont();

	FontData getFont(int index);

	Color getForeground();

	Color getForeground(int index);

	boolean getGrayed();

	EditorImage getImage(int index);

	GridRectangle getImageBounds(int index);

	EditorTreeItem getItem(int index);

	int getItemCount();

	EditorTreeItem[] getItems();

	EditorTree getParent();

	EditorTreeItem getParentItem();

	String getText(int index);

	GridRectangle getTextBounds(int index);

	int indexOf(EditorTreeItem item);

	void removeAll();

	void setBackground(Color color);

	void setBackground(int index, Color color);

	void setChecked(boolean checked);

	void setExpanded(boolean expanded);

	void setFont(FontData font);

	void setFont(int index, FontData font);

	void setForeground(Color color);

	void setForeground(int index, Color color);

	void setGrayed(boolean grayed);

	void setImage(EditorImage[] images);

	void setImage(int index, EditorImage image);

	void setImage(int index, InputStream imageStream);

	void setItemCount(int count);

	void setText(int index, String string);

	void setText(String[] strings);
}
