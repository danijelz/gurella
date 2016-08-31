package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.gurella.engine.editor.ui.Alignment;
import com.gurella.engine.editor.ui.EditorTreeColumn;
import com.gurella.studio.editor.model.extension.style.SwtWidgetStyle;

public class SwtEditorTreeColumn extends SwtEditorItem<TreeColumn, Tree> implements EditorTreeColumn {
	SwtEditorTreeColumn(SwtEditorTree parent, int style) {
		super(parent, style);
	}

	SwtEditorTreeColumn(SwtEditorTree parent, int index, int style) {
		init(new TreeColumn(parent.widget, style, index));
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
	public SwtEditorTree getParent() {
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
	TreeColumn createItem(Tree parent, int style) {
		return new TreeColumn(parent, style);
	}
}
