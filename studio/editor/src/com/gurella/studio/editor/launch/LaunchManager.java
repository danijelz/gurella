package com.gurella.studio.editor.launch;

import static com.gurella.studio.launch.SceneLauncher.launch;

import org.eclipse.debug.core.ILaunchManager;

import com.gurella.engine.plugin.Workbench;
import com.gurella.studio.editor.SceneEditor;
import com.gurella.studio.editor.menu.ContextMenuActions;
import com.gurella.studio.editor.menu.EditorContextMenuContributor;
import com.gurella.studio.editor.subscription.EditorCloseListener;

public class LaunchManager implements EditorContextMenuContributor, EditorCloseListener {
	private static final String section = "run";
	private final SceneEditor editor;

	public LaunchManager(SceneEditor editor) {
		this.editor = editor;
		Workbench.activate(this);
	}

	@Override
	public void contribute(float x, float y, ContextMenuActions actions) {
		actions.addSection(section);
		actions.addAction("", section, "Run", -800, () -> launch(editor, ILaunchManager.RUN_MODE));
		actions.addAction("", section, "Debug", -700, () -> launch(editor, ILaunchManager.DEBUG_MODE));
	}

	@Override
	public void onEditorClose() {
		Workbench.deactivate(this);
	}
}