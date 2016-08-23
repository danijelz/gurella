package com.gurella.engine.editor.ui;

import java.io.InputStream;

public interface EditorItem extends EditorWidget {
	String getText();

	void setText(String string);
	
	EditorImage getImage();

	void setImage(InputStream imageStream);
}
