package com.gurella.studio.editor.model.extension;

import static com.gurella.engine.utils.Values.cast;
import static com.gurella.studio.editor.model.extension.style.SwtWidgetStyle.getSwtStyle;

import java.io.InputStream;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.editor.ui.Alignment;
import com.gurella.engine.editor.ui.EditorButton;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorFont;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorLabel;
import com.gurella.engine.editor.ui.EditorLogLevel;
import com.gurella.engine.editor.ui.EditorMenu;
import com.gurella.engine.editor.ui.EditorMenuItem;
import com.gurella.engine.editor.ui.EditorMenuItem.MenuItemType;
import com.gurella.engine.editor.ui.EditorToolItem.ToolItemType;
import com.gurella.engine.editor.ui.EditorUi;
import com.gurella.engine.editor.ui.style.WidgetStyle;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;

//TODO import methods from UiUtils
public class SwtEditorUi implements EditorUi {
	public static final SwtEditorUi instance = new SwtEditorUi();

	private SwtEditorUi() {
	}

	@Override
	public void log(EditorLogLevel level, String message) {
		GurellaStudioPlugin.log(level, message);
	}

	@Override
	public void logError(Throwable t, String message) {
		GurellaStudioPlugin.log(t, message);
	}

	@Override
	public EditorImage createImage(InputStream imageStream) {
		return new SwtEditorImage(new Image(getDisplay(), imageStream));
	}

	@Override
	public SwtEditorFont createFont(String name, int height, boolean bold, boolean italic) {
		Font font = createSwtFont(name, height, bold, italic);
		return font == null ? null : new SwtEditorFont(font);
	}

	public Font createSwtFont(String name, int height, boolean bold, boolean italic) {
		return FontDescriptor.createFrom(name, height, getFontStyle(bold, italic)).createFont(getDisplay());
	}

	protected static int getFontStyle(boolean bold, boolean italic) {
		int style = bold ? SWT.BOLD : 0;
		style |= italic ? SWT.ITALIC : SWT.NORMAL;
		return style;
	}

	@Override
	public SwtEditorFont createFont(EditorFont initial, int height, boolean bold, boolean italic) {
		Font oldFont = ((SwtEditorFont) initial).font;
		Font font = createSwtFont(oldFont, height, bold, italic);
		return font == null ? null : new SwtEditorFont(font);
	}

	protected Font createSwtFont(Font oldFont, int height, boolean bold, boolean italic) {
		if (oldFont == null) {
			return null;
		}

		int style = getFontStyle(bold, italic);
		Font font = FontDescriptor.createFrom(oldFont).setHeight(height).setStyle(style).createFont(getDisplay());
		return font;
	}

	@Override
	public SwtEditorFont createFont(EditorControl control, int height, boolean bold, boolean italic) {
		Font oldFont = ((SwtEditorControl<?>) control).widget.getFont();
		Font font = createSwtFont(oldFont, height, bold, italic);
		return font == null ? null : new SwtEditorFont(font);
	}

	public static Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

	public static Alignment alignment(int alignment) {
		switch (alignment) {
		case SWT.LEFT:
			return Alignment.LEFT;
		case SWT.CENTER:
			return Alignment.CENTER;
		case SWT.RIGHT:
			return Alignment.RIGHT;
		default:
			return null;
		}
	}

	public static int alignment(Alignment alignment) {
		switch (alignment) {
		case LEFT:
			return SWT.LEFT;
		case CENTER:
			return SWT.CENTER;
		case RIGHT:
			return SWT.RIGHT;
		default:
			throw new IllegalArgumentException();
		}
	}

	public static Color toGdxColor(org.eclipse.swt.graphics.Color color) {
		return new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f,
				color.getAlpha() / 255f);
	}

	public EditorComposite createComposite(Composite parent) {
		return new SwtEditorComposite(parent);
	}

	@Override
	public EditorComposite createComposite(EditorComposite parent, WidgetStyle<? super EditorComposite>... styles) {
		return new SwtEditorComposite(Values.<SwtEditorComposite> cast(parent), getSwtStyle(styles));
	}

	@Override
	public EditorLabel createLabel(EditorComposite parent, WidgetStyle<? super EditorLabel>... styles) {
		return new SwtEditorLabel(cast(parent), getSwtStyle(styles));
	}

	@Override
	public EditorLabel createLabel(EditorComposite parent, String text, WidgetStyle<? super EditorLabel>... styles) {
		return new SwtEditorLabel(cast(parent), text, getSwtStyle(styles));
	}

	@Override
	public EditorLabel createSeparatorLabel(EditorComposite parent, WidgetStyle<? super EditorLabel>... styles) {
		return new SwtEditorLabel(cast(parent), getSwtStyle(SWT.SEPARATOR, styles));
	}

	@Override
	public EditorButton createCheckBox(EditorComposite parent, WidgetStyle<? super EditorButton>... styles) {
		return new SwtEditorButton(cast(parent), getSwtStyle(SWT.CHECK, styles));
	}

	@Override
	public EditorButton createCheckBox(EditorComposite parent, String text,
			WidgetStyle<? super EditorButton>... styles) {
		return new SwtEditorButton(cast(parent), text, getSwtStyle(SWT.CHECK, styles));
	}

	@Override
	public EditorMenu createMenu(EditorControl parent) {
		return new SwtEditorMenu((SwtEditorControl<?>) parent);
	}

	@Override
	public EditorMenu createMenu(EditorMenu parentMenu) {
		return new SwtEditorMenu((SwtEditorMenu) parentMenu);
	}

	@Override
	public EditorMenu createMenu(EditorMenuItem parentItem) {
		return new SwtEditorMenu((SwtEditorMenuItem) parentItem);
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

	public static int getToolItemStyle(ToolItemType type) {
		switch (type) {
		case CHECK:
			return SWT.CHECK;
		case DROP_DOWN:
			return SWT.DROP_DOWN;
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
