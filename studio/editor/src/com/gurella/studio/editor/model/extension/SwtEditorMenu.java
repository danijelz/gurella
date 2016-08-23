package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.editor.ui.Direction;
import com.gurella.engine.editor.ui.EditorMenu;
import com.gurella.engine.editor.ui.EditorMenuItem;

public class SwtEditorMenu extends SwtEditorWidget<Menu> implements EditorMenu {
	SwtEditorMenu(SwtEditorControl<?> parent) {
		init(new Menu(parent.widget));
	}

	SwtEditorMenu(SwtEditorMenu parentMenu) {
		init(new Menu(parentMenu.widget));
	}

	SwtEditorMenu(SwtEditorMenuItem parentItem) {
	}

	@Override
	public EditorMenuItem getDefaultItem() {
		return getEditorWidget(widget.getDefaultItem());
	}

	@Override
	public void setDefaultItem(EditorMenuItem item) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getEnabled() {
		return widget.getEnabled();
	}

	@Override
	public EditorMenuItem getItem(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getItemCount() {
		return widget.getItemCount();
	}

	@Override
	public EditorMenuItem[] getItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Direction getOrientation() {
		int orientation = widget.getOrientation();
		switch (orientation) {
		case SWT.LEFT_TO_RIGHT:
			return Direction.leftToRight;
		case SWT.RIGHT_TO_LEFT:
			return Direction.rightToLeft;
		default:
			return null;
		}
	}

	@Override
	public SwtEditorComposite getParent() {
		return getEditorWidget(widget.getParent());
	}

	@Override
	public EditorMenuItem getParentItem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SwtEditorMenu getParentMenu() {
		return getEditorWidget(widget.getParentMenu());
	}

	@Override
	public boolean getVisible() {
		return widget.getVisible();
	}

	@Override
	public int indexOf(EditorMenuItem item) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEnabled() {
		return widget.isEnabled();
	}

	@Override
	public boolean isVisible() {
		return widget.isVisible();
	}

	@Override
	public void setEnabled(boolean enabled) {
		widget.setEnabled(enabled);
	}

	@Override
	public void setLocation(int x, int y) {
		widget.setLocation(x, y);
	}

	@Override
	public void setOrientation(Direction orientation) {
		switch (orientation) {
		case leftToRight:
			widget.setOrientation(SWT.LEFT_TO_RIGHT);
			break;
		case rightToLeft:
			widget.setOrientation(SWT.RIGHT_TO_LEFT);
			break;
		}
	}

	@Override
	public void setVisible(boolean visible) {
		widget.setVisible(visible);
	}

	@Override
	Menu createWidget(Composite parent, FormToolkit toolkit) {
		return null;
	}
}
