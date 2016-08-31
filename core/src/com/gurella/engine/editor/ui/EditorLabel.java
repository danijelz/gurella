package com.gurella.engine.editor.ui;

import java.io.InputStream;

public interface EditorLabel extends EditorControl {
	public String getText();

	public void setText(String string);

	public Alignment getAlignment();

	public void setAlignment(Alignment alignment);

	EditorImage getImage();

	void setImage(InputStream imageStream);

	void setImage(EditorImage image);

	public static class BaseLabelStyle<T extends BaseLabelStyle<T>> extends ControlStyle<T> {
		public boolean wrap;
		public Alignment alignment;
		public ShadowType shadowType;

		public T wrap(boolean wrap) {
			this.wrap = wrap;
			return cast();
		}

		public T alignment(Alignment alignment) {
			this.alignment = alignment;
			return cast();
		}

		public T shadowType(ShadowType shadowType) {
			this.shadowType = shadowType;
			return cast();
		}
	}

	public enum ShadowType {
		SHADOW_IN, SHADOW_OUT;
	}

	public static class LabelStyle extends BaseLabelStyle<LabelStyle> {
	}

	public static class SeparatorStyle extends BaseLabelStyle<SeparatorStyle> {
		public boolean vertical;

		public SeparatorStyle vertical(boolean vertical) {
			this.vertical = vertical;
			return this;
		}
	}
}
