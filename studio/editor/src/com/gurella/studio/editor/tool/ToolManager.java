package com.gurella.studio.editor.tool;

import com.badlogic.gdx.InputAdapter;

public class ToolManager extends InputAdapter {
	private final int editorId;
	
	ScaleTool scaleTool;

	public ToolManager(int editorId) {
		this.editorId = editorId;
	}
	
}
