package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.TabItem;

import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorTabItem;

public class SwtEditorTabItem extends SwtEditorItem<TabItem> implements EditorTabItem {
	SwtEditorTabItem(SwtEditorTabFolder parent) {
		super(parent);
	}

	@Override
	public SwtEditorControl<?> getControl() {
		return getEditorWidget(widget.getControl());
	}

	@Override
	public SwtEditorTabFolder getParent() {
		return getEditorWidget(widget.getParent());
	}

	@Override
	public String getToolTipText() {
		return widget.getToolTipText();
	}

	@Override
	public void setControl(EditorControl control) {
		widget.setControl(control == null ? null : ((SwtEditorControl<?>) control).widget);
	}

	@Override
	public void setToolTipText(String string) {
		widget.setToolTipText(string);
	}

	@Override
	TabItem createItem(SwtEditorWidget<?> parent) {
		SwtEditorTabFolder tabFolder = (SwtEditorTabFolder) parent;
		return new TabItem(tabFolder.widget, 0);
	}
}
