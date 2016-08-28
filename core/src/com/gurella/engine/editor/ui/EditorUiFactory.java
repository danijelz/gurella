package com.gurella.engine.editor.ui;

import java.io.InputStream;

import com.gurella.engine.editor.ui.style.WidgetStyle;

public interface EditorUiFactory {
	EditorImage createImage(InputStream imageStream);

	EditorComposite createComposite(EditorComposite parent, WidgetStyle<? super EditorComposite>... styles);

	EditorLabel createLabel(EditorComposite parent, WidgetStyle<? super EditorLabel>... styles);

	EditorLabel createLabel(EditorComposite parent, String text, WidgetStyle<? super EditorLabel>... styles);

	EditorLabel createSeparatorLabel(EditorComposite parent, WidgetStyle<? super EditorLabel>... styles);

	EditorButton createCheckBox(EditorComposite parent, WidgetStyle<? super EditorButton>... styles);
}
