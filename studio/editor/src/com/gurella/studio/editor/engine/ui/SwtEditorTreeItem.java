package com.gurella.studio.editor.engine.ui;

import static com.gurella.engine.utils.Values.cast;

import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TreeItem;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.editor.ui.EditorFont;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorTreeItem;
import com.gurella.engine.math.GridRectangle;

public class SwtEditorTreeItem<ELEMENT> extends SwtEditorItem<TreeItem> implements EditorTreeItem {
	SwtEditorTreeItem(TreeItem item) {
		super(item);
	}

	SwtEditorTreeItem(SwtEditorTree<ELEMENT> parent) {
		super(new TreeItem(parent.widget, 0));
	}

	SwtEditorTreeItem(SwtEditorTree<ELEMENT> parent, int index) {
		super(new TreeItem(parent.widget, 0, index));
	}

	SwtEditorTreeItem(SwtEditorTreeItem<ELEMENT> parent) {
		super(new TreeItem(parent.widget, 0));
	}

	SwtEditorTreeItem(SwtEditorTreeItem<ELEMENT> parent, int index) {
		super(new TreeItem(parent.widget, 0, index));
	}

	@Override
	public Color getBackground() {
		return toGdxColor(widget.getBackground());
	}

	@Override
	public Color getBackground(int index) {
		return toGdxColor(widget.getBackground(index));
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
		return toEditorFont(widget.getFont());
	}

	@Override
	public EditorFont getFont(int index) {
		return toEditorFont(widget.getFont(index));
	}

	@Override
	public void setFont(EditorFont font) {
		widget.setFont(toSwtFont(font));
	}

	@Override
	public void setFont(String name, int height, boolean bold, boolean italic) {
		widget.setFont(toSwtFont(name, height, bold, italic));
	}

	@Override
	public void setFont(int height, boolean bold, boolean italic) {
		widget.setFont(toSwtFont(widget.getFont(), height, bold, italic));
	}

	@Override
	public void setFont(int index, EditorFont font) {
		widget.setFont(index, toSwtFont(font));
	}

	@Override
	public void setFont(int index, String name, int height, boolean bold, boolean italic) {
		widget.setFont(index, toSwtFont(name, height, bold, italic));
	}

	@Override
	public void setFont(int index, int height, boolean bold, boolean italic) {
		widget.setFont(index, toSwtFont(widget.getFont(), height, bold, italic));
	}

	@Override
	public Color getForeground() {
		return toGdxColor(widget.getForeground());
	}

	@Override
	public Color getForeground(int index) {
		return toGdxColor(widget.getForeground(index));
	}

	@Override
	public boolean getGrayed() {
		return widget.getGrayed();
	}

	@Override
	public EditorImage getImage(int index) {
		return toEditorImage(widget.getImage(index));
	}

	@Override
	public GridRectangle getImageBounds(int index) {
		Rectangle bounds = widget.getImageBounds(index);
		return new GridRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	@Override
	public SwtEditorTree<ELEMENT> getParent() {
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
		widget.setBackground(toSwtColor(color));
	}

	@Override
	public void setBackground(int r, int g, int b, int a) {
		widget.setBackground(toSwtColor(r, g, b, a));
	}

	@Override
	public void setBackground(int index, Color color) {
		widget.setBackground(index, toSwtColor(color));
	}

	@Override
	public void setBackground(int index, int r, int g, int b, int a) {
		widget.setBackground(index, toSwtColor(r, g, b, a));
	}

	@Override
	public void setChecked(boolean checked) {
		widget.setChecked(checked);
	}

	@Override
	public void setForeground(Color color) {
		widget.setForeground(toSwtColor(color));
	}

	@Override
	public void setForeground(int r, int g, int b, int a) {
		widget.setForeground(toSwtColor(r, g, b, a));
	}

	@Override
	public void setForeground(int index, Color color) {
		widget.setForeground(index, toSwtColor(color));
	}

	@Override
	public void setForeground(int index, int r, int g, int b, int a) {
		widget.setForeground(index, toSwtColor(r, g, b, a));
	}

	@Override
	public void setGrayed(boolean grayed) {
		widget.setGrayed(grayed);
	}

	@Override
	public void setImage(EditorImage[] images) {
		widget.setImage(Arrays.stream(images).map(i -> toSwtImage(i)).toArray(i -> new Image[i]));
	}

	@Override
	public void setImage(int index, EditorImage image) {
		widget.setImage(index, toSwtImage(image));
	}

	@Override
	public void setImage(int index, InputStream imageStream) {
		widget.setImage(index, toSwtImage(imageStream));
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
	public SwtEditorTreeItem<ELEMENT> getItem(int index) {
		return getEditorWidget(widget.getItem(index));
	}

	@Override
	public int getItemCount() {
		return widget.getItemCount();
	}

	@Override
	public SwtEditorTreeItem<ELEMENT>[] getItems() {
		return cast(
				Arrays.stream(widget.getItems()).map(i -> getEditorWidget(i)).toArray(i -> new SwtEditorTreeItem[i]));
	}

	@Override
	public SwtEditorTreeItem<ELEMENT> getParentItem() {
		return getEditorWidget(widget.getParentItem());
	}

	@Override
	public int indexOf(EditorTreeItem item) {
		return widget.indexOf(((SwtEditorTreeItem<?>) item).widget);
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

	/*
	 * @Override TreeItem createItem(Tree parent, int style) { return null; }
	 */

	@Override
	public SwtEditorTreeItem<ELEMENT> createItem() {
		return new SwtEditorTreeItem<ELEMENT>(this);
	}

	@Override
	public SwtEditorTreeItem<ELEMENT> createItem(int index) {
		return new SwtEditorTreeItem<ELEMENT>(this, index);
	}
}
