package com.gurella.studio.editor.engine.ui;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;

import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorScrolledComposite;

public abstract class SwtEditorBaseScrolledComposite<T extends ScrolledComposite> extends SwtEditorLayoutComposite<T>
		implements EditorScrolledComposite {
	public SwtEditorBaseScrolledComposite(T composite) {
		super(composite);
	}

	@Override
	public boolean getAlwaysShowScrollBars() {
		return widget.getAlwaysShowScrollBars();
	}

	@Override
	public EditorControl getContent() {
		return getEditorWidget(widget.getContent());
	}

	@Override
	public boolean getExpandHorizontal() {
		return widget.getExpandHorizontal();
	}

	@Override
	public boolean getExpandVertical() {
		return widget.getExpandVertical();
	}

	@Override
	public int getMinHeight() {
		return widget.getMinHeight();
	}

	@Override
	public int getMinWidth() {
		return widget.getMinWidth();
	}

	@Override
	public GridPoint2 getOrigin() {
		Point origin = widget.getOrigin();
		return new GridPoint2(origin.x, origin.y);
	}

	@Override
	public boolean getShowFocusedControl() {
		return widget.getShowFocusedControl();
	}

	@Override
	public void setAlwaysShowScrollBars(boolean show) {
		widget.setAlwaysShowScrollBars(show);
	}

	@Override
	public void setContent(EditorControl content) {
		widget.setContent(content == null ? null : ((SwtEditorControl<?>) content).widget);
	}

	@Override
	public void setExpandHorizontal(boolean expand) {
		widget.setExpandHorizontal(expand);
	}

	@Override
	public void setExpandVertical(boolean expand) {
		widget.setExpandVertical(expand);
	}

	@Override
	public void setMinHeight(int height) {
		widget.setMinHeight(height);
	}

	@Override
	public void setMinSize(int width, int height) {
		widget.setMinSize(width, height);
	}

	@Override
	public void setMinWidth(int width) {
		widget.setMinWidth(width);
	}

	@Override
	public void setOrigin(int x, int y) {
		widget.setOrigin(x, y);
	}

	@Override
	public void setShowFocusedControl(boolean show) {
		widget.setShowFocusedControl(show);
	}

	@Override
	public void showControl(EditorControl control) {
		widget.showControl(control == null ? null : ((SwtEditorControl<?>) control).widget);
	}
}
