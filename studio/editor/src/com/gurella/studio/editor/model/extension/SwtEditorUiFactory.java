package com.gurella.studio.editor.model.extension;

import static com.gurella.engine.utils.Values.cast;
import static com.gurella.studio.editor.model.extension.style.SwtWidgetStyle.getSwtStyle;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.gurella.engine.editor.ui.EditorButton;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorLabel;
import com.gurella.engine.editor.ui.EditorUiFactory;
import com.gurella.engine.editor.ui.style.WidgetStyle;
import com.gurella.engine.utils.Values;

public class SwtEditorUiFactory implements EditorUiFactory {
	public static final SwtEditorUiFactory instance = new SwtEditorUiFactory();

	private SwtEditorUiFactory() {
	}

	@Override
	public EditorImage createImage(InputStream imageStream) {
		return new SwtEditorImage(new Image(getDisplay(), imageStream));
	}

	public static Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

	public EditorComposite createComposite(Composite parent) {
		return new SwtEditorComposite(parent);
	}

	@Override
	public EditorComposite createComposite(EditorComposite parent, WidgetStyle<? super EditorComposite>... styles) {
		return new SwtEditorComposite(Values.<SwtEditorComposite> cast(parent), getSwtStyle(styles));
	}

	@Override
	public EditorLabel createLabel(EditorComposite parent, WidgetStyle<? super EditorLabel>... styles) {
		return new SwtEditorLabel(cast(parent), getSwtStyle(styles));
	}

	@Override
	public EditorLabel createLabel(EditorComposite parent, String text, WidgetStyle<? super EditorLabel>... styles) {
		return new SwtEditorLabel(cast(parent), text, getSwtStyle(styles));
	}

	@Override
	public EditorLabel createSeparatorLabel(EditorComposite parent, WidgetStyle<? super EditorLabel>... styles) {
		return new SwtEditorLabel(cast(parent), getSwtStyle(SWT.SEPARATOR, styles));
	}

	@Override
	public EditorButton createCheckBox(EditorComposite parent, WidgetStyle<? super EditorButton>... styles) {
		return new SwtEditorButton(cast(parent), getSwtStyle(SWT.CHECK, styles));
	}
}
