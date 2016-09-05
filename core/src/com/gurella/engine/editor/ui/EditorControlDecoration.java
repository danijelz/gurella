package com.gurella.engine.editor.ui;

import java.io.InputStream;

import com.badlogic.gdx.utils.Disposable;

public interface EditorControlDecoration extends Disposable {
	EditorControl getControl();

	String getDescriptionText();

	EditorImage getImage();

	int getMarginWidth();

	boolean getShowHover();

	boolean getShowOnlyOnFocus();

	void hide();

	void hideHover();

	boolean isVisible();

	void setDescriptionText(String text);

	void setImage(EditorImage image);

	void setImage(InputStream imageStream);

	void setMarginWidth(int marginWidth);

	void setShowHover(boolean showHover);

	void setShowOnlyOnFocus(boolean showOnlyOnFocus);

	void show();

	void showHoverText(String text);

	void setInfoImage();

	void setErrorImage();

	void setRequiredImage();

	void setWarningImage();

	public enum HorizontalAlignment {
		LEFT, CENTER, RIGHT;
	}

	public enum VerticalAlignment {
		TOP, CENTER, BOTTOM;
	}
}
