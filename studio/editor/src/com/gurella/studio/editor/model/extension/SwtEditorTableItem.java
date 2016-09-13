package com.gurella.studio.editor.model.extension;

import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TableItem;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.editor.ui.EditorFont;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorTableItem;
import com.gurella.engine.utils.GridRectangle;

public class SwtEditorTableItem<ELEMENT> extends SwtEditorItem<TableItem> implements EditorTableItem {
	SwtEditorTableItem(TableItem item) {
		super(item);
	}

	SwtEditorTableItem(SwtEditorTable<ELEMENT> parent) {
		super(new TableItem(parent.widget, SWT.NONE));
	}

	SwtEditorTableItem(SwtEditorTable<ELEMENT> parent, int index) {
		super(new TableItem(parent.widget, SWT.NONE, index));
	}

	@Override
	public Color getBackground() {
		return toGdxColor(widget.getBackground());
	}

	@Override
	public Color getBackground(int index) {
		return toGdxColor(widget.getBackground(index));
	}

	@Override
	public GridRectangle getBounds() {
		Rectangle bounds = widget.getBounds();
		return new GridRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	@Override
	public GridRectangle getBounds(int index) {
		Rectangle bounds = widget.getBounds(index);
		return new GridRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	@Override
	public boolean getChecked() {
		return widget.getChecked();
	}

	@Override
	public EditorFont getFont() {
		return toEditorFont(widget.getFont());
	}

	@Override
	public EditorFont getFont(int index) {
		return toEditorFont(widget.getFont(index));
	}

	@Override
	public void setFont(EditorFont font) {
		widget.setFont(toSwtFont(font));
	}

	@Override
	public void setFont(String name, int height, boolean bold, boolean italic) {
		widget.setFont(toSwtFont(name, height, bold, italic));
	}

	@Override
	public void setFont(int height, boolean bold, boolean italic) {
		widget.setFont(toSwtFont(widget.getFont(), height, bold, italic));
	}

	@Override
	public void setFont(int index, EditorFont font) {
		widget.setFont(index, toSwtFont(font));
	}

	@Override
	public void setFont(int index, String name, int height, boolean bold, boolean italic) {
		widget.setFont(index, toSwtFont(name, height, bold, italic));
	}

	@Override
	public void setFont(int index, int height, boolean bold, boolean italic) {
		widget.setFont(index, toSwtFont(widget.getFont(), height, bold, italic));
	}

	@Override
	public Color getForeground() {
		return toGdxColor(widget.getForeground());
	}

	@Override
	public Color getForeground(int index) {
		return toGdxColor(widget.getForeground(index));
	}

	@Override
	public boolean getGrayed() {
		return widget.getGrayed();
	}

	@Override
	public EditorImage getImage(int index) {
		return toEditorImage(widget.getImage(index));
	}

	@Override
	public GridRectangle getImageBounds(int index) {
		Rectangle bounds = widget.getImageBounds(index);
		return new GridRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	@Override
	public SwtEditorTable<ELEMENT> getParent() {
		return getEditorWidget(widget.getParent());
	}

	@Override
	public String getText(int index) {
		return widget.getText();
	}

	@Override
	public GridRectangle getTextBounds(int index) {
		Rectangle bounds = widget.getTextBounds(index);
		return new GridRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	@Override
	public void setBackground(Color color) {
		widget.setBackground(toSwtColor(color));
	}

	@Override
	public void setBackground(int r, int g, int b, int a) {
		widget.setBackground(toSwtColor(r, g, b, a));
	}

	@Override
	public void setBackground(int index, Color color) {
		widget.setBackground(index, toSwtColor(color));
	}

	@Override
	public void setBackground(int index, int r, int g, int b, int a) {
		widget.setBackground(index, toSwtColor(r, g, b, a));
	}

	@Override
	public void setChecked(boolean checked) {
		widget.setChecked(checked);
	}

	@Override
	public void setForeground(Color color) {
		widget.setForeground(toSwtColor(color));
	}

	@Override
	public void setForeground(int r, int g, int b, int a) {
		widget.setForeground(toSwtColor(r, g, b, a));
	}

	@Override
	public void setForeground(int index, Color color) {
		widget.setForeground(index, toSwtColor(color));
	}

	@Override
	public void setForeground(int index, int r, int g, int b, int a) {
		widget.setForeground(index, toSwtColor(r, g, b, a));
	}

	@Override
	public void setGrayed(boolean grayed) {
		widget.setGrayed(grayed);
	}

	@Override
	public void setImage(EditorImage[] images) {
		widget.setImage(Arrays.stream(images).sequential().map(i -> toSwtImage(i)).toArray(i -> new Image[i]));
	}

	@Override
	public void setImage(int index, EditorImage image) {
		widget.setImage(index, toSwtImage(image));
	}

	@Override
	public void setImage(int index, InputStream imageStream) {
		widget.setImage(index, toSwtImage(imageStream));
	}

	@Override
	public void setText(int index, String string) {
		widget.setText(index, string);
	}

	@Override
	public void setText(String[] strings) {
		widget.setText(strings);
	}
}
