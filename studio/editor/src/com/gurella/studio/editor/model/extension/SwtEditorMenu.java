package com.gurella.studio.editor.model.extension;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.gurella.engine.editor.ui.Direction;
import com.gurella.engine.editor.ui.EditorMenu;
import com.gurella.engine.editor.ui.EditorMenuItem;
import com.gurella.engine.editor.ui.EditorMenuItem.MenuItemType;

public class SwtEditorMenu extends SwtEditorWidget<Menu> implements EditorMenu {
	SwtEditorMenu(SwtEditorControl<?> parent) {
		init(new Menu(parent.widget));
	}

	SwtEditorMenu(SwtEditorMenu parentMenu) {
		init(new Menu(parentMenu.widget));
	}

	SwtEditorMenu(SwtEditorMenuItem parentItem) {
		init(new Menu(parentItem.widget));
	}

	@Override
	public SwtEditorMenuItem getDefaultItem() {
		return getEditorWidget(widget.getDefaultItem());
	}

	@Override
	public void setDefaultItem(EditorMenuItem item) {
		SwtEditorMenuItem swtItem = (SwtEditorMenuItem) item;
		widget.setDefaultItem(swtItem == null ? null : swtItem.widget);
	}

	@Override
	public boolean getEnabled() {
		return widget.getEnabled();
	}

	@Override
	public SwtEditorMenuItem getItem(int index) {
		MenuItem item = widget.getItem(index);
		return getEditorWidget(item);
	}

	@Override
	public int getItemCount() {
		return widget.getItemCount();
	}

	@Override
	public EditorMenuItem[] getItems() {
		return Arrays.stream(widget.getItems()).sequential().map(i -> getEditorWidget(i))
				.toArray(i -> new EditorMenuItem[i]);
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
		return getEditorWidget(widget.getParentItem());
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
		SwtEditorMenuItem swtItem = (SwtEditorMenuItem) item;
		return widget.indexOf(swtItem.widget);
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
		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void setVisible(boolean visible) {
		widget.setVisible(visible);
	}

	@Override
	Menu createWidget(Composite parent, int style) {
		return null;
	}

	@Override
	public SwtEditorMenuItem createItem(MenuItemType type) {
		return new SwtEditorMenuItem(this, getMenuItemStyle(type));
	}

	@Override
	public SwtEditorMenuItem createItem(int index, MenuItemType type) {
		return new SwtEditorMenuItem(this, getMenuItemStyle(type), index);
	}

	@Override
	public SwtEditorMenu createSubMenu() {
		return new SwtEditorMenu(this);
	}

	public static int getMenuItemStyle(MenuItemType type) {
		switch (type) {
		case CHECK:
			return SWT.CHECK;
		case CASCADE:
			return SWT.CASCADE;
		case PUSH:
			return SWT.PUSH;
		case RADIO:
			return SWT.RADIO;
		case SEPARATOR:
			return SWT.SEPARATOR;
		default:
			throw new IllegalArgumentException();
		}
	}
}
