package com.gurella.studio.editor.engine.ui;

import org.eclipse.swt.widgets.DateTime;

import com.gurella.engine.editor.ui.EditorDateTime;

public class SwtEditorDateTime extends SwtEditorBaseComposite<DateTime> implements EditorDateTime {
	public SwtEditorDateTime(SwtEditorLayoutComposite<?> parent, int style) {
		super(new DateTime(parent.widget, style));
	}

	@Override
	public int getDay() {
		return widget.getDay();
	}

	@Override
	public void setDay(int day) {
		widget.setDay(day);
	}

	@Override
	public int getHours() {
		return widget.getHours();
	}

	@Override
	public void setHours(int hours) {
		widget.setHours(hours);
	}

	@Override
	public int getMinutes() {
		return widget.getMinutes();
	}

	@Override
	public void setMinutes(int minutes) {
		widget.setMinutes(minutes);
	}

	@Override
	public int getMonth() {
		return widget.getMonth();
	}

	@Override
	public void setMonth(int month) {
		widget.setMonth(month);
	}

	@Override
	public int getSeconds() {
		return widget.getSeconds();
	}

	@Override
	public void setSeconds(int seconds) {
		widget.setSeconds(seconds);
	}

	@Override
	public int getYear() {
		return widget.getYear();
	}

	@Override
	public void setYear(int year) {
		widget.setYear(year);
	}

	@Override
	public void setDate(int year, int month, int day) {
		widget.setDate(year, month, day);
	}

	@Override
	public void setTime(int hours, int minutes, int seconds) {
		widget.setTime(hours, minutes, seconds);
	}
}
