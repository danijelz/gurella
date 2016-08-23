package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.MenuItem;

import com.gurella.engine.editor.ui.EditorMenu;
import com.gurella.engine.editor.ui.EditorMenuItem;

public class SwtEditorMenuItem extends SwtEditorItem<MenuItem> implements EditorMenuItem {
	SwtEditorMenuItem(SwtEditorMenu parent) {
		super(parent);
	}

	@Override
	public int getID() {
		return widget.getID();
	}

	@Override
	public void setID(int id) {
		widget.setID(id);
	}

	@Override
	public int getAccelerator() {
		return widget.getAccelerator();
	}

	@Override
	public void setAccelerator(int accelerator) {
		widget.setAccelerator(accelerator);
	}

	@Override
	public boolean isEnabled() {
		return widget.isEnabled();
	}

	@Override
	public boolean getEnabled() {
		return widget.getEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		widget.setEnabled(enabled);
	}

	@Override
	public SwtEditorMenu getParent() {
		return getEditorWidget(widget.getParent());
	}

	@Override
	public SwtEditorMenu getMenu() {
		return getEditorWidget(widget.getMenu());
	}

	@Override
	public void setMenu(EditorMenu menu) {
		SwtEditorMenu swtMenu = (SwtEditorMenu) menu;
		widget.setMenu(swtMenu.widget);
	}

	@Override
	public boolean getSelection() {
		return widget.getSelection();
	}

	@Override
	public void setSelection(boolean selected) {
		widget.setSelection(selected);
	}

	@Override
	MenuItem createItem(SwtEditorWidget<?> parent) {
		SwtEditorMenu menu = (SwtEditorMenu) parent;
		return new MenuItem(menu.widget, 0);
	}
}
