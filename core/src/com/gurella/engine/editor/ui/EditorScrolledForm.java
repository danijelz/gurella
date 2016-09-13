package com.gurella.engine.editor.ui;

import java.io.InputStream;

public interface EditorScrolledForm extends EditorScrolledComposite {
	boolean isDelayedReflow();

	void setDelayedReflow(boolean delayedReflow);

	void reflow();

	EditorImage getBackgroundImage();

	EditorComposite getBody();

	EditorForm getForm();

	EditorImage getImage();

	// String getMessage();
	// IMessageManager getMessageManager();
	// int getMessageType();

	String getText();

	// IToolBarManager getToolBarManager();

	void setBackgroundImage(EditorImage backgroundImage);

	void setBackgroundImage(InputStream imageStream);

	void setBusy(boolean busy);

	void setHeadClient(EditorControl headClient);

	void setImage(EditorImage image);

	void setImage(InputStream imageStream);

	// void setMessage(String newMessage, int newType);
	// void setMessage(String newMessage, int newType, IMessage[] messages);

	void setText(String text);

	// void updateToolBar();

	public static class ScrolledFormStyle extends ScrollableStyle<ScrolledFormStyle> {
	}
}
