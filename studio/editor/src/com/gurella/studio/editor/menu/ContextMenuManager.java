package com.gurella.studio.editor.menu;

import com.gurella.engine.event.EventService;
import com.gurella.studio.editor.subscription.EditorContextMenuContributor;
import com.gurella.studio.editor.subscription.EditorMouseListener;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;

public class ContextMenuManager implements EditorMouseListener, EditorPreCloseListener {
	private final int editorId;

	public ContextMenuManager(int editorId) {
		this.editorId = editorId;
		EventService.subscribe(editorId, this);
	}

	@Override
	public void onMouseMenu(float x, float y) {
		ContextMenuActions actions = new ContextMenuActions();
		EventService.post(editorId, EditorContextMenuContributor.class, c -> c.contribute(actions));
		actions.showMenu();
	}

	@Override
	public void onMouseSelection(float x, float y) {
	}

	@Override
	public void onEditorPreClose() {
		EventService.unsubscribe(editorId, this);
	}
}
