package com.gurella.studio.editor.menu;

import java.util.LinkedHashSet;
import java.util.Set;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.gurella.engine.plugin.Plugin;
import com.gurella.engine.plugin.PluginListener;
import com.gurella.engine.plugin.Workbench;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.utils.GestureDetectorPlugin;
import com.gurella.studio.gdx.GdxContext;

public class ContextMenuManager implements EditorCloseListener, PluginListener {
	private final int editorId;
	private final Set<EditorContextMenuContributor> extensions = new LinkedHashSet<>();
	private final GestureDetectorPlugin gestureDetector = new GestureDetectorPlugin(new MenuTapListener());

	public ContextMenuManager(int editorId) {
		this.editorId = editorId;
		GdxContext.subscribe(editorId, editorId, this);
		Workbench.addListener(editorId, this);
		Workbench.activate(editorId, gestureDetector);
	}

	@Override
	public void activated(Plugin plugin) {
		if (plugin instanceof EditorContextMenuContributor) {
			EditorContextMenuContributor exstension = (EditorContextMenuContributor) plugin;
			extensions.add(exstension);
		}
	}

	@Override
	public void deactivated(Plugin plugin) {
		if (plugin instanceof EditorContextMenuContributor) {
			EditorContextMenuContributor exstension = (EditorContextMenuContributor) plugin;
			extensions.remove(exstension);
		}
	}

	@Override
	public void onEditorClose() {
		Workbench.deactivate(editorId, gestureDetector);
		Workbench.removeListener(editorId, this);
		GdxContext.unsubscribe(editorId, editorId, this);
	}

	private class MenuTapListener extends GestureAdapter {
		@Override
		public boolean tap(float x, float y, int count, int button) {
			if (count == 1 && button == Buttons.RIGHT) {
				ContextMenuActions actions = new ContextMenuActions();
				extensions.stream().forEachOrdered(c -> c.contribute(x, y, actions));
				actions.showMenu();
			}
			return false;
		}
	}
}
