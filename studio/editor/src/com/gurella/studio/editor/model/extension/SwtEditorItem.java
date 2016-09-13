package com.gurella.studio.editor.model.extension;

import java.io.InputStream;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;

import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorItem;

public abstract class SwtEditorItem<T extends Item, P extends Widget> extends SwtEditorWidget<T> implements EditorItem {
	SwtEditorItem() {
	}

	SwtEditorItem(SwtEditorWidget<P> parent, int style) {
		init(createItem(parent.widget, style));
	}

	abstract T createItem(P parent, int style);

	@Override
	T createWidget(Composite parent, int style) {
		return null;
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
