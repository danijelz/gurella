package com.gurella.studio.editor.model.extension;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ToolItem;

import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorToolItem;
import com.gurella.engine.utils.GridRectangle;

public class SwtEditorToolItem extends SwtEditorItem<ToolItem> implements EditorToolItem {
	SwtEditorToolItem(SwtEditorToolBar parent) {
		super(parent);
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
		Image image = widget.getDisabledImage();
		return image == null ? null : new SwtEditorImage(image);
	}

	@Override
	public boolean getEnabled() {
		return widget.getEnabled();
	}

	@Override
	public EditorImage getHotImage() {
		Image image = widget.getHotImage();
		return image == null ? null : new SwtEditorImage(image);
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
		if (imageStream == null) {
			widget.setDisabledImage(null);
		} else {
			Image image = new Image(widget.getDisplay(), imageStream);
			widget.addListener(SWT.Dispose, e -> image.dispose());
			widget.setDisabledImage(image);
		}
	}

	@Override
	public void setDisabledImage(EditorImage image) {
		widget.setDisabledImage(image == null ? null : ((SwtEditorImage) image).image);
	}

	@Override
	public void setEnabled(boolean enabled) {
		widget.setEnabled(enabled);
	}

	@Override
	public void setHotImage(EditorImage image) {
		widget.setHotImage(image == null ? null : ((SwtEditorImage) image).image);
	}

	@Override
	public void setHotImage(InputStream imageStream) {
		if (imageStream == null) {
			widget.setHotImage(null);
		} else {
			Image image = new Image(widget.getDisplay(), imageStream);
			widget.addListener(SWT.Dispose, e -> image.dispose());
			widget.setHotImage(image);
		}
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
	ToolItem createItem(SwtEditorWidget<?> parent) {
		SwtEditorToolBar toolBar = (SwtEditorToolBar) parent;
		return new ToolItem(toolBar.widget, style);
	}
}
