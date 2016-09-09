package com.gurella.studio.editor.model.extension;

import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.editor.ui.EditorFont;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorTableItem;
import com.gurella.engine.utils.GridRectangle;
import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorTableItem<ELEMENT> extends SwtEditorItem<TableItem, Table> implements EditorTableItem {
	SwtEditorTableItem(TableItem item) {
		init(item);
	}

	SwtEditorTableItem(SwtEditorTable<ELEMENT> parent) {
		super(parent, 0);
	}

	SwtEditorTableItem(SwtEditorTable<ELEMENT> parent, int index) {
		init(new TableItem(parent.widget, 0, index));
	}

	@Override
	public Color getBackground() {
		return SwtEditorUi.toGdxColor(widget.getBackground());
	}

	@Override
	public Color getBackground(int index) {
		return SwtEditorUi.toGdxColor(widget.getBackground(index));
	}

	@Override
	public GridRectangle getBounds() {
		Rectangle bounds = widget.getBounds();
		return new GridRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	@Override
	public GridRectangle getBounds(int index) {
		Rectangle bounds = widget.getBounds(index);
		return new GridRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	@Override
	public boolean getChecked() {
		return widget.getChecked();
	}

	@Override
	public EditorFont getFont() {
		Font font = widget.getFont();
		return font == null ? null : new SwtEditorFont(font);
	}

	@Override
	public EditorFont getFont(int index) {
		Font font = widget.getFont(index);
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
	public void setFont(int index, EditorFont font) {
		widget.setFont(index, font == null ? null : ((SwtEditorFont) font).font);
	}

	@Override
	public void setFont(int index, String name, int height, boolean bold, boolean italic) {
		Font font = SwtEditorUi.instance.createSwtFont(name, height, bold, italic);
		if (font != null) {
			widget.addDisposeListener(e -> font.dispose());
		}
		widget.setFont(index, font);
	}

	@Override
	public void setFont(int index, int height, boolean bold, boolean italic) {
		Font font = SwtEditorUi.instance.createSwtFont(widget.getFont(), height, bold, italic);
		if (font != null) {
			widget.addDisposeListener(e -> font.dispose());
		}
		widget.setFont(index, font);
	}

	@Override
	public Color getForeground() {
		return SwtEditorUi.toGdxColor(widget.getForeground());
	}

	@Override
	public Color getForeground(int index) {
		return SwtEditorUi.toGdxColor(widget.getForeground(index));
	}

	@Override
	public boolean getGrayed() {
		return widget.getGrayed();
	}

	@Override
	public EditorImage getImage(int index) {
		Image image = widget.getImage(index);
		return image == null ? null : new SwtEditorImage(image);
	}

	@Override
	public GridRectangle getImageBounds(int index) {
		Rectangle bounds = widget.getImageBounds(index);
		return new GridRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	@Override
	public SwtEditorTable<ELEMENT> getParent() {
		return getEditorWidget(widget.getParent());
	}

	@Override
	public String getText(int index) {
		return widget.getText();
	}

	@Override
	public GridRectangle getTextBounds(int index) {
		Rectangle bounds = widget.getTextBounds(index);
		return new GridRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
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
	public void setBackground(int index, Color color) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(color);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setBackground(index, swtColor);
	}

	@Override
	public void setBackground(int index, int r, int g, int b, int a) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(r, g, b, a);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setBackground(index, swtColor);
	}

	@Override
	public void setChecked(boolean checked) {
		widget.setChecked(checked);
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
	public void setForeground(int index, Color color) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(color);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setForeground(index, swtColor);
	}

	@Override
	public void setForeground(int index, int r, int g, int b, int a) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(r, g, b, a);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setForeground(index, swtColor);
	}

	@Override
	public void setGrayed(boolean grayed) {
		widget.setGrayed(grayed);
	}

	@Override
	public void setImage(EditorImage[] images) {
		widget.setImage(
				Arrays.stream(images).sequential().map(i -> ((SwtEditorImage) i).image).toArray(i -> new Image[i]));
	}

	@Override
	public void setImage(int index, EditorImage image) {
		widget.setImage(index, image == null ? null : ((SwtEditorImage) image).image);
	}

	@Override
	public void setImage(int index, InputStream imageStream) {
		if (imageStream == null) {
			widget.setImage(index, null);
		} else {
			Image image = new Image(widget.getDisplay(), imageStream);
			widget.addListener(SWT.Dispose, e -> image.dispose());
			widget.setImage(index, image);
		}
	}

	@Override
	public void setText(int index, String string) {
		widget.setText(index, string);
	}

	@Override
	public void setText(String[] strings) {
		widget.setText(strings);
	}

	@Override
	TableItem createItem(Table parent, int style) {
		return new TableItem(parent, style);
	}
}
