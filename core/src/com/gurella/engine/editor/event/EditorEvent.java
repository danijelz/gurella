package com.gurella.engine.editor.event;

import com.gurella.engine.editor.EditorControl;

public interface EditorEvent {
	int getButton();

	void setButton(int button);

	char getCharacter();

	void setCharacter(char character);

	int getCount();

	void setCount(int count);

	Object getData();

	void setData(Object data);

	int getDetail();

	void setDetail(int detail);

	boolean isDoit();

	void setDoit(boolean doit);

	int getEnd();

	void setEnd(int end);

	int getHeight();

	void setHeight(int height);

	int getIndex();

	void setIndex(int index);

	EditorControl getItem();

	void setItem(EditorControl item);

	int getKeyCode();

	void setKeyCode(int keyCode);

	int getKeyLocation();

	void setKeyLocation(int keyLocation);

	double getMagnification();

	void setMagnification(double magnification);

	double getRotation();

	void setRotation(double rotation);

	int[] getSegments();

	void setSegments(int[] segments);

	char[] getSegmentsChars();

	void setSegmentsChars(char[] segmentsChars);

	int getStart();

	void setStart(int start);

	int getStateMask();

	void setStateMask(int stateMask);

	String getText();

	void setText(String text);

	int getTime();

	void setTime(int time);

	EditorEventType getType();

	void setType(EditorEventType type);

	EditorControl getWidget();

	void setWidget(EditorControl widget);

	int getWidth();

	void setWidth(int width);

	int getX();

	void setX(int x);

	int getxDirection();

	void setxDirection(int xDirection);

	int getY();

	void setY(int y);

	int getyDirection();

	void setyDirection(int yDirection);

}