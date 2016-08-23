package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.editor.ui.Direction;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorMenu;
import com.gurella.engine.editor.ui.FontData;
import com.gurella.engine.utils.GridRectangle;
import com.gurella.engine.utils.Values;

public abstract class SwtEditorControl<T extends Control> extends SwtEditorWidget<T> implements EditorControl {
	SwtEditorControl() {
	}

	public SwtEditorControl(SwtEditorComposite parent, FormToolkit toolkit) {
		super(parent, toolkit);
	}

	@Override
	public SwtEditorComposite getParent() {
		Composite parent = widget.getParent();
		return getEditorWidget(parent);
	}

	public static <T extends EditorControl> T getEditorControl(Control control) {
		return Values.cast(instances.get(control));
	}

	@Override
	public int getBorderWidth() {
		return widget.getBorderWidth();
	}

	@Override
	public boolean setFocus() {
		return widget.setFocus();
	}

	@Override
	public boolean forceFocus() {
		return widget.forceFocus();
	}

	@Override
	public void redraw() {
		widget.redraw();
	}

	@Override
	public void pack() {
		widget.pack();
	}

	@Override
	public void moveAbove(EditorControl control) {
		SwtEditorControl<?> swtControl = (SwtEditorControl<?>) control;
		this.widget.moveAbove(swtControl.widget);
	}

	@Override
	public void moveBelow(EditorControl control) {
		SwtEditorControl<?> swtControl = (SwtEditorControl<?>) control;
		this.widget.moveBelow(swtControl.widget);
	}

	@Override
	public Color getBackground() {
		org.eclipse.swt.graphics.Color background = widget.getBackground();
		return new Color(background.getRed() / 255f, background.getGreen() / 255f, background.getBlue() / 255f,
				background.getAlpha() / 255f);
	}

	@Override
	public void setBackground(Color color) {
		org.eclipse.swt.graphics.Color swtColor = new org.eclipse.swt.graphics.Color(widget.getDisplay(),
				(int) color.r * 255, (int) color.g * 255, (int) color.b * 255, (int) color.a * 255);
		widget.addListener(SWT.Dispose, e -> swtColor.dispose());
		widget.setBackground(swtColor);
	}

	@Override
	public GridRectangle getBounds() {
		Rectangle bounds = widget.getBounds();
		return new GridRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		widget.setBounds(x, y, width, height);
	}

	@Override
	public boolean getDragDetect() {
		return widget.getDragDetect();
	}

	@Override
	public void setDragDetect(boolean dragDetect) {
		widget.setDragDetect(dragDetect);
	}

	@Override
	public boolean getEnabled() {
		return widget.getEnabled();
	}

	@Override
	public boolean isEnabled() {
		return widget.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		widget.setEnabled(enabled);
	}

	@Override
	public FontData getFontData() {
		Font font = widget.getFont();
	}

	@Override
	public void setFontData(FontData fontData) {
		//TODO
	}

	@Override
	public Color getForeground() {
		org.eclipse.swt.graphics.Color background = widget.getForeground();
		return new Color(background.getRed() / 255f, background.getGreen() / 255f, background.getBlue() / 255f,
				background.getAlpha() / 255f);
	}

	@Override
	public void setForeground(Color color) {
		org.eclipse.swt.graphics.Color swtColor = new org.eclipse.swt.graphics.Color(widget.getDisplay(),
				(int) color.r * 255, (int) color.g * 255, (int) color.b * 255, (int) color.a * 255);
		widget.addListener(SWT.Dispose, e -> swtColor.dispose());
		widget.setForeground(swtColor);
	}

	@Override
	public Object getLayoutData() {
		return widget.getLayoutData();
	}

	@Override
	public void setLayoutData(Object layoutData) {
		widget.setLayoutData(layoutData);
	}

	@Override
	public GridPoint2 getLocation() {
		Point location = widget.getLocation();
		return new GridPoint2(location.x, location.y);
	}

	@Override
	public void setLocation(int x, int y) {
		widget.setLocation(x, y);
	}

	@Override
	public SwtEditorMenu getMenu() {
		Menu menu = widget.getMenu();
		return menu == null ? null : getEditorWidget(menu);
	}

	@Override
	public void setMenu(EditorMenu menu) {
		SwtEditorMenu swtMenu = (SwtEditorMenu) menu;
		widget.setMenu(swtMenu.widget);
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
	public void setOrientation(Direction direction) {
		switch (direction) {
		case leftToRight:
			widget.setOrientation(SWT.LEFT_TO_RIGHT);
			break;
		case rightToLeft:
			widget.setOrientation(SWT.RIGHT_TO_LEFT);
			break;
		}
	}

	@Override
	public GridPoint2 getSize() {
		Point size = widget.getSize();
		return new GridPoint2(size.x, size.y);
	}

	@Override
	public void setSize(int width, int height) {
		widget.setSize(width, height);
	}

	@Override
	public Direction getTextDirection() {
		int textDirection = widget.getTextDirection();
		switch (textDirection) {
		case SWT.LEFT_TO_RIGHT:
			return Direction.leftToRight;
		case SWT.RIGHT_TO_LEFT:
			return Direction.rightToLeft;
		default:
			return null;
		}
	}

	@Override
	public void setTextDirection(Direction textDirection) {
		switch (textDirection) {
		case leftToRight:
			widget.setTextDirection(SWT.LEFT_TO_RIGHT);
			break;
		case rightToLeft:
			widget.setTextDirection(SWT.RIGHT_TO_LEFT);
			break;
		}
	}

	@Override
	public String getToolTipText() {
		return widget.getToolTipText();
	}

	@Override
	public void setToolTipText(String string) {
		widget.setToolTipText(string);
	}

	@Override
	public boolean getTouchEnabled() {
		return widget.getTouchEnabled();
	}

	@Override
	public void setTouchEnabled(boolean enabled) {
		widget.setTouchEnabled(enabled);
	}

	@Override
	public boolean isVisible() {
		return widget.isVisible();
	}

	@Override
	public boolean getVisible() {
		return widget.getVisible();
	}

	@Override
	public void setVisible(boolean visible) {
		widget.setVisible(visible);
	}

	@Override
	public boolean isFocusControl() {
		return widget.isFocusControl();
	}

	@Override
	public boolean isReparentable() {
		return widget.isReparentable();
	}

	@Override
	public void setCapture(boolean capture) {
		widget.setCapture(capture);
	}

	@Override
	public boolean setParent(EditorComposite parent) {
		return widget.setParent(((SwtEditorComposite) parent).widget);
	}

	@Override
	public GridPoint2 toControl(int x, int y) {
		Point point = widget.toControl(x, y);
		return new GridPoint2(point.x, point.y);
	}

	@Override
	public GridPoint2 toDisplay(int x, int y) {
		Point point = widget.toDisplay(x, y);
		return new GridPoint2(point.x, point.y);
	}
}
