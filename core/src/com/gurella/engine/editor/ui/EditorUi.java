package com.gurella.engine.editor.ui;

import java.io.InputStream;

import com.gurella.engine.editor.ui.EditorButton.ArrowDirection;
import com.gurella.engine.editor.ui.EditorCombo.ComboStyle;
import com.gurella.engine.editor.ui.EditorComposite.CompositeStyle;
import com.gurella.engine.editor.ui.EditorDateTime.CalendarStyle;
import com.gurella.engine.editor.ui.EditorDateTime.DateStyle;
import com.gurella.engine.editor.ui.EditorDateTime.DateTimeLength;
import com.gurella.engine.editor.ui.EditorDateTime.DropDownDateStyle;
import com.gurella.engine.editor.ui.EditorDateTime.TimeStyle;
import com.gurella.engine.editor.ui.EditorGroup.GroupStyle;
import com.gurella.engine.editor.ui.EditorLabel.LabelStyle;
import com.gurella.engine.editor.ui.EditorLabel.SeparatorStyle;
import com.gurella.engine.editor.ui.EditorLink.LinkStyle;
import com.gurella.engine.editor.ui.EditorProgressBar.ProgressBarStyle;
import com.gurella.engine.editor.ui.EditorSash.SashStyle;
import com.gurella.engine.editor.ui.EditorScale.ScaleStyle;
import com.gurella.engine.editor.ui.EditorSlider.SliderStyle;
import com.gurella.engine.editor.ui.EditorSpinner.SpinnerStyle;
import com.gurella.engine.editor.ui.EditorTable.TableStyle;
import com.gurella.engine.editor.ui.EditorToolBar.ToolBarStyle;
import com.gurella.engine.editor.ui.EditorTree.TreeStyle;
import com.gurella.engine.editor.ui.style.WidgetStyle;

public interface EditorUi {
	void log(EditorLogLevel level, String message);

	void logError(Throwable t, String message);

	EditorImage createImage(InputStream imageStream);

	EditorFont createFont(String name, int height, boolean bold, boolean italic);

	EditorFont createFont(EditorFont initial, int height, boolean bold, boolean italic);

	EditorFont createFont(EditorControl control, int height, boolean bold, boolean italic);

	EditorComposite createComposite(EditorComposite parent);

	EditorComposite createComposite(EditorComposite parent, CompositeStyle style);

	EditorGroup createGroup(EditorComposite parent);

	EditorGroup createGroup(EditorComposite parent, GroupStyle style);

	EditorLabel createLabel(EditorComposite parent);

	EditorLabel createLabel(EditorComposite parent, LabelStyle style);

	EditorLabel createLabel(EditorComposite parent, String text);

	EditorLabel createLabel(EditorComposite parent, String text, LabelStyle style);

	EditorLabel createSeparator(EditorComposite parent, boolean vertical);

	EditorLabel createSeparator(EditorComposite parent, SeparatorStyle style);

	EditorLink createLink(EditorComposite parent);

	EditorLink createLink(EditorComposite parent, LinkStyle style);

	EditorLink createLink(EditorComposite parent, String text);

	EditorLink createLink(EditorComposite parent, String text, LinkStyle style);

	EditorProgressBar createProgressBar(EditorComposite parent, boolean vertical, boolean smooth,
			boolean indeterminate);

	EditorProgressBar createProgressBar(EditorComposite parent, ProgressBarStyle style);

	EditorSash createSash(EditorComposite parent, boolean vertical, boolean smooth);

	EditorSash createSash(EditorComposite parent, SashStyle style);

	EditorScale createScale(EditorComposite parent, boolean vertical);

	EditorScale createScale(EditorComposite parent, ScaleStyle style);

	EditorSlider createSlider(EditorComposite parent, boolean vertical);

	EditorSlider createSlider(EditorComposite parent, SliderStyle style);

	EditorCombo createCombo(EditorComposite parent);

	EditorCombo createCombo(EditorComposite parent, ComboStyle style);

	EditorDateTime createDate(EditorComposite parent, DateTimeLength length);

	EditorDateTime createDate(EditorComposite parent, DateStyle styles);

	EditorDateTime createDropDownDate(EditorComposite parent);

	EditorDateTime createDropDownDate(EditorComposite parent, DropDownDateStyle style);

	EditorDateTime createTime(EditorComposite parent, DateTimeLength length);

	EditorDateTime createTime(EditorComposite parent, TimeStyle style);

	EditorDateTime createCalendar(EditorComposite parent);

	EditorDateTime createCalendar(EditorComposite parent, CalendarStyle style);

	EditorSpinner createSpinner(EditorComposite parent);

	EditorSpinner createSpinner(EditorComposite parent, SpinnerStyle style);

	EditorTabFolder createTabFolder(EditorComposite parent, boolean top,
			WidgetStyle<? super EditorTabFolder>... styles);

	EditorExpandBar createExpandBar(EditorComposite parent, boolean verticalScroll,
			WidgetStyle<? super EditorExpandBar>... styles);

	EditorList createList(EditorComposite parent, boolean multi, WidgetStyle<? super EditorList>... styles);

	EditorText createText(EditorComposite parent, WidgetStyle<? super EditorText>... styles);

	EditorText createTextArea(EditorComposite parent, WidgetStyle<? super EditorText>... styles);

	EditorButton createCheckBox(EditorComposite parent, WidgetStyle<? super EditorButton>... styles);

	EditorButton createCheckBox(EditorComposite parent, String text, WidgetStyle<? super EditorButton>... styles);

	EditorButton createButton(EditorComposite parent, WidgetStyle<? super EditorButton>... styles);

	EditorButton createButton(EditorComposite parent, String text, WidgetStyle<? super EditorButton>... styles);

	EditorButton createToggleButton(EditorComposite parent, WidgetStyle<? super EditorButton>... styles);

	EditorButton createToggleButton(EditorComposite parent, String text, WidgetStyle<? super EditorButton>... styles);

	EditorButton createRadioButton(EditorComposite parent, WidgetStyle<? super EditorButton>... styles);

	EditorButton createRadioButton(EditorComposite parent, String text, WidgetStyle<? super EditorButton>... styles);

	EditorButton createArrowButton(EditorComposite parent, ArrowDirection arrowDirection,
			WidgetStyle<? super EditorButton>... styles);

	EditorMenu createMenu(EditorControl parent);

	EditorToolBar createToolBar(EditorComposite parent, boolean vertical);

	EditorToolBar createToolBar(EditorComposite parent, boolean vertical, ToolBarStyle style);

	EditorTable createTable(EditorComposite parent);

	EditorTable createTable(EditorComposite parent, TableStyle style);

	EditorTree createTree(EditorComposite parent);

	EditorTree createTree(EditorComposite parent, TreeStyle style);
}
