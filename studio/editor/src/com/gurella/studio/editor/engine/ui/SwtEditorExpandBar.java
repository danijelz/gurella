package com.gurella.studio.editor.engine.ui;

import java.util.Arrays;

import org.eclipse.swt.widgets.ExpandBar;

import com.gurella.engine.editor.ui.EditorExpandBar;
import com.gurella.engine.editor.ui.EditorExpandItem;

public class SwtEditorExpandBar extends SwtEditorBaseComposite<ExpandBar> implements EditorExpandBar {
	public SwtEditorExpandBar(SwtEditorLayoutComposite<?> parent, int style) {
		super(new ExpandBar(parent.widget, style));
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
		return Arrays.stream(widget.getItems()).map(i -> getEditorWidget(i)).toArray(i -> new SwtEditorExpandItem[i]);
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
	public SwtEditorExpandItem createItem() {
		return new SwtEditorExpandItem(this);
	}

	@Override
	public SwtEditorExpandItem createItem(int index) {
		return new SwtEditorExpandItem(this, index);
	}
}
