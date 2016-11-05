package com.gurella.studio.editor.menu;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.gurella.engine.event.EventService;
import com.gurella.engine.plugin.Workbench;
import com.gurella.studio.editor.subscription.EditorContextMenuContributor;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;
import com.gurella.studio.editor.utils.GestureDetectorPlugin;

public class ContextMenuManager implements EditorPreCloseListener {
	private final int editorId;
	private final GestureDetectorPlugin gestureDetector = new GestureDetectorPlugin(new MenuTapListener());

	public ContextMenuManager(int editorId) {
		this.editorId = editorId;
		EventService.subscribe(editorId, this);
		Workbench.activate(gestureDetector);
	}

	@Override
	public void onEditorPreClose() {
		Workbench.deactivate(gestureDetector);
		EventService.unsubscribe(editorId, this);
	}

	private class MenuTapListener extends GestureAdapter {
		@Override
		public boolean tap(float x, float y, int count, int button) {
			if (count == 1 && button == Buttons.RIGHT) {
				ContextMenuActions actions = new ContextMenuActions();
				EventService.post(editorId, EditorContextMenuContributor.class, c -> c.contribute(actions));
				actions.showMenu();
			}
			return false;
		}
	}
}
