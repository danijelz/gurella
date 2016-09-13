package com.gurella.studio.editor.model.extension;

import java.io.InputStream;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorToolItem;
import com.gurella.engine.utils.GridRectangle;

public class SwtEditorToolItem extends SwtEditorItem<ToolItem, ToolBar> implements EditorToolItem {
	SwtEditorToolItem(SwtEditorToolBar parent, int style) {
		super(parent, style);
	}

	SwtEditorToolItem(SwtEditorToolBar parent, int index, int style) {
		init(new ToolItem(parent.widget, style, index));
	}

	@Override
	public GridRectangle getBounds() {
		Rectangle bounds = widget.getBounds();
		return new GridRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	@Override
	public SwtEditorControl<?> getControl() {
		return getEditorWidget(widget.getControl());
	}

	@Override
	public EditorImage getDisabledImage() {
		return toEditorImage(widget.getDisabledImage());
	}

	@Override
	public boolean getEnabled() {
		return widget.getEnabled();
	}

	@Override
	public EditorImage getHotImage() {
		return toEditorImage(widget.getHotImage());
	}

	@Override
	public SwtEditorToolBar getParent() {
		return getEditorWidget(widget.getParent());
	}

	@Override
	public boolean getSelection() {
		return widget.getSelection();
	}

	@Override
	public String getToolTipText() {
		return widget.getToolTipText();
	}

	@Override
	public int getWidth() {
		return widget.getWidth();
	}

	@Override
	public boolean isEnabled() {
		return widget.isEnabled();
	}

	@Override
	public void setControl(EditorControl control) {
		widget.setControl(control == null ? null : ((SwtEditorControl<?>) control).widget);
	}

	@Override
	public void setDisabledImage(InputStream imageStream) {
		widget.setDisabledImage(toSwtImage(imageStream));
	}

	@Override
	public void setDisabledImage(EditorImage image) {
		widget.setDisabledImage(toSwtImage(image));
	}

	@Override
	public void setEnabled(boolean enabled) {
		widget.setEnabled(enabled);
	}

	@Override
	public void setHotImage(EditorImage image) {
		widget.setHotImage(toSwtImage(image));
	}

	@Override
	public void setHotImage(InputStream imageStream) {
		widget.setHotImage(toSwtImage(imageStream));
	}

	@Override
	public void setSelection(boolean selected) {
		widget.setSelection(selected);
	}

	@Override
	public void setToolTipText(String string) {
		widget.setToolTipText(string);
	}

	@Override
	public void setWidth(int width) {
		widget.setWidth(width);
	}

	@Override
	ToolItem createItem(ToolBar parent, int style) {
		return new ToolItem(parent, style);
	}
}
