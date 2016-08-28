package com.gurella.studio.editor.model.extension;

import java.util.Arrays;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.gurella.engine.editor.ui.EditorToolBar;
import com.gurella.engine.editor.ui.EditorToolItem;

public class SwtEditorToolBar extends SwtEditorBaseComposite<ToolBar> implements EditorToolBar {
	public SwtEditorToolBar(SwtEditorBaseComposite<?> parent, int style) {
		super(parent, style);
	}

	@Override
	public SwtEditorToolItem getItem(int index) {
		return getEditorWidget(widget.getItem(index));
	}

	@Override
	public SwtEditorToolItem getItem(int x, int y) {
		return getEditorWidget(widget.getItem(new Point(x, y)));
	}

	@Override
	public int getItemCount() {
		return widget.getItemCount();
	}

	@Override
	public SwtEditorToolItem[] getItems() {
		return Arrays.stream(widget.getItems()).map(i -> getEditorWidget(i)).toArray(i -> new SwtEditorToolItem[i]);
	}

	@Override
	public int getRowCount() {
		return widget.getRowCount();
	}

	@Override
	public int indexOf(EditorToolItem item) {
		return widget.indexOf(((SwtEditorToolItem) item).widget);
	}

	@Override
	ToolBar createWidget(Composite parent, int style) {
		return new ToolBar(parent, style);
	}
}
