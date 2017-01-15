package com.gurella.studio.editor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.gurella.engine.event.EventService;
import com.gurella.engine.plugin.Plugin;
import com.gurella.engine.plugin.PluginListener;
import com.gurella.engine.plugin.Workbench;
import com.gurella.engine.scene.Scene;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;

//TODO merge with SceneEditorContext
class SceneProvider implements PluginListener, EditorPreCloseListener, EditorCloseListener {
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
		if (plugin instanceof SceneProviderExtension) {
			SceneProviderExtension exstension = (SceneProviderExtension) plugin;
			if (extensions.add(exstension) && scene != null) {
				exstension.setScene(scene);
			}
		}
	}

	@Override
	public void deactivated(Plugin plugin) {
		if (plugin instanceof SceneProviderExtension) {
			SceneProviderExtension exstension = (SceneProviderExtension) plugin;
			extensions.remove(exstension);
		}
	}

	void setScene(Scene scene) {
		this.scene = scene;
		scene.start();
		extensions.forEach(e -> e.setScene(scene));
	}

	@Override
	public void onEditorPreClose() {
		Optional.ofNullable(scene).ifPresent(s -> s.stop());
	}

	@Override
	public void onEditorClose() {
		Workbench.removeListener(this);
		EventService.unsubscribe(editorId, this);
	}
}
