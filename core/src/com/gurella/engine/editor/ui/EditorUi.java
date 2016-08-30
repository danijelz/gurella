package com.gurella.engine.editor.ui;

import java.io.InputStream;

import com.gurella.engine.editor.ui.EditorButton.ArrowDirection;
import com.gurella.engine.editor.ui.EditorDateTime.DateTimeLength;
import com.gurella.engine.editor.ui.style.WidgetStyle;

public interface EditorUi {
	void log(EditorLogLevel level, String message);

	void logError(Throwable t, String message);

	EditorImage createImage(InputStream imageStream);

	EditorFont createFont(String name, int height, boolean bold, boolean italic);

	EditorFont createFont(EditorFont initial, int height, boolean bold, boolean italic);

	EditorFont createFont(EditorControl control, int height, boolean bold, boolean italic);

	EditorComposite createComposite(EditorComposite parent, WidgetStyle<? super EditorComposite>... styles);

	EditorGroup createGroup(EditorComposite parent, WidgetStyle<? super EditorGroup>... styles);

	EditorLabel createLabel(EditorComposite parent, WidgetStyle<? super EditorLabel>... styles);

	EditorLabel createLabel(EditorComposite parent, String text, WidgetStyle<? super EditorLabel>... styles);

	EditorLabel createSeparator(EditorComposite parent, Orientation orientation,
			WidgetStyle<? super EditorLabel>... styles);

	EditorLink createLink(EditorComposite parent, WidgetStyle<? super EditorLink>... styles);

	EditorLink createLink(EditorComposite parent, String text, WidgetStyle<? super EditorLink>... styles);

	EditorProgressBar createProgressBar(EditorComposite parent, Orientation orientation, boolean smooth,
			boolean indeterminate, WidgetStyle<? super EditorProgressBar>... styles);

	EditorSash createSash(EditorComposite parent, Orientation orientation, boolean smooth,
			WidgetStyle<? super EditorSash>... styles);

	EditorScale createScale(EditorComposite parent, Orientation orientation,
			WidgetStyle<? super EditorScale>... styles);

	EditorSlider createSlider(EditorComposite parent, Orientation orientation,
			WidgetStyle<? super EditorSlider>... styles);

	EditorCombo createCombo(EditorComposite parent, WidgetStyle<? super EditorCombo>... styles);

	EditorDateTime createDate(EditorComposite parent, DateTimeLength length,
			WidgetStyle<? super EditorDateTime>... styles);

	EditorDateTime createDropDownDate(EditorComposite parent, WidgetStyle<? super EditorDateTime>... styles);

	EditorDateTime createTime(EditorComposite parent, DateTimeLength length,
			WidgetStyle<? super EditorDateTime>... styles);

	EditorDateTime createCalendar(EditorComposite parent, WidgetStyle<? super EditorDateTime>... styles);

	EditorSpinner createSpinner(EditorComposite parent, WidgetStyle<? super EditorSpinner>... styles);

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
	
	EditorButton createArrowButton(EditorComposite parent, ArrowDirection arrowDirection, WidgetStyle<? super EditorButton>... styles);

	EditorMenu createMenu(EditorControl parent);
}
