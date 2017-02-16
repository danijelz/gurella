package com.gurella.engine.base.serialization;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.gurella.engine.metatype.CopyContext;
import com.gurella.engine.metatype.MetaType;
import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.metatype.serialization.json.JsonInput;
import com.gurella.engine.metatype.serialization.json.JsonOutput;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.transform.TransformComponent;

public class SceneSerializationTest {
	public static void main(String[] args) {
		Scene scene = new Scene();
		scene.newNode("Node 1");
		scene.newNode("Node 2");
		SceneNode node3 = scene.newNode("Node 3");
		SceneNode node3_1 = node3.newChild("Node 3/1");
		TransformComponent component = node3_1.newComponent(TransformComponent.class);
		component.setTranslation(1, 1, 1);

		MetaType<Scene> metaType = MetaTypes.getMetaType(Scene.class);
		metaType.getProperties();

		JsonOutput output = new JsonOutput();
		String string = output.serialize(Scene.class, scene, null);
		System.out.println(new JsonReader().parse(string).prettyPrint(OutputType.minimal, 120));

		JsonInput input = new JsonInput();
		Scene deserialized = input.deserialize(null, Scene.class, string);
		System.out.println("deserialized: " + MetaTypes.isEqual(scene, deserialized));

		Scene duplicate = CopyContext.copyObject(scene);
		System.out.println("duplicate: " + MetaTypes.isEqual(scene, duplicate));

		String string1 = output.serialize(Scene.class, duplicate, scene);
		System.out.println(new JsonReader().parse(string1).prettyPrint(OutputType.minimal, 120));

		Scene deserialized1 = input.deserialize(null, Scene.class, string1, scene);
		System.out.println(MetaTypes.isEqual(scene, deserialized1));
	}
}
