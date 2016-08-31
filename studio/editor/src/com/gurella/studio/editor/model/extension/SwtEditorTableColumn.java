package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.gurella.engine.editor.ui.Alignment;
import com.gurella.engine.editor.ui.EditorTableColumn;
import com.gurella.studio.editor.model.extension.style.SwtWidgetStyle;

public class SwtEditorTableColumn extends SwtEditorItem<TableColumn, Table> implements EditorTableColumn {
	SwtEditorTableColumn(SwtEditorTable parent, int style) {
		super(parent, style);
	}

	SwtEditorTableColumn(SwtEditorTable parent, int style, int index) {
		init(new TableColumn(parent.widget, style, index));
	}

	@Override
	public Alignment getAlignment() {
		return SwtWidgetStyle.alignment(widget.getAlignment());
	}

	@Override
	public boolean getMoveable() {
		return widget.getMoveable();
	}

	@Override
	public SwtEditorTable getParent() {
		return getEditorWidget(widget.getParent());
	}

	@Override
	public boolean getResizable() {
		return widget.getResizable();
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
	public void pack() {
		widget.pack();
	}

	@Override
	public void setAlignment(Alignment alignment) {
		widget.setAlignment(SwtWidgetStyle.alignment(alignment));
	}

	@Override
	public void setMoveable(boolean moveable) {
		widget.setMoveable(moveable);
	}

	@Override
	public void setResizable(boolean resizable) {
		widget.setResizable(resizable);
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
	TableColumn createItem(Table parent, int style) {
		return new TableColumn(parent, style);
	}
}
