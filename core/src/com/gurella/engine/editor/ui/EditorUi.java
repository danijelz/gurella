package com.gurella.engine.editor.ui;

import java.io.InputStream;

import com.gurella.engine.editor.ui.style.WidgetStyle;

public interface EditorUi {
	void log(EditorLogLevel level, String message);

	void logError(Throwable t, String message);

	EditorImage createImage(InputStream imageStream);

	EditorComposite createComposite(EditorComposite parent, WidgetStyle<? super EditorComposite>... styles);

	EditorLabel createLabel(EditorComposite parent, WidgetStyle<? super EditorLabel>... styles);

	EditorLabel createLabel(EditorComposite parent, String text, WidgetStyle<? super EditorLabel>... styles);

	EditorLabel createSeparatorLabel(EditorComposite parent, WidgetStyle<? super EditorLabel>... styles);

	EditorButton createCheckBox(EditorComposite parent, WidgetStyle<? super EditorButton>... styles);

	EditorButton createCheckBox(EditorComposite parent, String text, WidgetStyle<? super EditorButton>... styles);

	EditorMenu createMenu(EditorControl parent);

	EditorMenu createMenu(EditorMenu parentMenu);

	EditorMenu createMenu(EditorMenuItem parentItem);
}
