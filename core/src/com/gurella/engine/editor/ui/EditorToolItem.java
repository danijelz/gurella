package com.gurella.engine.editor.ui;

import java.io.InputStream;

import com.gurella.engine.utils.GridRectangle;

public interface EditorToolItem extends EditorItem {
	GridRectangle getBounds();

	EditorControl getControl();

	EditorImage getDisabledImage();

	boolean getEnabled();

	EditorImage getHotImage();

	EditorToolBar getParent();

	boolean getSelection();

	String getToolTipText();

	int getWidth();

	boolean isEnabled();

	void setControl(EditorControl control);

	void setDisabledImage(InputStream imageStream);

	void setDisabledImage(EditorImage image);

	void setEnabled(boolean enabled);

	void setHotImage(EditorImage image);

	void setHotImage(InputStream imageStream);

	void setSelection(boolean selected);

	void setToolTipText(String string);

	void setWidth(int width);

	public enum ToolItemType {
		PUSH, CHECK, RADIO, SEPARATOR, DROP_DOWN
	}
}
