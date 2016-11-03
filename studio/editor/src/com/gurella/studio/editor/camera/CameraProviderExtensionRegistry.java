package com.gurella.studio.editor.camera;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.graphics.Camera;
import com.gurella.engine.plugin.Plugin;
import com.gurella.engine.plugin.PluginListener;

public class CameraProviderExtensionRegistry implements PluginListener, CameraProvider {
	private final CameraManager cameraManager;
	private final Set<CameraProviderExtension> extensions = new HashSet<>();

	CameraProviderExtensionRegistry(CameraManager cameraManager) {
		this.cameraManager = cameraManager;
	}

	@Override
	public void activated(Plugin plugin) {
		if (plugin instanceof CameraProviderExtension) {
			CameraProviderExtension exstension = (CameraProviderExtension) plugin;
			if (extensions.add(exstension)) {
				exstension.setCameraProvider(this);
			}
		}
	}

	@Override
	public void deactivated(Plugin plugin) {
		if (plugin instanceof CameraProviderExtension) {
			CameraProviderExtension exstension = (CameraProviderExtension) plugin;
			if (extensions.remove(exstension)) {
				exstension.setCameraProvider(null);
			}
		}
	}

	@Override
	public Camera getCamera() {
		return cameraManager.getCamera();
	}
}
