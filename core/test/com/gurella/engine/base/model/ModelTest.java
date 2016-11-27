package com.gurella.engine.base.model;

import com.gurella.engine.metatype.Model;
import com.gurella.engine.metatype.Models;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.transform.TransformComponent;

public class ModelTest {
	public static void main(String[] args) {
		Model<TransformComponent> model = Models.getModel(TransformComponent.class);
		System.out.println(Models.getDiagnostic(model));
		
		System.out.println("\n\n");
		System.out.println(Models.getDiagnostic(Models.getModel(Scene.class)));
	}
}
