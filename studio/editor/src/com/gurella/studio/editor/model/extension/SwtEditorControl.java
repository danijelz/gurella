package com.gurella.studio.editor.model.extension;

import static com.gurella.studio.editor.model.extension.SwtEditorUi.transformLayoutData;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.editor.ui.Direction;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorFont;
import com.gurella.engine.editor.ui.EditorMenu;
import com.gurella.engine.editor.ui.layout.EditorLayoutData;
import com.gurella.engine.utils.GridRectangle;
import com.gurella.studio.GurellaStudioPlugin;

public abstract class SwtEditorControl<T extends Control> extends SwtEditorWidget<T> implements EditorControl {
	SwtEditorControl() {
	}

	public SwtEditorControl(SwtEditorBaseComposite<?> parent, int style) {
		super(parent, style);
	}

	@Override
	void init(T widget) {
		GurellaStudioPlugin.getToolkit().adapt(widget, true, true);
		super.init(widget);
	}

	@Override
	public SwtEditorBaseComposite<?> getParent() {
		Composite parent = widget.getParent();
		return getEditorWidget(parent);
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
		widget.pack(true);
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
		return SwtEditorUi.toGdxColor(widget.getBackground());
	}

	@Override
	public void setBackground(Color color) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(color);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setBackground(swtColor);
	}

	@Override
	public void setBackground(int r, int g, int b, int a) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(r, g, b, a);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
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
	public EditorFont getFont() {
		Font font = widget.getFont();
		return font == null ? null : new SwtEditorFont(font);
	}

	@Override
	public void setFont(EditorFont font) {
		widget.setFont(font == null ? null : ((SwtEditorFont) font).font);
	}

	@Override
	public void setFont(String name, int height, boolean bold, boolean italic) {
		Font font = SwtEditorUi.instance.createSwtFont(name, height, bold, italic);
		if (font != null) {
			widget.addDisposeListener(e -> font.dispose());
		}
		widget.setFont(font);
	}

	@Override
	public void setFont(int height, boolean bold, boolean italic) {
		Font font = SwtEditorUi.instance.createSwtFont(widget.getFont(), height, bold, italic);
		if (font != null) {
			widget.addDisposeListener(e -> font.dispose());
		}
		widget.setFont(font);
	}

	@Override
	public Color getForeground() {
		return SwtEditorUi.toGdxColor(widget.getForeground());
	}

	@Override
	public void setForeground(Color color) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(color);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setForeground(swtColor);
	}

	@Override
	public void setForeground(int r, int g, int b, int a) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(r, g, b, a);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setForeground(swtColor);
	}

	@Override
	public EditorLayoutData getLayoutData() {
		Object layoutData = widget.getLayoutData();
		return layoutData instanceof GridData ? transformLayoutData((GridData) layoutData) : null;
	}

	@Override
	public EditorLayoutData getOrCreateLayoutData() {
		Object data = widget.getLayoutData();
		return data instanceof GridData ? transformLayoutData((GridData) data) : new EditorLayoutData();
	}

	@Override
	public EditorLayoutData getOrCreateDefaultLayoutData() {
		Object data = widget.getLayoutData();
		GridData gridData = data instanceof GridData ? (GridData) data : GridDataFactory.defaultsFor(widget).create();
		return transformLayoutData(gridData);
	}

	@Override
	public void setLayoutData(EditorLayoutData layoutData) {
		widget.setLayoutData(layoutData == null ? null : transformLayoutData(layoutData));
	}

	@Override
	public void setLayoutData(int horizontalSpan, int verticalSpan) {
		Object data = widget.getLayoutData();
		if (data instanceof GridData) {
			GridDataFactory.createFrom((GridData) data).span(horizontalSpan, verticalSpan).applyTo(widget);
		} else {
			GridDataFactory.generate(widget, horizontalSpan, verticalSpan);
		}
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
		default:
			throw new IllegalArgumentException();
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
		default:
			throw new IllegalArgumentException();
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
		return widget.setParent(((SwtEditorBaseComposite<?>) parent).widget);
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
