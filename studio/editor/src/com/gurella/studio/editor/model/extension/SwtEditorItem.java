package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.editor.ui.EditorItem;

public abstract class SwtEditorItem<T extends Item> extends SwtEditorWidget<T> implements EditorItem {
	SwtEditorItem(SwtEditorWidget<?> parent) {
		init(createItem(parent));
	}

	abstract T createItem(SwtEditorWidget<?> parent);

	@Override
	T createWidget(Composite parent, FormToolkit toolkit) {
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
}
