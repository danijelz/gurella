package com.gurella.studio.editor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.gurella.engine.asset.AssetService;
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
	private final Set<SceneConsumer> consumers = new HashSet<>();

	private Scene scene;

	SceneProvider(int editorId) {
		this.editorId = editorId;
		Workbench.addListener(editorId, this);
		EventService.subscribe(editorId, this);
	}

	@Override
	public void activated(Plugin plugin) {
		if (plugin instanceof SceneConsumer) {
			SceneConsumer exstension = (SceneConsumer) plugin;
			if (consumers.add(exstension) && scene != null) {
				exstension.setScene(scene);
			}
		}
	}

	@Override
	public void deactivated(Plugin plugin) {
		if (plugin instanceof SceneConsumer) {
			SceneConsumer exstension = (SceneConsumer) plugin;
			consumers.remove(exstension);
		}
	}

	void setScene(Scene scene) {
		this.scene = scene;
		scene.start();
		consumers.forEach(e -> e.setScene(scene));
	}

	@Override
	public void onEditorPreClose() {
		Optional.ofNullable(scene).ifPresent(s -> AssetService.unload(s));
	}

	@Override
	public void onEditorClose() {
		Workbench.removeListener(editorId, this);
		EventService.unsubscribe(editorId, this);
	}
}
