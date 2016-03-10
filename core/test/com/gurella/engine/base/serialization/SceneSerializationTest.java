package com.gurella.engine.base.serialization;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.serialization.json.JsonInput;
import com.gurella.engine.base.serialization.json.JsonOutput;
import com.gurella.engine.scene.Scene;

public class SceneSerializationTest {
	public static void main(String[] args) {
		Scene scene = new Scene();
		scene.newNode("Node 1");
		scene.newNode("Node 2");
		scene.newNode("Node 3");
		
		Model<Scene> model = Models.getModel(Scene.class);
		model.getProperties();

		JsonOutput output = new JsonOutput();
		String string = output.serialize(Scene.class, scene);

		System.out.println(new JsonReader().parse(string).prettyPrint(OutputType.minimal, 120));

		JsonInput input = new JsonInput();
		Scene deserialized = input.deserialize(Scene.class, string);
		System.out.println(Models.isEqual(scene, deserialized));
	}
}
