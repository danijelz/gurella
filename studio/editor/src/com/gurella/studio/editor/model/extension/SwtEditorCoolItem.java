package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.CoolItem;

import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorCoolItem;
import com.gurella.engine.utils.GridRectangle;

public class SwtEditorCoolItem extends SwtEditorItem<CoolItem> implements EditorCoolItem {
	SwtEditorCoolItem(SwtEditorCoolBar parent, int style) {
		super(new CoolItem(parent.widget, style));
	}

	@Override
	public GridRectangle getBounds() {
		Rectangle bounds = widget.getBounds();
		return new GridRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	@Override
	public SwtEditorCoolBar getParent() {
		return getEditorWidget(widget.getParent());
	}

	@Override
	public SwtEditorControl<?> getControl() {
		return getEditorWidget(widget.getControl());
	}

	@Override
	public void setControl(EditorControl control) {
		widget.setControl(((SwtEditorControl<?>) control).widget);
	}

	@Override
	public GridPoint2 getMinimumSize() {
		Point point = widget.getMinimumSize();
		return new GridPoint2(point.x, point.y);
	}

	@Override
	public void setMinimumSize(int width, int height) {
		widget.setMinimumSize(width, height);
	}

	@Override
	public GridPoint2 getPreferredSize() {
		Point point = widget.getPreferredSize();
		return new GridPoint2(point.x, point.y);
	}

	@Override
	public void setPreferredSize(int width, int height) {
		widget.setPreferredSize(width, height);
	}

	@Override
	public GridPoint2 getSize() {
		Point point = widget.getSize();
		return new GridPoint2(point.x, point.y);
	}

	@Override
	public void setSize(int width, int height) {
		widget.setSize(width, height);
	}
}
