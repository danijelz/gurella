package com.gurella.studio.editor.model.extension;

import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorTableItem;
import com.gurella.engine.editor.ui.FontData;
import com.gurella.engine.utils.GridRectangle;
import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorTableItem extends SwtEditorItem<TableItem> implements EditorTableItem {
	SwtEditorTableItem(SwtEditorTable parent, int style) {
		super(parent, style);
	}

	@Override
	public Color getBackground() {
		org.eclipse.swt.graphics.Color color = widget.getBackground();
		return new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f,
				color.getAlpha() / 255f);
	}

	@Override
	public Color getBackground(int index) {
		org.eclipse.swt.graphics.Color color = widget.getBackground(index);
		return new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f,
				color.getAlpha() / 255f);
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
	public FontData getFont() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FontData getFont(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getForeground() {
		org.eclipse.swt.graphics.Color color = widget.getForeground();
		return new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f,
				color.getAlpha() / 255f);
	}

	@Override
	public Color getForeground(int index) {
		org.eclipse.swt.graphics.Color color = widget.getForeground(index);
		return new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f,
				color.getAlpha() / 255f);
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
	public SwtEditorTable getParent() {
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
	public void setBackground(int index, Color color) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(color);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setBackground(index, swtColor);
	}

	@Override
	public void setChecked(boolean checked) {
		widget.setChecked(checked);
	}

	@Override
	public void setFont(FontData font) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFont(int index, FontData font) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setForeground(Color color) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(color);
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
	public void setGrayed(boolean grayed) {
		widget.setGrayed(grayed);
	}

	@Override
	public void setImage(EditorImage[] images) {
		widget.setImage(Arrays.stream(images).map(i -> ((SwtEditorImage) i).image).toArray(i -> new Image[i]));
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
	TableItem createItem(SwtEditorWidget<?> parent, int style) {
		return new TableItem((Table) parent.widget, style);
	}
}
