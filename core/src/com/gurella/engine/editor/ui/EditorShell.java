package com.gurella.engine.editor.ui;

import java.io.InputStream;

import com.badlogic.gdx.math.GridPoint2;

public interface EditorShell extends EditorComposite {
	EditorButton getDefaultButton();

	EditorImage getImage();

	EditorImage[] getImages();

	boolean getMaximized();

	EditorMenu getMenuBar();

	boolean getMinimized();

	String getText();

	void setDefaultButton(EditorButton button);

	void setImage(EditorImage image);

	void setImage(InputStream imageStream);

	void setImages(EditorImage[] images);

	void setMaximized(boolean maximized);

	void setMenuBar(EditorMenu menu);

	void setMinimized(boolean minimized);

	void setText(String string);

	void close();

	void forceActive();

	int getAlpha();

	boolean getFullScreen();

	GridPoint2 getMinimumSize();

	boolean getModified();

	EditorToolBar getToolBar();

	void open();

	void setActive();

	void setAlpha(int alpha);

	void setFullScreen(boolean fullScreen);

	void setMinimumSize(int width, int height);

	void setModified(boolean modified);

	public static class ShellStyle extends ScrollableStyle<ShellStyle> {
		public boolean close;
		public boolean min;
		public boolean max;
		public boolean noTrim;
		public boolean resize;
		public boolean title;
		public boolean onTop;
		public boolean tool;
		public boolean sheet;
		public Modality modality;

		public ShellStyle close(boolean close) {
			this.close = close;
			return this;
		}

		public ShellStyle min(boolean min) {
			this.min = min;
			return this;
		}

		public ShellStyle max(boolean max) {
			this.max = max;
			return this;
		}

		public ShellStyle noTrim(boolean noTrim) {
			this.noTrim = noTrim;
			return this;
		}

		public ShellStyle resize(boolean resize) {
			this.resize = resize;
			return this;
		}

		public ShellStyle title(boolean title) {
			this.title = title;
			return this;
		}

		public ShellStyle onTop(boolean onTop) {
			this.onTop = onTop;
			return this;
		}

		public ShellStyle tool(boolean tool) {
			this.tool = tool;
			return this;
		}

		public ShellStyle sheet(boolean sheet) {
			this.sheet = sheet;
			return this;
		}

		public ShellStyle modality(Modality modality) {
			this.modality = modality;
			return this;
		}
	}

	public enum Modality {
		APPLICATION_MODAL, MODELESS, PRIMARY_MODAL, SYSTEM_MODAL;
	}
}
