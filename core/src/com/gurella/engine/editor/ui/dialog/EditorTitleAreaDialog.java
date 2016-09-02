package com.gurella.engine.editor.ui.dialog;

import java.io.InputStream;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.editor.ui.EditorImage;

public interface EditorTitleAreaDialog extends EditorDialog {
	String getErrorMessage();

	String getMessage();

	void setErrorMessage(String newErrorMessage);

	void setMessage(String newMessage);

	void setMessage(String newMessage, int newType);

	void setTitle(String newTitle);

	void setTitleAreaColor(Color color);

	void setTitleAreaColor(int r, int g, int b, int a);

	void setTitleImage(EditorImage newTitleImage);

	void setTitleImage(InputStream newTitleImageStream);
}
