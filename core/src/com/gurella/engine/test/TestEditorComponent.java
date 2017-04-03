package com.gurella.engine.test;

import com.badlogic.gdx.graphics.Texture;
import com.gurella.engine.asset.AssetReference;
import com.gurella.engine.editor.property.PropertyEditorDescriptor;
import com.gurella.engine.scene.SceneNodeComponent;

public class TestEditorComponent extends SceneNodeComponent {
	public int testInt;
	public int intGroup;
	
	public TestObj testObj = new TestObj();
	
	public AssetReference<Texture> textureRef = new AssetReference<Texture>(Texture.class);
	
	public static class TestObj {
		@PropertyEditorDescriptor(group = "TestObjGroup")
		public int int2;
		@PropertyEditorDescriptor(group = "TestObjGroup3")
		public int int3;
	}
}
