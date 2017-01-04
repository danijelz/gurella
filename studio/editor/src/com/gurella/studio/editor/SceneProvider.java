package com.gurella.studio.editor;

import java.util.HashSet;
import java.util.Set;

import com.gurella.engine.event.EventService;
import com.gurella.engine.plugin.Plugin;
import com.gurella.engine.plugin.PluginListener;
import com.gurella.engine.plugin.Workbench;
import com.gurella.engine.scene.Scene;
import com.gurella.studio.editor.subscription.EditorCloseListener;

public class SceneProvider implements PluginListener, EditorCloseListener {
	private final int editorId;
	private final Set<SceneProviderExtension> extensions = new HashSet<>();

	private Scene scene;

	SceneProvider(int editorId) {
		this.editorId = editorId;
		Workbench.addListener(this);
		EventService.subscribe(editorId, this);
	}

	@Override
	public void activated(Plugin plugin) {
		if (scene == null) {
			return;
		}

		if (plugin instanceof SceneProviderExtension) {
			SceneProviderExtension exstension = (SceneProviderExtension) plugin;
			if (extensions.add(exstension)) {
				exstension.setScene(scene);
			}
		}
	}

	@Override
	public void deactivated(Plugin plugin) {
		if (scene == null) {
			return;
		}

		if (plugin instanceof SceneProviderExtension) {
			SceneProviderExtension exstension = (SceneProviderExtension) plugin;
			if (extensions.remove(exstension)) {
				exstension.setScene(null);
			}
		}
	}

	void setScene(Scene scene) {
		this.scene = scene;
		extensions.forEach(e -> e.setScene(scene));
	}

	@Override
	public void onEditorClose() {
		Workbench.removeListener(this);
		EventService.unsubscribe(editorId, this);
	}
}
