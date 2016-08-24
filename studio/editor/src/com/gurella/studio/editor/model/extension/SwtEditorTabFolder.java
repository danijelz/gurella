package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.editor.ui.EditorTabFolder;
import com.gurella.engine.editor.ui.EditorTabItem;

public class SwtEditorTabFolder extends SwtEditorBaseComposite<TabFolder> implements EditorTabFolder {
	public SwtEditorTabFolder(SwtEditorBaseComposite<?> parent) {
		super(parent);
	}

	@Override
	public EditorTabItem getItem(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EditorTabItem getItem(GridPoint2 point) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public EditorTabItem[] getItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EditorTabItem[] getSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSelectionIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int indexOf(EditorTabItem item) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setSelection(int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSelection(EditorTabItem item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSelection(EditorTabItem[] items) {
		// TODO Auto-generated method stub

	}

	@Override
	TabFolder createWidget(Composite parent) {
		// TODO Auto-generated method stub
		return null;
	}
}
