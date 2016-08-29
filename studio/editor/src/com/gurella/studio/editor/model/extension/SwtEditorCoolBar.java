package com.gurella.studio.editor.model.extension;

import java.util.Arrays;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;

import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.editor.ui.EditorCoolBar;
import com.gurella.engine.editor.ui.EditorCoolItem;

public class SwtEditorCoolBar extends SwtEditorBaseComposite<CoolBar> implements EditorCoolBar {
	public SwtEditorCoolBar(SwtEditorComposite parent, int style) {
		super(parent, style);
	}

	@Override
	public SwtEditorCoolItem getItem(int index) {
		return getEditorWidget(widget.getItem(index));
	}

	@Override
	public int getItemCount() {
		return widget.getItemCount();
	}

	@Override
	public int[] getItemOrder() {
		return widget.getItemOrder();
	}

	@Override
	public SwtEditorCoolItem[] getItems() {
		return Arrays.stream(widget.getItems()).map(i -> getEditorWidget(i)).toArray(i -> new SwtEditorCoolItem[i]);
	}

	@Override
	public GridPoint2[] getItemSizes() {
		return Arrays.stream(widget.getItemSizes()).map(p -> new GridPoint2(p.x, p.y)).toArray(i -> new GridPoint2[i]);
	}

	@Override
	public boolean getLocked() {
		return widget.getLocked();
	}

	@Override
	public int[] getWrapIndices() {
		return widget.getWrapIndices();
	}

	@Override
	public int indexOf(EditorCoolItem item) {
		return widget.indexOf(((SwtEditorCoolItem) item).widget);
	}

	@Override
	public void setItemLayout(int[] itemOrder, int[] wrapIndices, GridPoint2[] sizes) {
		Point[] points = Arrays.stream(sizes).map(p -> new Point(p.x, p.y)).toArray(i -> new Point[i]);
		widget.setItemLayout(itemOrder, wrapIndices, points);
	}

	@Override
	public void setLocked(boolean locked) {
		widget.setLocked(locked);
	}

	@Override
	public void setWrapIndices(int[] indices) {
		widget.setWrapIndices(indices);
	}

	@Override
	CoolBar createWidget(Composite parent, int style) {
		return new CoolBar(parent, style);
	}
}
