package com.gurella.engine.editor.ui;

import java.io.InputStream;

import com.badlogic.gdx.graphics.Color;

public interface EditorSection extends EditorExpandableComposite {
	String getDescription();

	EditorControl getDescriptionControl();

	EditorControl getSeparatorControl();

	Color getTitleBarBackground();

	Color getTitleBarBorderColor();

	Color getTitleBarGradientBackground();

	void setBackgroundImage(EditorImage image);

	void setBackgroundImage(InputStream imageStream);

	void setDescription(String description);

	void setDescriptionControl(EditorControl descriptionControl);

	void setSeparatorControl(EditorControl separator);

	void setTitleBarBackground(Color color);

	void setTitleBarBackground(int r, int g, int b, int a);

	void setTitleBarBorderColor(Color color);

	void setTitleBarBorderColor(int r, int g, int b, int a);

	void setTitleBarGradientBackground(Color color);

	void setTitleBarGradientBackground(int r, int g, int b, int a);

	public static class SectionStyle extends BaseExpandableCompositeStyle<SectionStyle> {
		public boolean description;

		public SectionStyle description(boolean description) {
			this.description = description;
			return cast();
		}
	}
}
