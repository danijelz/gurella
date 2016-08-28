package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;

import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.editor.ui.EditorScrollBar;
import com.gurella.engine.utils.GridRectangle;

public class SwtEditorScrollBar extends SwtEditorWidget<ScrollBar> implements EditorScrollBar {
	SwtEditorScrollBar(ScrollBar scrollBar) {
		init(scrollBar);
	}

	@Override
	public boolean getEnabled() {
		return widget.getEnabled();
	}

	@Override
	public boolean isEnabled() {
		return widget.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		widget.setEnabled(enabled);
	}

	@Override
	public int getIncrement() {
		return widget.getIncrement();
	}

	@Override
	public void setIncrement(int value) {
		widget.setIncrement(value);
	}

	@Override
	public int getMaximum() {
		return widget.getMaximum();
	}

	@Override
	public void setMaximum(int value) {
		widget.setMaximum(value);
	}

	@Override
	public int getMinimum() {
		return widget.getMinimum();
	}

	@Override
	public void setMinimum(int value) {
		widget.setMinimum(value);
	}

	@Override
	public int getPageIncrement() {
		return widget.getPageIncrement();
	}

	@Override
	public void setPageIncrement(int value) {
		widget.setPageIncrement(value);
	}

	@Override
	public SwtEditorScrollable<?> getParent() {
		return getEditorWidget(widget.getParent());
	}

	@Override
	public int getSelection() {
		return widget.getSelection();
	}

	@Override
	public void setSelection(int selection) {
		widget.setSelection(selection);
	}

	@Override
	public GridPoint2 getSize() {
		Point size = widget.getSize();
		return new GridPoint2(size.x, size.y);
	}

	@Override
	public int getThumb() {
		return widget.getThumb();
	}

	@Override
	public void setThumb(int value) {
		widget.setThumb(value);
	}

	@Override
	public GridRectangle getThumbBounds() {
		Rectangle bounds = widget.getThumbBounds();
		return new GridRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	@Override
	public GridRectangle getThumbTrackBounds() {
		Rectangle bounds = widget.getThumbTrackBounds();
		return new GridRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	@Override
	public boolean getVisible() {
		return widget.getVisible();
	}

	@Override
	public boolean isVisible() {
		return widget.isVisible();
	}

	@Override
	public void setVisible(boolean visible) {
		widget.setVisible(visible);
	}

	@Override
	public void setValues(int selection, int minimum, int maximum, int thumb, int increment, int pageIncrement) {
		widget.setValues(selection, minimum, maximum, thumb, increment, pageIncrement);
	}

	@Override
	ScrollBar createWidget(Composite parent, int style) {
		return null;
	}
}
