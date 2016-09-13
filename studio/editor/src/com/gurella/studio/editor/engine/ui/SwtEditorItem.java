package com.gurella.studio.editor.engine.ui;

import java.io.InputStream;

import org.eclipse.swt.widgets.Item;

import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorItem;

public abstract class SwtEditorItem<T extends Item> extends SwtEditorWidget<T> implements EditorItem {
	SwtEditorItem(T item) {
		super(item);
	}

	@Override
	public String getText() {
		return widget.getText();
	}

	@Override
	public void setText(String string) {
		widget.setText(string);
	}

	@Override
	public SwtEditorImage getImage() {
		return toEditorImage(widget.getImage());
	}

	@Override
	public void setImage(InputStream imageStream) {
		widget.setImage(toSwtImage(imageStream));
	}

	@Override
	public void setImage(EditorImage image) {
		widget.setImage(toSwtImage(image));
	}
}
