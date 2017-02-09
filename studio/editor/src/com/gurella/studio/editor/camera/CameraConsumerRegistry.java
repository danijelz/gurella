package com.gurella.studio.editor.camera;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.graphics.Camera;
import com.gurella.engine.utils.plugin.Plugin;
import com.gurella.engine.utils.plugin.PluginListener;

class CameraConsumerRegistry implements PluginListener {
	private final Set<CameraConsumer> consumers = new HashSet<>();

	private Camera camera;

	@Override
	public void activated(Plugin plugin) {
		if (plugin instanceof CameraConsumer) {
			CameraConsumer exstension = (CameraConsumer) plugin;
			if (consumers.add(exstension)) {
				exstension.setCamera(camera);
			}
		}
	}

	@Override
	public void deactivated(Plugin plugin) {
		if (plugin instanceof CameraConsumer) {
			CameraConsumer exstension = (CameraConsumer) plugin;
			if (consumers.remove(exstension)) {
				exstension.setCamera(null);
			}
		}
	}

	void updateCamera(Camera camera) {
		this.camera = camera;
		consumers.forEach(e -> e.setCamera(camera));
	}
}
