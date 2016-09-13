package com.gurella.engine.editor.ui;

import java.io.InputStream;

import com.badlogic.gdx.graphics.Color;

public interface EditorForm extends EditorComposite {
	EditorImage getBackgroundImage();

	EditorComposite getBody();

	// IMessage[] getChildrenMessages();
	EditorComposite getHead();

	EditorControl getHeadClient();

	Color getHeadColor(HeadColorKey key);

	EditorImage getImage();

	// String getMessage();
	// IMessageManager getMessageManager();
	// int getMessageType();
	String getText();

	// IToolBarManager getToolBarManager();
	// int getToolBarVerticalAlignment();
	boolean isBackgroundImageTiled();

	boolean isBusy();

	boolean isSeparatorVisible();

	void setBackgroundImage(EditorImage backgroundImage);

	void setBackgroundImage(InputStream imageStream);

	void setBackgroundImageTiled(boolean backgroundImageTiled);

	void setBusy(boolean busy);

	void setHeadClient(EditorControl headClient);

	void setHeadColor(HeadColorKey key, Color color);

	void setHeadColor(HeadColorKey key, int r, int g, int b, int a);

	void setImage(EditorImage image);

	void setImage(InputStream imageStream);

	// void setMessage(String message);
	// /void setMessage(String newMessage, int newType);
	// TODO void setMessage(String newMessage, int newType, IMessage[] children);
	void setSeparatorVisible(boolean addSeparator);

	void setText(String text);

	void setTextBackground(Color[] gradientColors, int[] percents, boolean vertical);
	// void setToolBarVerticalAlignment(int alignment);
	// void updateToolBar();

	public enum HeadColorKey {
		BORDER,
		H_BOTTOM_KEYLINE1,
		H_BOTTOM_KEYLINE2,
		H_GRADIENT_END,
		H_GRADIENT_START,
		H_HOVER_FULL,
		H_HOVER_LIGHT,
		H_PREFIX,
		PREFIX,
		SEPARATOR,
		TB_BG,
		TB_BORDER,
		TB_FG,
		TB_PREFIX,
		TB_TOGGLE,
		TB_TOGGLE_HOVER,
		TITLE;
	}
}
