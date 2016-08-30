package com.gurella.engine.editor.ui;

import java.io.InputStream;

public interface EditorButton extends EditorControl {
	Alignment getAlignment();

	void setAlignment(Alignment alignment);

	boolean getGrayed();

	void setGrayed(boolean grayed);

	EditorImage getImage();

	void setImage(InputStream imageStream);

	boolean getSelection();

	void setSelection(boolean selected);

	String getText();

	void setText(String string);

	void setImage(EditorImage image);

	public static class ButtonStyle extends ControlStyle {
		public boolean flat;
		public boolean wrap;
		public Alignment textAlignment;
	}

	public static class ArrowStyle extends ButtonStyle {
		public ArrowDirection direction;
	}

	public enum ArrowDirection {
		UP, DOWN, LEFT, RIGHT;
	}
}
