package com.gurella.engine.editor.ui;

public interface EditorDateTime extends EditorBaseComposite {
	int getDay();

	void setDay(int day);

	int getHours();

	void setHours(int hours);

	int getMinutes();

	void setMinutes(int minutes);

	int getMonth();

	void setMonth(int month);

	int getSeconds();

	void setSeconds(int seconds);

	int getYear();

	void setYear(int year);

	void setDate(int year, int month, int day);

	void setTime(int hours, int minutes, int seconds);

	public enum DateTimeLength {
		SHORT, MEDIUM, LONG;
	}
}
