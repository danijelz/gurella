package com.gurella.studio.editor.model.extension;

import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.widgets.TreeColumn;

import com.gurella.engine.editor.ui.Alignment;
import com.gurella.engine.editor.ui.EditorTreeColumn;
import com.gurella.engine.editor.ui.viewer.EditorViewer.LabelProvider;
import com.gurella.studio.editor.model.extension.style.SwtWidgetStyle;

public class SwtEditorTreeColumn<ELEMENT> extends SwtEditorItem<TreeColumn> implements EditorTreeColumn<ELEMENT> {
	TreeViewerColumn viewerColumn;
	ColumnLabelProviderAdapter<ELEMENT> labelProviderAdapter;

	SwtEditorTreeColumn(TreeColumn column) {
		super(column);
		viewerColumn = new TreeViewerColumn(getParent().viewer, widget);
	}

	SwtEditorTreeColumn(SwtEditorTree<ELEMENT> parent, int style) {
		super(new TreeColumn(parent.widget, style));
		viewerColumn = new TreeViewerColumn(getParent().viewer, widget);
	}

	SwtEditorTreeColumn(SwtEditorTree<ELEMENT> parent, int index, int style) {
		super(new TreeColumn(parent.widget, style, index));
		viewerColumn = new TreeViewerColumn(getParent().viewer, widget);
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
	public SwtEditorTree<ELEMENT> getParent() {
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
	public LabelProvider<ELEMENT> getLabelProvider() {
		return labelProviderAdapter == null ? null : labelProviderAdapter.labelProvider;
	}

	@Override
	public void setLabelProvider(LabelProvider<ELEMENT> labelProvider) {
		if (labelProvider == null) {
			viewerColumn.setLabelProvider(null);
		} else {
			ColumnLabelProviderAdapter<ELEMENT> adapter = new ColumnLabelProviderAdapter<>(labelProvider);
			widget.addDisposeListener(e -> adapter.dispose());
			viewerColumn.setLabelProvider(adapter);
		}
	}
}
