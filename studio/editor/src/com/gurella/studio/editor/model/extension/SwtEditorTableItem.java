package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorTableItem;
import com.gurella.engine.editor.ui.FontData;
import com.gurella.engine.utils.GridRectangle;

public class SwtEditorTableItem extends SwtEditorItem<TableItem> implements EditorTableItem {
	SwtEditorTableItem(SwtEditorTable parent, int style) {
		super(parent, style);
	}

	@Override
	public Color getBackground() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getBackground(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GridRectangle getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GridRectangle getBounds(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getChecked() {
		return widget.getChecked();
	}

	@Override
	public FontData getFont() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FontData getFont(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getForeground() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getForeground(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getGrayed() {
		return widget.getGrayed();
	}

	@Override
	public EditorImage getImage(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GridRectangle getImageBounds(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SwtEditorTable getParent() {
		return getEditorWidget(widget.getParent());
	}

	@Override
	public String getText(int index) {
		return widget.getText();
	}

	@Override
	public GridRectangle getTextBounds(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBackground(Color color) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBackground(int index, Color color) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setChecked(boolean checked) {
		widget.setChecked(checked);
	}

	@Override
	public void setFont(FontData font) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFont(int index, FontData font) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setForeground(Color color) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setForeground(int index, Color color) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGrayed(boolean grayed) {
		widget.setGrayed(grayed);
	}

	@Override
	public void setImage(EditorImage[] images) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setImage(int index, EditorImage image) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setText(int index, String string) {
		widget.setText(index, string);
	}

	@Override
	public void setText(String[] strings) {
		widget.setText(strings);
	}

	@Override
	TableItem createItem(SwtEditorWidget<?> parent, int style) {
		return new TableItem((Table) parent.widget, style);
	}
}
