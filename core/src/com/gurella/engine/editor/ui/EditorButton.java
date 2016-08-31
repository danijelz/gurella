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

	public static class BaseButtonStyle<T extends BaseButtonStyle<T>> extends ControlStyle<T> {
		public boolean flat;
		public boolean wrap;
		public Alignment alignment;

		public T flat(boolean flat) {
			this.flat = flat;
			return cast();
		}

		public T wrap(boolean wrap) {
			this.wrap = wrap;
			return cast();
		}

		public T alignment(Alignment alignment) {
			this.alignment = alignment;
			return cast();
		}
	}

	public static class ButtonStyle extends BaseButtonStyle<ButtonStyle> {
	}

	public static class ToggleButtonStyle extends BaseButtonStyle<ToggleButtonStyle> {
	}

	public static class RadioButtonStyle extends BaseButtonStyle<RadioButtonStyle> {
	}

	public static class CheckBoxStyle extends BaseButtonStyle<CheckBoxStyle> {
	}

	public static class ArrowButtonStyle extends BaseButtonStyle<ArrowButtonStyle> {
		public ArrowDirection direction;

		public ArrowButtonStyle direction(ArrowDirection direction) {
			this.direction = direction;
			return this;
		}
	}

	public enum ArrowDirection {
		UP, DOWN, LEFT, RIGHT;
	}
}
