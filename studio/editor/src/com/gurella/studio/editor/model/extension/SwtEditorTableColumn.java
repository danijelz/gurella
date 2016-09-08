package com.gurella.studio.editor.model.extension;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.gurella.engine.editor.ui.Alignment;
import com.gurella.engine.editor.ui.EditorTableColumn;
import com.gurella.engine.editor.ui.viewer.EditorListViewer.LabelProvider;
import com.gurella.studio.editor.model.extension.style.SwtWidgetStyle;

public class SwtEditorTableColumn<ELEMENT> extends SwtEditorItem<TableColumn, Table>
		implements EditorTableColumn<ELEMENT> {
	TableViewerColumn viewerColumn;
	ColumnLabelProviderAdapter<ELEMENT> labelProviderAdapter;

	SwtEditorTableColumn(SwtEditorTable<ELEMENT> parent, int style) {
		super(parent, style);
		viewerColumn = new TableViewerColumn(getParent().viewer, widget);
	}

	SwtEditorTableColumn(SwtEditorTable<ELEMENT> parent, int style, int index) {
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
	public SwtEditorTable<ELEMENT> getParent() {
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

	public static class ColumnLabelProviderAdapter<ELEMENT> extends ColumnLabelProvider {
		LabelProvider<ELEMENT> labelProvider;

		public ColumnLabelProviderAdapter(LabelProvider<ELEMENT> labelProvider) {
			this.labelProvider = labelProvider;
		}

		@Override
		public Image getImage(Object element) {
			@SuppressWarnings("unchecked")
			ELEMENT casted = (ELEMENT) element;
			SwtEditorImage image = (SwtEditorImage) labelProvider.getImage(casted);
			return image == null ? null : image.image;
		}

		@Override
		public String getText(Object element) {
			@SuppressWarnings("unchecked")
			ELEMENT casted = (ELEMENT) element;
			return labelProvider.getText(casted);
		}
	}
}
