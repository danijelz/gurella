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

	public static class DateStyle extends ScrollableStyle<DateStyle> {
		public DateTimeLength length;

		public DateStyle length(DateTimeLength length) {
			this.length = length;
			return this;
		}
	}

	public static class DropDownDateStyle extends ScrollableStyle<DropDownDateStyle> {
	}

	public static class TimeStyle extends ScrollableStyle<TimeStyle> {
		public DateTimeLength length;

		public TimeStyle length(DateTimeLength length) {
			this.length = length;
			return this;
		}
	}

	public static class CalendarStyle extends ScrollableStyle<CalendarStyle> {
	}

	public enum DateTimeLength {
		SHORT, MEDIUM, LONG;
	}
}
