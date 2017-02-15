package com.gurella.studio.editor.utils;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.gurella.engine.serialization.json.JsonOutput;

public class PrettyPrintSerializer {
	private PrettyPrintSerializer() {
	}

	public static <T> String serialize(Class<T> expectedType, T rootObject) {
		return serialize(expectedType, null, rootObject);
	}

	public static <T> String serialize(Class<T> expectedType, Object template, T rootObject) {
		JsonOutput output = new JsonOutput();
		String serialized = output.serialize(expectedType, rootObject, template);
		return new JsonReader().parse(serialized).prettyPrint(OutputType.minimal, 120);
	}
}
