package com.gurella.engine.editor;

public interface EditorLabel extends EditorControl {
	public String getText();

	public void setText(String string);
	
	public Alignment getAlignment();
	
	public void setAlignment(Alignment alignment);
}
