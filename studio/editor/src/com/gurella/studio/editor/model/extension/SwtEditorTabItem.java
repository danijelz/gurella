package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.TabItem;

import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorTabItem;

public class SwtEditorTabItem extends SwtEditorItem<TabItem> implements EditorTabItem {
	SwtEditorTabItem(SwtEditorTabFolder parent) {
		super(parent);
	}

	@Override
	public SwtEditorControl<?> getControl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SwtEditorTabFolder getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToolTipText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setControl(EditorControl control) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setImage(EditorImage image) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setToolTipText(String string) {
		// TODO Auto-generated method stub

	}

	@Override
	TabItem createItem(SwtEditorWidget<?> parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
