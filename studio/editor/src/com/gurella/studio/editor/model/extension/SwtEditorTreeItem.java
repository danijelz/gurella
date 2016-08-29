package com.gurella.studio.editor.model.extension;

import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorTreeItem;
import com.gurella.engine.editor.ui.FontData;
import com.gurella.engine.utils.GridRectangle;
import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorTreeItem extends SwtEditorItem<TreeItem, Tree> implements EditorTreeItem {
	SwtEditorTreeItem(SwtEditorTree parent, int style) {
		super(parent, style);
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
	public SwtEditorTree getParent() {
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
	public void clear(int index, boolean all) {
		widget.clear(index, all);
	}

	@Override
	public void clearAll(boolean all) {
		widget.clearAll(all);
	}

	@Override
	public boolean getExpanded() {
		return widget.getExpanded();
	}

	@Override
	public SwtEditorTreeItem getItem(int index) {
		return getEditorWidget(widget.getItem(index));
	}

	@Override
	public int getItemCount() {
		return widget.getItemCount();
	}

	@Override
	public SwtEditorTreeItem[] getItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SwtEditorTreeItem getParentItem() {
		return getEditorWidget(widget.getParentItem());
	}

	@Override
	public int indexOf(EditorTreeItem item) {
		return widget.indexOf(((SwtEditorTreeItem) item).widget);
	}

	@Override
	public void removeAll() {
		widget.removeAll();
	}

	@Override
	public void setExpanded(boolean expanded) {
		widget.setExpanded(expanded);
	}

	@Override
	public void setItemCount(int count) {
		widget.setItemCount(count);
	}

	@Override
	TreeItem createItem(Tree parent, int style) {
		return new TreeItem(parent, style);
	}
}
