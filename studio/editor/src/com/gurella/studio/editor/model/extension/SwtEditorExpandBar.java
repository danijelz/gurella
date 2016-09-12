package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;

import com.gurella.engine.editor.ui.EditorExpandBar;
import com.gurella.engine.editor.ui.EditorExpandItem;

public class SwtEditorExpandBar extends SwtEditorBaseComposite<ExpandBar> implements EditorExpandBar {
	public SwtEditorExpandBar(SwtEditorLayoutComposite<?> parent, int style) {
		super(parent, style);
	}

	@Override
	public SwtEditorExpandItem getItem(int index) {
		return getEditorWidget(widget.getItem(index));
	}

	@Override
	public int getItemCount() {
		return widget.getItemCount();
	}

	@Override
	public SwtEditorExpandItem[] getItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSpacing() {
		return widget.getSpacing();
	}

	@Override
	public int indexOf(EditorExpandItem item) {
		return widget.indexOf(((SwtEditorExpandItem) item).widget);
	}

	@Override
	public void setSpacing(int spacing) {
		widget.setSpacing(spacing);
	}

	@Override
	ExpandBar createWidget(Composite parent, int style) {
		return new ExpandBar(parent, style);
	}

	@Override
	public SwtEditorExpandItem createItem() {
		return new SwtEditorExpandItem(this);
	}

	@Override
	public SwtEditorExpandItem createItem(int index) {
		return new SwtEditorExpandItem(this, index);
	}
}
