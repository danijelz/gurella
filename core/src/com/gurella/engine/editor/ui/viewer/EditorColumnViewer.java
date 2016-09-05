package com.gurella.engine.editor.ui.viewer;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorFont;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorWidget;
import com.gurella.engine.utils.GridRectangle;

public interface EditorColumnViewer<ELEMENT, SELECTION> extends EditorViewer<List<ELEMENT>, ELEMENT, SELECTION> {
	ViewerCell<ELEMENT> getCell(int x, int y);

	Object[] getColumnProperties();

	CellLabelProvider getLabelProvider(int columnIndex);

	void setColumnProperties(String[] columnProperties);

	interface CellLabelProvider {

	}

	public interface ViewerCell<ELEMENT> {
		Color getBackground();

		GridRectangle getBounds();

		int getColumnIndex();

		EditorControl getControl();

		ELEMENT getElement();

		EditorFont getFont();

		Color getForeground();

		EditorImage getImage();

		GridRectangle getImageBounds();

		EditorWidget getItem();

		String getText();

		GridRectangle getTextBounds();

		ViewerRow<ELEMENT> getViewerRow();

		boolean scrollIntoView();

		void setBackground(Color background);

		void setFont(EditorFont font);

		void setForeground(Color foreground);

		void setImage(EditorImage image);

		void setText(String text);
	}

	public interface ViewerRow<ELEMENT> {
		Color getBackground(int columnIndex);

		GridRectangle getBounds();

		GridRectangle getBounds(int columnIndex);

		ViewerCell<ELEMENT> getCell(int column);

		ViewerCell<ELEMENT> getCell(int x, int y);

		int getColumnCount();

		int getColumnIndex(int x, int y);

		EditorControl getControl();

		ELEMENT getElement();

		EditorFont getFont(int columnIndex);

		Color getForeground(int columnIndex);

		EditorImage getImage(int columnIndex);

		GridRectangle getImageBounds(int index);

		EditorWidget getItem();

		ViewerRow<ELEMENT> getNeighbor(int direction, boolean sameLevel);

		String getText(int columnIndex);

		GridRectangle getTextBounds(int index);

		void setBackground(int columnIndex, Color color);

		void setFont(int columnIndex, EditorFont font);

		void setForeground(int columnIndex, Color color);

		void setImage(int columnIndex, EditorImage image);

		void setText(int columnIndex, String text);
	}
}
