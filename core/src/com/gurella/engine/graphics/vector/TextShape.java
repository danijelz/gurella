package com.gurella.engine.graphics.vector;

import com.gurella.engine.graphics.vector.Canvas.TextLayout;

//TODO remove
public class TextShape extends Shape {
	private String text;
	private TextLayout textLayout;
	
	public TextShape(String text, TextLayout textLayout) {
		this.text = text;
		this.textLayout = textLayout;
	}

	public void setText(String text) {
		if (this.text != text && (this.text == null || !this.text.equals(text))) {
			this.text = text;
			markDataChanged();
		}
	}
	
	public void setTextLayout(TextLayout textLayout) {
		if (this.textLayout != textLayout && (this.textLayout == null || !this.textLayout.equals(textLayout))) {
			this.textLayout = textLayout;
			markDataChanged();
		}
	}

	@Override
	protected void initPath(Path path) {
		if(text != null && textLayout != null) {
			textLayout.appendPath(text, path);
		}
	}
}
