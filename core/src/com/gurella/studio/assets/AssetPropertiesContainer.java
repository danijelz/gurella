package com.gurella.studio.assets;

import com.badlogic.gdx.graphics.Texture;
import com.gurella.engine.resource.AssetResourceDescriptor;
import com.gurella.engine.scene.Scene;
import com.gurella.studio.inspector.InspectorPropertiesContainer;
import com.kotcrab.vis.ui.widget.VisTable;

public class AssetPropertiesContainer  extends VisTable implements InspectorPropertiesContainer {
	private Scene scene;
	private AssetResourceDescriptor<Texture> descriptor;

	public AssetPropertiesContainer(Scene scene, AssetResourceDescriptor<Texture> descriptor) {
		this.scene = scene;
		this.descriptor = descriptor;
		add("Asset");
	}
	
	public Scene getScene() {
		return scene;
	}
	
	public AssetResourceDescriptor<Texture> getDescriptor() {
		return descriptor;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}
}
