package com.gurella.engine.base.metatype;

import com.gurella.engine.metatype.MetaType;
import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.transform.TransformComponent;

public class MetaTypeTest {
	public static void main(String[] args) {
		MetaType<TransformComponent> metaType = MetaTypes.getMetaType(TransformComponent.class);
		System.out.println(MetaTypes.getDiagnostic(metaType));
		
		System.out.println("\n\n");
		System.out.println(MetaTypes.getDiagnostic(MetaTypes.getMetaType(Scene.class)));
	}
}
