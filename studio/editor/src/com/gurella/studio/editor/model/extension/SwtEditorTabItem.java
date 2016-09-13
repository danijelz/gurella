package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorTabItem;

public class SwtEditorTabItem extends SwtEditorItem<TabItem, TabFolder> implements EditorTabItem {
	SwtEditorTabItem(SwtEditorTabFolder parent) {
		super(new TabItem(parent.widget, SWT.NONE));
	}

	SwtEditorTabItem(SwtEditorTabFolder parent, int index) {
		super(new TabItem(parent.widget, SWT.NONE, index));
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
}
