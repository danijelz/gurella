package com.gurella.studio.editor.engine.ui;

import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.editor.ui.EditorButton;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorMenu;
import com.gurella.engine.editor.ui.EditorShell;

public class SwtEditorShell extends SwtEditorLayoutComposite<Shell> implements EditorShell {
	public SwtEditorShell(Shell parent, int style) {
		super(new Shell(parent, style));
	}

	@Override
	public SwtEditorButton getDefaultButton() {
		return getEditorWidget(widget.getDefaultButton());
	}

	@Override
	public SwtEditorImage getImage() {
		return toEditorImage(widget.getImage());
	}

	@Override
	public SwtEditorImage[] getImages() {
		return Arrays.stream(widget.getImages()).map(i -> toEditorImage(i)).toArray(i -> new SwtEditorImage[i]);
	}

	@Override
	public boolean getMaximized() {
		return widget.getMaximized();
	}

	@Override
	public SwtEditorMenu getMenuBar() {
		return getEditorWidget(widget.getMenu());
	}

	@Override
	public boolean getMinimized() {
		return widget.getMinimized();
	}

	@Override
	public String getText() {
		return widget.getText();
	}

	@Override
	public void setDefaultButton(EditorButton button) {
		widget.setDefaultButton(button == null ? null : ((SwtEditorButton) button).widget);
	}

	@Override
	public void setImage(InputStream imageStream) {
		if (imageStream == null) {
			widget.setImage(null);
		} else {
			Image image = new Image(widget.getDisplay(), imageStream);
			widget.addListener(SWT.Dispose, e -> image.dispose());
			widget.setImage(image);
		}
	}

	@Override
	public void setImage(EditorImage image) {
		widget.setImage(toSwtImage(image));
	}

	@Override
	public void setImages(EditorImage[] images) {
		widget.setImages(Arrays.stream(images).map(i -> toSwtImage(i)).toArray(i -> new Image[i]));
	}

	@Override
	public void setMaximized(boolean maximized) {
		widget.setMaximized(maximized);
	}

	@Override
	public void setMenuBar(EditorMenu menu) {
		widget.setMenuBar(menu == null ? null : ((SwtEditorMenu) menu).widget);
	}

	@Override
	public void setMinimized(boolean minimized) {
		widget.setMinimized(minimized);
	}

	@Override
	public void setText(String string) {
		widget.setText(string);
	}

	@Override
	public void close() {
		widget.close();
	}

	@Override
	public void forceActive() {
		widget.forceActive();
	}

	@Override
	public int getAlpha() {
		return widget.getAlpha();
	}

	@Override
	public boolean getFullScreen() {
		return widget.getFullScreen();
	}

	@Override
	public GridPoint2 getMinimumSize() {
		Point point = widget.getMinimumSize();
		return new GridPoint2(point.x, point.y);
	}

	@Override
	public boolean getModified() {
		return widget.getModified();
	}

	@Override
	public SwtEditorToolBar getToolBar() {
		return getEditorWidget(widget.getToolBar());
	}

	@Override
	public void open() {
		widget.open();
	}

	@Override
	public void setActive() {
		widget.setActive();
	}

	@Override
	public void setAlpha(int alpha) {
		widget.setAlpha(alpha);
	}

	@Override
	public void setFullScreen(boolean fullScreen) {
		widget.setFullScreen(fullScreen);
	}

	@Override
	public void setMinimumSize(int width, int height) {
		widget.setMinimumSize(width, height);
	}

	@Override
	public void setModified(boolean modified) {
		widget.setModified(modified);
	}
}
