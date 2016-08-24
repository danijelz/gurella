package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.editor.ui.EditorDateTime;

public class SwtEditorDateTime extends SwtEditorBaseComposite<DateTime> implements EditorDateTime {
	public SwtEditorDateTime(SwtEditorBaseComposite<?> parent, FormToolkit toolkit) {
		super(parent);
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

	@Override
	DateTime createWidget(Composite parent) {
		return new DateTime(parent, 0);
	}
}
