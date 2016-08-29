package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.gurella.engine.editor.ui.Alignment;
import com.gurella.engine.editor.ui.EditorTableColumn;

public class SwtEditorTableColumn extends SwtEditorItem<TableColumn> implements EditorTableColumn {
	SwtEditorTableColumn(SwtEditorTable parent, int style) {
		super(parent, style);
	}

	@Override
	public Alignment getAlignment() {
		return SwtEditorUiFactory.alignmentFromSwt(widget.getAlignment());
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
		widget.setAlignment(SwtEditorUiFactory.alignmentToSwt(alignment));
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
	TableColumn createItem(SwtEditorWidget<?> parent, int style) {
		return new TableColumn((Table) parent.widget, style);
	}
}
