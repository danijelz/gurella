package com.gurella.studio.editor.model.extension;

import java.util.Arrays;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.editor.ui.EditorTabFolder;
import com.gurella.engine.editor.ui.EditorTabItem;

public class SwtEditorTabFolder extends SwtEditorBaseComposite<TabFolder> implements EditorTabFolder {
	public SwtEditorTabFolder(SwtEditorComposite parent, int style) {
		super(parent, style);
	}

	@Override
	public SwtEditorTabItem getItem(int index) {
		return getEditorWidget(widget.getItem(index));
	}

	@Override
	public SwtEditorTabItem getItem(GridPoint2 point) {
		return getEditorWidget(widget.getItem(new Point(point.x, point.y)));
	}

	@Override
	public int getItemCount() {
		return widget.getItemCount();
	}

	@Override
	public SwtEditorTabItem[] getItems() {
		return Arrays.stream(widget.getItems()).map(i -> getEditorWidget(i)).toArray(i -> new SwtEditorTabItem[i]);
	}

	@Override
	public SwtEditorTabItem[] getSelection() {
		return Arrays.stream(widget.getSelection()).map(i -> getEditorWidget(i)).toArray(i -> new SwtEditorTabItem[i]);
	}

	@Override
	public int getSelectionIndex() {
		return widget.getSelectionIndex();
	}

	@Override
	public int indexOf(EditorTabItem item) {
		return widget.indexOf(((SwtEditorTabItem) item).widget);
	}

	@Override
	public void setSelection(int index) {
		widget.setSelection(index);
	}

	@Override
	public void setSelection(EditorTabItem item) {
		widget.setSelection(item == null ? null : ((SwtEditorTabItem) item).widget);
	}

	@Override
	public void setSelection(EditorTabItem[] items) {
		widget.setSelection(Arrays.stream(items).map(i -> ((SwtEditorTabItem) i).widget).toArray(i -> new TabItem[i]));
	}

	@Override
	TabFolder createWidget(Composite parent, int style) {
		return new TabFolder(parent, style);
	}

	@Override
	public SwtEditorTabItem createItem() {
		return new SwtEditorTabItem(this);
	}

	@Override
	public SwtEditorTabItem createItem(int index) {
		return new SwtEditorTabItem(this, index);
	}
}
