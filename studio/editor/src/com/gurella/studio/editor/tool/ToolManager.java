package com.gurella.studio.editor.tool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.gurella.engine.event.EventService;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;

public class ToolManager extends InputAdapter implements EditorPreCloseListener {
	private final int editorId;

	ScaleTool scaleTool = new ScaleTool();
	SelectionTool selected;

	public ToolManager(int editorId) {
		this.editorId = editorId;
		EventService.subscribe(editorId, this);
	}
	
	@Override
	public void onEditorPreClose() {
		EventService.unsubscribe(editorId, this);
		scaleTool.dispose();
	}
}
