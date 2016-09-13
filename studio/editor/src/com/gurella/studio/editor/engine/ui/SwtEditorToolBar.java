package com.gurella.studio.editor.engine.ui;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.ToolBar;

import com.gurella.engine.editor.ui.EditorToolBar;
import com.gurella.engine.editor.ui.EditorToolItem;
import com.gurella.engine.editor.ui.EditorToolItem.ToolItemType;

public class SwtEditorToolBar extends SwtEditorBaseComposite<ToolBar> implements EditorToolBar {
	public SwtEditorToolBar(SwtEditorLayoutComposite<?> parent, int style) {
		super(new ToolBar(parent.widget, style));
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
		return Arrays.stream(widget.getItems()).sequential().map(i -> getEditorWidget(i))
				.toArray(i -> new SwtEditorToolItem[i]);
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
	public SwtEditorToolItem createItem(ToolItemType type) {
		return new SwtEditorToolItem(this, getToolItemStyle(type));
	}

	@Override
	public SwtEditorToolItem createItem(int index, ToolItemType type) {
		return new SwtEditorToolItem(this, index, getToolItemStyle(type));
	}

	public static int getToolItemStyle(ToolItemType type) {
		switch (type) {
		case CHECK:
			return SWT.CHECK;
		case DROP_DOWN:
			return SWT.DROP_DOWN;
		case PUSH:
			return SWT.PUSH;
		case RADIO:
			return SWT.RADIO;
		case SEPARATOR:
			return SWT.SEPARATOR;
		default:
			throw new IllegalArgumentException();
		}
	}
}
