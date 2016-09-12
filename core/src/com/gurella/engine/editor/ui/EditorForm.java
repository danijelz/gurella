package com.gurella.engine.editor.ui;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.editor.ui.layout.EditorLayout;

public interface EditorForm extends EditorComposite {
	EditorImage	getBackgroundImage();
	EditorComposite	getBody();
	//IMessage[]	getChildrenMessages();
	EditorComposite	getHead();
	EditorControl	getHeadClient();
	Color	getHeadColor(String key);
	EditorImage	getImage();
	//String	getMessage(); 
	//IMessageManager	getMessageManager();
	//int	getMessageType(); 
	String	getText();
	//IToolBarManager	getToolBarManager();
	//int	getToolBarVerticalAlignment();
	boolean	isBackgroundImageTiled();
	boolean	isBusy();
	boolean	isSeparatorVisible();
	void	setBackgroundImage(EditorImage backgroundImage);
	void	setBackgroundImageTiled(boolean backgroundImageTiled);
	void	setBusy(boolean busy);
	void	setHeadClient(EditorControl headClient);
	void	setHeadColor(String key, Color color);
	void	setImage(EditorImage image);
	//void	setMessage(String message);
//	/void	setMessage(String newMessage, int newType);
	//TODO void	setMessage(String newMessage, int newType, IMessage[] children);
	void	setSeparatorVisible(boolean addSeparator);
	void	setText(String text);
	void	setTextBackground(Color[] gradientColors, int[] percents, boolean vertical);
	//void	setToolBarVerticalAlignment(int alignment);
	//void	updateToolBar();
}
