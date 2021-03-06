package com.gurella.studio.editor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.gurella.engine.scene.Scene;
import com.gurella.engine.utils.plugin.Plugin;
import com.gurella.engine.utils.plugin.PluginListener;
import com.gurella.engine.utils.plugin.Workbench;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;
import com.gurella.studio.gdx.GdxContext;

//TODO merge with SceneEditorContext
class SceneProvider implements PluginListener, EditorPreCloseListener, EditorCloseListener {
	private final int editorId;
	private final Set<SceneConsumer> consumers = new HashSet<>();

	private Scene scene;

	SceneProvider(int editorId) {
		this.editorId = editorId;
		Workbench.addListener(editorId, this);
		GdxContext.subscribe(editorId, editorId, this);
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
		Optional.ofNullable(scene).ifPresent(s -> GdxContext.unload(editorId, s));
	}

	@Override
	public void onEditorClose() {
		Workbench.removeListener(editorId, this);
		GdxContext.unsubscribe(editorId, editorId, this);
	}
}
