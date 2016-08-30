package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorExpandItem;

public class SwtEditorExpandItem extends SwtEditorItem<ExpandItem, ExpandBar> implements EditorExpandItem {
	public SwtEditorExpandItem(SwtEditorExpandBar parent) {
		super(parent, 0);
	}

	public SwtEditorExpandItem(SwtEditorExpandBar parent, int index) {
		init(new ExpandItem(parent.widget, 0, index));
	}

	@Override
	public SwtEditorControl<?> getControl() {
		return getEditorWidget(widget.getControl());
	}

	@Override
	public boolean getExpanded() {
		return widget.getExpanded();
	}

	@Override
	public int getHeaderHeight() {
		return widget.getHeaderHeight();
	}

	@Override
	public int getHeight() {
		return widget.getHeight();
	}

	@Override
	public SwtEditorExpandBar getParent() {
		return getEditorWidget(widget.getParent());
	}

	@Override
	public void setControl(EditorControl control) {
		widget.setControl(control == null ? null : ((SwtEditorControl<?>) control).widget);
	}

	@Override
	public void setExpanded(boolean expanded) {
		widget.setExpanded(expanded);
	}

	@Override
	public void setHeight(int height) {
		widget.setHeight(height);
	}

	@Override
	ExpandItem createItem(ExpandBar parent, int style) {
		return new ExpandItem(parent, style);
	}
}
