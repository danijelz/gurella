package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorTabItem;

public class SwtEditorTabItem extends SwtEditorItem<TabItem, TabFolder> implements EditorTabItem {
	SwtEditorTabItem(SwtEditorTabFolder parent, int style) {
		super(parent, style);
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
	TabItem createItem(TabFolder parent, int style) {
		return new TabItem(parent, style);
	}
}
