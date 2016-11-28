package com.gurella.engine.test;

import com.gurella.engine.editor.property.PropertyEditorDescriptor;
import com.gurella.engine.metatype.PropertyDescriptor;
import com.gurella.engine.scene.SceneNodeComponent;

public class TestEditorComponent extends SceneNodeComponent {
	public int testInt;
	public int intGroup;
	
	public TestObj testObj = new TestObj();
	
	public static class TestObj {
		@PropertyEditorDescriptor(group = "TestObjGroup")
		public int int2;
		@PropertyEditorDescriptor(group = "TestObjGroup3")
		public int int3;
	}
}
