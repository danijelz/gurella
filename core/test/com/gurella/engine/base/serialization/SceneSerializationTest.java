package com.gurella.engine.base.serialization;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.gurella.engine.base.model.CopyContext;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.serialization.json.JsonInput;
import com.gurella.engine.base.serialization.json.JsonOutput;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.transform.TransformComponent;

public class SceneSerializationTest {
	public static void main(String[] args) {
		Scene scene = new Scene();
		scene.newNode("Node 1");
		scene.newNode("Node 2");
		SceneNode2 node3 = scene.newNode("Node 3");
		SceneNode2 node3_1 = node3.newChild("Node 3/1");
		TransformComponent component = node3_1.newComponent(TransformComponent.class);
		component.setTranslation(1, 1, 1);

		Model<Scene> model = Models.getModel(Scene.class);
		model.getProperties();

		FileHandle file = new FileHandle("");
		JsonOutput output = new JsonOutput();
		String string = output.serialize(file, Scene.class, scene);
		System.out.println(new JsonReader().parse(string).prettyPrint(OutputType.minimal, 120));

		JsonInput input = new JsonInput();
		Scene deserialized = input.deserialize(Scene.class, string);
		System.out.println("deserialized: " + Models.isEqual(scene, deserialized));

		Scene duplicate = new CopyContext().copy(scene);
		System.out.println("duplicate: " + Models.isEqual(scene, duplicate));

		Scene copied = new CopyContext().copyProperties(scene, new Scene());
		System.out.println("copied: " + Models.isEqual(scene, copied));

		String string1 = output.serialize(file, Scene.class, scene, duplicate);
		System.out.println(new JsonReader().parse(string1).prettyPrint(OutputType.minimal, 120));

		Scene deserialized1 = input.deserialize(Scene.class, string1, scene);
		System.out.println(Models.isEqual(scene, deserialized1));
	}
}
