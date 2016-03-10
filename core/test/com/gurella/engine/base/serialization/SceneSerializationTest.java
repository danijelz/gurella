package com.gurella.engine.base.serialization;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.gurella.engine.base.model.CopyContext;
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
		System.out.println("deserialized: " + Models.isEqual(scene, deserialized));
		
		Scene duplicate = new CopyContext().copy(scene);
		System.out.println("duplicate: " + Models.isEqual(scene, duplicate));
		
		Scene copied = new CopyContext().copyProperties(scene, new Scene());
		System.out.println("copied: " + Models.isEqual(scene, copied));
		
		String string1 = output.serialize(Scene.class, scene, duplicate);
		System.out.println(new JsonReader().parse(string1).prettyPrint(OutputType.minimal, 120));
		
		Scene deserialized1 = input.deserialize(Scene.class, string1, scene);
		System.out.println(Models.isEqual(scene, deserialized1));
	}
}
