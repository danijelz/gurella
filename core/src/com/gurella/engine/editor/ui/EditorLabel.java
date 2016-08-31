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

	public static class LabelStyle extends ControlStyle {
		public boolean wrap;
		public Alignment alignment;
		public ShadowType shadowType;
	}

	public enum ShadowType {
		SHADOW_IN, SHADOW_OUT;
	}

	public static class SeparatorStyle extends LabelStyle {
		public boolean vertical;
	}
}
