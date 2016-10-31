package com.gurella.studio.editor.tool;

import com.badlogic.gdx.InputAdapter;
import com.gurella.engine.event.EventService;
import com.gurella.studio.editor.subscription.EditorFocusListener;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;

public class ToolManager extends InputAdapter implements EditorPreCloseListener, EditorFocusListener {
	private final int editorId;
	
	private EditorFocusData focusData = new EditorFocusData(null, null);

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

	@Override
	public void focusChanged(EditorFocusData focusData) {
		// TODO Auto-generated method stub
	}
}
